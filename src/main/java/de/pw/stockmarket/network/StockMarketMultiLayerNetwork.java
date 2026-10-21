package de.pw.stockmarket.network;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class StockMarketMultiLayerNetwork {

    public static MultiLayerNetwork createMultiLayerNetwork(int numFeatures, int numOutputs) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .l2(0.0001)
                .updater(new Adam(0.001)) // Etwas niedrigere Lernrate für stabilere Konvergenz bei tiefen Netzen
                .list()
                // SCHICHT 0: Erste LSTM Schicht (Extrahiert kurzfristige Muster aus den Input-Features)
                .layer(0, new LSTM.Builder()
                        .nIn(numFeatures) // Eingang: 3 Features (Kurs, RSI, SMA, SMA-CrossOver-Abstand, Bollinger Band Position)
                        .nOut(64) // Ausgang: 64 versteckte Einheiten
                        .activation(Activation.TANH)
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(1.0)
                        .dropOut(0.15) // 15% Dropout gegen Overfitting
                        .build())
                // SCHICHT 1: Zweite LSTM Schicht (Lernt abstraktere/längerfristige Trends aus Schicht 0)
                .layer(1, new LSTM.Builder()
                        .nIn(64)           // Eingang: MUSS exakt dem nOut von Layer 0 entsprechen!
                        .nOut(32)          // Reduktion auf 32 Einheiten (Flaschenhals-Prinzip)
                        .activation(Activation.TANH)
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(1.0)
                        .dropOut(0.1)      // Zusätzlicher Schutz auf der zweiten Ebene
                        .build())
                // SCHICHT 2: RnnOutput Schicht (Klassifikation auf Basis des aggregierten Wissens)
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .nIn(32) // Eingang: MUSS exakt dem nOut von Layer 1 entsprechen!
                        .nOut(numOutputs) // Ausgang: 2 Klassen (Fällt/Steigt)
                        .activation(Activation.SOFTMAX)  // Prozentuale Wahrscheinlichkeiten
                        .build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        return net;
    }
}
