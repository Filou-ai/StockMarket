package de.pw.stockmarket.pipeline;

import java.util.LinkedList;
import java.util.List;

public class StockMarketPipeline {

    private final List<IPipelineObjeckt> pipelineObjects;
    private PipelineBasket currentBasket;

    public StockMarketPipeline(PipelineBasket basket) {
        pipelineObjects = new LinkedList<>();
        this.currentBasket = basket;
    }

    public void execute() {
        pipelineObjects.forEach(obj -> {
            obj.initialize(currentBasket);
            currentBasket = obj.execute();
        });
    }

    public void addPipelineObectAtLast(IPipelineObjeckt pipelineObject) {
        this.pipelineObjects.addLast(pipelineObject);
    }

    public PipelineBasket getPipelineBasket() {
        return currentBasket;
    }
}
