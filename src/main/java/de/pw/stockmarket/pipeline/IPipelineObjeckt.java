package de.pw.stockmarket.pipeline;

public interface IPipelineObjeckt {
    public void initialize(PipelineBasket basket);
    public PipelineBasket execute();
}
