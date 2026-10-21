package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.live.StockMarketForecast;
import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import org.nd4j.linalg.api.ndarray.INDArray;

public class StockMarketForeCastObject extends AbstractPipelineObject implements IPipelineObjeckt {
    @Override
    public void initialize(PipelineBasket basket) {
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        System.out.println("6. Berechne Live-Signal basierend auf den aktuellsten Marktindikatoren.");
        INDArray predictionOutput =  StockMarketForecast.foreCast(getBasket().getNetwork(),
                                                                  getBasket().getTrainDataSetSize(),
                                                                  getBasket().getNormalizer());
        getBasket().setPredictionOutput(predictionOutput);
        System.out.println("-> ForeCast abgeschlossen");
        return getBasket();
    }
}
