package de.pw.stockmarket.live;

import de.pw.stockmarket.data.DataPreparator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.factory.Nd4j;

public class StockMarketForecast {

    public static INDArray foreCast(MultiLayerNetwork net,
                                int lastIndex,
                                DataNormalization normalizer) {
        INDArray liveFeatures = Nd4j.create(1, DataPreparator.getNumFeatures(), 1); // 1 einzelner Zeitschritt

        liveFeatures.putScalar(new int[]{0, 0, 0}, DataPreparator.getClosePrice().getValue(lastIndex).doubleValue());
        liveFeatures.putScalar(new int[]{0, 1, 0}, DataPreparator.getRsi().getValue(lastIndex).doubleValue());
        liveFeatures.putScalar(new int[]{0, 2, 0}, DataPreparator.getSma().getValue(lastIndex).doubleValue());
        liveFeatures.putScalar(new int[]{0, 3, 0}, DataPreparator.getSmaDistanceFeature().getValue(lastIndex).doubleValue());
        liveFeatures.putScalar(new int[]{0, 4, 0}, DataPreparator.getBandPositionFeature().getValue(lastIndex).doubleValue());
        // Extrem wichtig: Live-Daten müssen exakt wie die Trainigsdaten skaliert werden
        normalizer.transform(liveFeatures);

        // Vorhersage ausführen
        net.rnnClearPreviousState();

        return net.output(liveFeatures);
    }
}
