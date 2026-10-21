package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.data.DataPreparator;
import de.pw.stockmarket.network.StockMarketMultiLayerNetwork;
import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class NeuralNetworkBuilder extends AbstractPipelineObject implements IPipelineObjeckt {

    @Override
    public void initialize(PipelineBasket basket) {
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        System.out.println("4. Konfiguriere das LSTM Recurrent Neural Network...");
        MultiLayerNetwork network = StockMarketMultiLayerNetwork.createMultiLayerNetwork(
                DataPreparator.getNumFeatures(),
                DataPreparator.getNumOutputs());
        getBasket().setNetwork(network);
        System.out.println("-> Netzwerk konfiguriert...");
        return getBasket();
    }
}
