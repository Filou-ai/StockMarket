package de.pw.stockmarket.train;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

public class StockMarketTrainer {

    public static void trainNetwork(MultiLayerNetwork net, DataSet dataSet, int epochs) {
        for(int epoch = 1; epoch <= epochs; epoch++) {
            net.fit(dataSet);
            net.rnnClearPreviousState(); // Zellgedächtnis nach jeder Epoche zurücksetzen
            if(epoch % 5 == 0) {
                System.out.println("-> Epoche " + epoch + " / " + epochs + " abgeschlossen.");
            }
        }
    }
}
