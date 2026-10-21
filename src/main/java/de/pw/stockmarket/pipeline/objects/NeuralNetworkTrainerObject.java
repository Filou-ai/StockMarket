package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import de.pw.stockmarket.train.StockMarketTrainer;

public class NeuralNetworkTrainerObject extends AbstractPipelineObject implements IPipelineObjeckt {
    @Override
    public void initialize(PipelineBasket basket) {
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        System.out.println("5. Anlernen des Netzwerkes");
        StockMarketTrainer.trainNetwork(getBasket().getNetwork(),
                                        getBasket().getTrainDataSet(),
                                        getBasket().getTotalBars());
        System.out.println("Training beendet...");
        return getBasket();
    }
}
