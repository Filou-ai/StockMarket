package de.pw.stockmarket.pipeline;

import de.pw.stockmarket.live.ETickerSymbol;
import de.pw.stockmarket.loader.EDataLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.ta4j.core.BarSeries;

public class PipelineBasket {

    private ETickerSymbol symbol;
    private long timeSpan;
    private EDataLoader loader;
    private BarSeries barSeries;
    private DataSet trainDataSet;
    private DataSet evaluateDataSet;
    private int totalBars;
    private DataNormalization normalizer;
    private MultiLayerNetwork network;
    private INDArray predictionOutput;
    private int trainDataSetSize;

    public BarSeries getBarSeries() {
        return barSeries;
    }

    public void setBarSeries(BarSeries barSeries) {
        this.barSeries = barSeries;
    }

    public ETickerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(ETickerSymbol symbol) {
        this.symbol = symbol;
    }

    public int getTotalBars() {
        return totalBars;
    }

    public void setTotalBars(int totalBars) {
        this.totalBars = totalBars;
    }

    public EDataLoader getDataLoader() {
        return loader;
    }

    public void setDataLoader(EDataLoader loader) {
        this.loader = loader;
    }

    public DataSet getTrainDataSet() {
        return trainDataSet;
    }
    public void setTrainDataSet(DataSet dataSet) {
        this.trainDataSet = dataSet;
    }
    public DataNormalization getNormalizer() {
        return normalizer;
    }
    public void setNormalizer(DataNormalization normalizer) {
        this.normalizer = normalizer;
    }
    public MultiLayerNetwork getNetwork() {
        return network;
    }
    public void setNetwork(MultiLayerNetwork network) {
        this.network = network;
    }
    public INDArray getPredictionOutput() {
        return predictionOutput;
    }
    public void setPredictionOutput(INDArray predictionOutput) {
        this.predictionOutput = predictionOutput;
    }

    public long getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(long timeSpan) {
        this.timeSpan = timeSpan;
    }

    public DataSet getEvaluateDataSet() {
        return evaluateDataSet;
    }
    public void setEvaluateDataSet(DataSet evaluateDataSet) {
        this.evaluateDataSet = evaluateDataSet;
    }

    public int getTrainDataSetSize() {
        return trainDataSetSize;
    }

    public void setTrainDataSetSize(int trainDataSetSize) {
        this.trainDataSetSize = trainDataSetSize;
    }
}
