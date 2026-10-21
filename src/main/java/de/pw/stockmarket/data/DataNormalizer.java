package de.pw.stockmarket.data;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;

public class DataNormalizer {

    public static DataNormalization normalizeData(DataSet trainData, DataSet evaluateData) {
        DataNormalization normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fit(trainData);

        normalizer.transform(trainData);
       // normalizer.transform(evaluateData);
        return normalizer;
    }
}
