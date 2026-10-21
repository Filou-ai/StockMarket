package de.pw.stockmarket;

import de.pw.stockmarket.live.ETickerSymbol;
import de.pw.stockmarket.loader.EDataLoader;
import de.pw.stockmarket.pipeline.PipelineBasket;
import de.pw.stockmarket.pipeline.StockMarketPipeline;
import de.pw.stockmarket.pipeline.objects.*;
import de.pw.stockmarket.properties.PropertiesLoader;

import java.util.Properties;


public class StockMarketMain {

    static void main() {

        PropertiesLoader loader = new PropertiesLoader();
        Properties loaderProps = loader.loadProperties();

        PipelineBasket basket = new PipelineBasket();
        basket.setSymbol(ETickerSymbol.AIR_LIQUIDE);
        basket.setDataLoader(EDataLoader.TIINGO);
        basket.setTimeSpan(100); // in Tagen

        StockMarketPipeline pipeline = new StockMarketPipeline(basket);
        pipeline.addPipelineObectAtLast(new DataLoaderObject(loaderProps));
        pipeline.addPipelineObectAtLast(new DataPreparatorObject());
        pipeline.addPipelineObectAtLast(new DataNormalizerObject());
        pipeline.addPipelineObectAtLast(new NeuralNetworkBuilder());
        pipeline.addPipelineObectAtLast(new NeuralNetworkTrainerObject());
        pipeline.addPipelineObectAtLast(new DataEvaluationObject());
        pipeline.addPipelineObectAtLast(new StockMarketForeCastObject());

        pipeline.execute();

        PipelineBasket resultBasket = pipeline.getPipelineBasket();

        printResults(resultBasket);
    }

    private static void printResults(PipelineBasket resultBasket) {
        // Ergebnisse aus der Softmax-Schicht auslesen
        double probabilityFallend = resultBasket.getPredictionOutput().getDouble(new int[]{0, 0, 0});
        double probabilitySteigend = resultBasket.getPredictionOutput().getDouble(new int[]{0, 1, 0});

        System.out.println("=== VORHERSEHUNG FUER DEN NAECHSTEN HANDELSTAG ===");
        System.out.format("Wahrscheinlichkeit, dass %s FAELLT: %.2f%%\n", resultBasket.getBarSeries().getName(),
                probabilityFallend * 100);
        System.out.format("Wahrscheinlichkeit, dass %s STEIGT: %.2f%%\n", resultBasket.getBarSeries().getName(),
                probabilitySteigend * 100);

        if (probabilitySteigend > 0.55) {
            System.out.println("HANDLUNGSEMPFEHLUNG: >> KAUFEN (UP-Trend prognostiziert) <<");
        } else if (probabilityFallend > 0.55) {
            System.out.println("HANDLUNGSEMPFEHLUNG: >> VERKAUFEN / SHORT (DOWN-Trend prognostiziert) <<");
        } else {
            System.out.println("HANDLUNGSEMPFEHLUNG: >> NEUTRAL / SEITWAERTSBEWEGUNG <<");
        }
    }
}
