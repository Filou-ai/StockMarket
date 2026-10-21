package de.pw.stockmarket.pipeline;

public interface IPipelineObjeckt {
    void initialize(PipelineBasket basket);
    PipelineBasket execute();
}
