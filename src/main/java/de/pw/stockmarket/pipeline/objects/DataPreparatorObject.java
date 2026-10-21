package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.data.DataPreparator;
import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import org.nd4j.linalg.dataset.DataSet;

public class DataPreparatorObject extends AbstractPipelineObject implements IPipelineObjeckt {
    @Override
    public void initialize(PipelineBasket basket) {
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        System.out.println("2. Berechne technische Indikatoren (RSI, SMA)...");
        int totalRecords = getBasket().getBarSeries().getEndIndex();
        int trainSize = (int) (totalRecords * 0.80);

        DataSet trainDataSet = DataPreparator.prepareTrainData(getBasket().getBarSeries().getSubSeries(0, trainSize));
        getBasket().setTrainDataSet(trainDataSet);
        getBasket().setTrainDataSetSize(getBasket().getBarSeries().getSubSeries(0, trainSize).getEndIndex());
        DataSet evaluateDataSet = DataPreparator.prepareEvaluateData(getBasket().getBarSeries().getSubSeries(trainSize + 1, totalRecords));
        getBasket().setEvaluateDataSet(evaluateDataSet);
        System.out.println("-> Berechnung abgeschlossen...");
        return getBasket();
    }
}
