package de.pw.stockmarket.data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CombineIndicator;
import org.ta4j.core.indicators.helpers.DifferencePercentageIndicator;

public class DataPreparator {

    private static final int numFeatures = 5; // Features: Schlusskurs,
                                              //           RSI, SMA,
                                              //           SMA-CrossOver-Abstand
                                              //           Bollinger Band Position
    private static final int numOutputs = 2;  // Klassen: [fällt, steigt]

    private static ClosePriceIndicator closePrice;
    private static RSIIndicator rsi;
    private static SMAIndicator fastSma;
    private static SMAIndicator slowSma;
    private static CombineIndicator smaDistanceFeature;
    private static CombineIndicator bbandPositionFeature;

    public static DataSet prepareTrainData(BarSeries series) {
        closePrice = new ClosePriceIndicator(series);
        rsi = new RSIIndicator(closePrice, 14);
        fastSma = new SMAIndicator(closePrice, 20);
        slowSma = new SMAIndicator(closePrice, 50);

        // =====================================================
        // SMA-CrossOver-Abstand -> (SMA_20 - SMA_50 / SMA_50)
        // =====================================================
        CombineIndicator smaDiff = CombineIndicator.minus(fastSma, slowSma);
        smaDistanceFeature = CombineIndicator.divide(smaDiff, slowSma);

        // =============================================================
        // Bollinger Band Position -> (Close - Lower) / (Upper - Lower)
        // ==============================================================
        BollingerBandsMiddleIndicator middleBB = new BollingerBandsMiddleIndicator(fastSma);
        BollingerBandsLowerIndicator lowerBB = new BollingerBandsLowerIndicator(middleBB, closePrice);
        BollingerBandsUpperIndicator upperBB = new BollingerBandsUpperIndicator(middleBB, closePrice);

        CombineIndicator closeMinusLower = CombineIndicator.minus(closePrice, lowerBB);
        CombineIndicator bandWidth = CombineIndicator.minus(upperBB, lowerBB);

        bbandPositionFeature = CombineIndicator.divide(closeMinusLower, bandWidth);

        int startIndex = 50;
        int trainTimesteps = series.getBarCount() - 50 - 1;

        if (trainTimesteps <= 0) {
            throw new IllegalArgumentException(String.format(
                    "ZUR WENIG DATEN! Deine BarSeries hat nur %d Bars. " +
                            "Abzüglich der Vorlaufzeit (%d) und des Folgetags (-1) bleiben %d Schritte übrig. " +
                            "Du benötigst für ein 30-Tage-Fenster mindestens %d Bars in der CSV/Datenbank!",
                    series.getBarCount(), startIndex, trainTimesteps, (startIndex + 30 + 1)
            ));
        }

        // DL4J 3D-Format für RNNs: [Batchsize, Features, Zeitachse/Timesteps]
        INDArray featuresMat = Nd4j.create(new int[]{1, numFeatures, trainTimesteps});
        INDArray labelMat = Nd4j.create(1, numOutputs, trainTimesteps);

        for(int i = 0; i < trainTimesteps; i++) {
            int realSeriesIndex = i + startIndex;
            // features befüllen
            Double[] returnArray = fillFeatureMap(realSeriesIndex, i, featuresMat, closePrice, rsi, fastSma, smaDistanceFeature,
                           bbandPositionFeature);
            double currentClose = returnArray[0];
            double nextClose = returnArray[1];
            if(nextClose > currentClose) {
                labelMat.putScalar(new int[]{0, 1, i}, 1.0); // Index 1: Steigt
                labelMat.putScalar(new int[]{0, 0, i}, 0.0); // Wichtig: Gegen-Index auf 0 setzen!
            } else {
                labelMat.putScalar(new int[]{0, 0, i}, 1.0); // Index 0: Fällt
                labelMat.putScalar(new int[]{0, 1, i}, 0.0); // Wichtig: Gegen-Index auf 0 setzen!
            }
        }
        return new DataSet(featuresMat, labelMat);
    }

    private static Double[] fillFeatureMap(int realSeriesIndex, int i, INDArray featuresMat,
                                       ClosePriceIndicator closePrice, RSIIndicator rsi, SMAIndicator fastSma,
                                       CombineIndicator smaDistanceFeature, CombineIndicator bbandPositionFeature) {
        Double[] returnArray = new Double[2];
        featuresMat.putScalar(new int[]{0, 0, i}, closePrice.getValue(realSeriesIndex).doubleValue());
        featuresMat.putScalar(new int[]{0, 1, i}, rsi.getValue(realSeriesIndex).doubleValue());
        featuresMat.putScalar(new int[]{0, 2, i}, fastSma.getValue(realSeriesIndex).doubleValue());
        featuresMat.putScalar(new int[]{0, 3, i}, smaDistanceFeature.getValue(realSeriesIndex).doubleValue());
        featuresMat.putScalar(new int[]{0, 4, i}, bbandPositionFeature.getValue(realSeriesIndex).doubleValue());
        // Labels befüllen: Steigt der Kurs am nächsten Handelstag (i+1)?
        returnArray[0] = closePrice.getValue(realSeriesIndex).doubleValue();
        returnArray[1] = closePrice.getValue(realSeriesIndex + 1).doubleValue();
        return returnArray;
    }

    public static DataSet prepareEvaluateData(BarSeries series) {
        ClosePriceIndicator evaluateClosePrice = new ClosePriceIndicator(series);
        RSIIndicator evaluateRsi = new RSIIndicator(evaluateClosePrice, 14);
        SMAIndicator evaluateFastSma = new SMAIndicator(evaluateClosePrice, 20);
        SMAIndicator evaluateSlowSma = new SMAIndicator(evaluateClosePrice, 50);

        // =====================================================
        // SMA-CrossOver-Abstand -> (SMA_20 - SMA_50 / SMA_50)
        // =====================================================
        CombineIndicator smaDiff = CombineIndicator.minus(evaluateFastSma, evaluateSlowSma);
        CombineIndicator evaluateSmaDistanceFeature = CombineIndicator.divide(smaDiff, evaluateSlowSma);

        // =============================================================
        // Bollinger Band Position -> (Close - Lower) / (Upper - Lower)
        // ==============================================================
        BollingerBandsMiddleIndicator middleBB = new BollingerBandsMiddleIndicator(evaluateFastSma);
        BollingerBandsLowerIndicator lowerBB = new BollingerBandsLowerIndicator(middleBB, evaluateClosePrice);
        BollingerBandsUpperIndicator upperBB = new BollingerBandsUpperIndicator(middleBB, evaluateClosePrice);

        CombineIndicator closeMinusLower = CombineIndicator.minus(evaluateClosePrice, lowerBB);
        CombineIndicator bandWidth = CombineIndicator.minus(upperBB, lowerBB);

        CombineIndicator evaluateBbandPositionFeature = CombineIndicator.divide(closeMinusLower, bandWidth);

        int trainTimesteps = series.getBarCount() - 1;

        // DL4J 3D-Format für RNNs: [Batchsize, Features, Zeitachse/Timesteps]
        INDArray featuresMat = Nd4j.create(new int[]{1, numFeatures, trainTimesteps});
        INDArray labelMat = Nd4j.create(new int[]{1, numOutputs, trainTimesteps});

        for(int i = 0; i < trainTimesteps; i++) {
            // features befüllen
            Double[] returnArray = fillFeatureMap(i, i, featuresMat, evaluateClosePrice, evaluateRsi, evaluateFastSma,
                    evaluateSmaDistanceFeature, evaluateBbandPositionFeature);
            double currentClose = returnArray[0];
            double nextClose = returnArray[1];
            if(nextClose > currentClose) {
                labelMat.putScalar(new int[]{0, 1, i}, 1.0); // Index 1: Steigt
            } else {
                labelMat.putScalar(new int[]{0, 0, i}, 1.0); // Index 0: Fällt
            }
        }
        return new DataSet(featuresMat, labelMat);
    }

    public static int getNumFeatures() {
        return numFeatures;
    }

    public static int getNumOutputs() {
        return numOutputs;
    }

    public static ClosePriceIndicator getClosePrice() {
        return closePrice;
    }

    public static RSIIndicator getRsi() {
        return rsi;
    }

    public static SMAIndicator getSma() {
        return fastSma;
    }

    public static SMAIndicator getSlowSma() {
        return slowSma;
    }

    public static CombineIndicator getSmaDistanceFeature() {
        return smaDistanceFeature;
    }

    public static CombineIndicator getBandPositionFeature() {
        return bbandPositionFeature;
    }
}
