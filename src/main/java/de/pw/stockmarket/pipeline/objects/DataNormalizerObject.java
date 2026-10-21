package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.data.DataNormalizer;
import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;

public class DataNormalizerObject extends AbstractPipelineObject implements IPipelineObjeckt {
    @Override
    public void initialize(PipelineBasket basket) {
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        System.out.println("3. Normalisiere Daten auf den Wertebereich [0.0, 1.0]...");
        DataNormalization normalizer = DataNormalizer.normalizeData(getBasket().getTrainDataSet(), getBasket().getEvaluateDataSet());
        getBasket().setNormalizer(normalizer);
        System.out.println("-> Normalisierung abgeschlosen");
        return getBasket();
    }
}
