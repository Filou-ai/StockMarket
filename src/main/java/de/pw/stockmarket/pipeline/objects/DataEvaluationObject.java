package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class DataEvaluationObject extends AbstractPipelineObject implements IPipelineObjeckt {
    @Override
    public void initialize(PipelineBasket basket) {
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        MultiLayerNetwork net = getBasket().getNetwork();
        DataSet evaluateDataSet = getBasket().getEvaluateDataSet();
        long sequenceLength = evaluateDataSet.getFeatures().size(2);

        INDArray features = evaluateDataSet.getFeatures();
        INDArray labels = evaluateDataSet.getLabels();

        INDArray predicted = net.output(features);

        long lastTimeStepIndex = sequenceLength - 1;

        INDArray finalStepLabels = labels.get(NDArrayIndex.all(),
                                              NDArrayIndex.all(),
                                              NDArrayIndex.point(lastTimeStepIndex));
        INDArray finalStepPredicted = predicted.get(NDArrayIndex.all(),
                                                    NDArrayIndex.all(),
                                                    NDArrayIndex.point(lastTimeStepIndex));

        Evaluation eval = new Evaluation(2);
        eval.eval(finalStepLabels, finalStepPredicted);

        System.out.println("=== LSTM TRADING-PERFORMANCE EVALUATION ===");
        System.out.println(eval.stats());

        // Optional: Direkt auf einzelne Metriken zugreifen
        System.out.printf("Trefferquote (Accuracy): %.2f%%%n", eval.accuracy() * 100);
        System.out.printf("Signal-Verlässlichkeit (Precision für Klasse 'Steigt'): %.2f%%%n", eval.precision(1) * 100);
        System.out.printf("Signal-Trefferquote (Recall für Klasse 'Steigt'): %.2f%%%n", eval.recall(1) * 100);

        return getBasket();
    }
}
