package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.pipeline.PipelineBasket;

public abstract class AbstractPipelineObject {

    private PipelineBasket basket;

    public PipelineBasket getBasket() {
        return basket;
    }

    public void setBasket(PipelineBasket basket) {
        this.basket = basket;
    }
}
