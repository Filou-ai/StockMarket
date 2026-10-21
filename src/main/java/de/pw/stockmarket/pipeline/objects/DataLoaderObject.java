package de.pw.stockmarket.pipeline.objects;

import de.pw.stockmarket.loader.AlphaVantageLoader;
import de.pw.stockmarket.loader.TiingoDownloader;
import de.pw.stockmarket.pipeline.IPipelineObjeckt;
import de.pw.stockmarket.pipeline.PipelineBasket;
import org.ta4j.core.BarSeries;

import java.util.Properties;

public class DataLoaderObject extends AbstractPipelineObject implements IPipelineObjeckt {

    private final Properties loaderProperties;

    public DataLoaderObject(Properties loaderProps) {
        super();
        loaderProperties = loaderProps;
    }

    @Override
    public void initialize(PipelineBasket basket) {
        System.out.println("INITIALIZING DATA LOADER OBJECT");
        setBasket(basket);
    }

    @Override
    public PipelineBasket execute() {
        System.out.println("1. Lade historische Daten für " + getBasket().getSymbol());
        BarSeries series = null;
        try {
            switch(getBasket().getDataLoader()) {
                case ALPHA_VANTAGE -> series = new AlphaVantageLoader(loaderProperties.getProperty("AlphaVantageLoader.url"),
                                                                      loaderProperties.getProperty("AlphaVantageLoader.token"))
                        .loadStockData(getBasket().getSymbol(),
                                                                                      getBasket().getTimeSpan());
                case TIINGO ->  series = new TiingoDownloader(loaderProperties.getProperty("Tiingo.url"),
                                                              loaderProperties.getProperty("Tiingo.token"))
                        .loadStockData(getBasket().getSymbol(),
                                                                              getBasket().getTimeSpan());
                default -> series = null;
            }
        }catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        assert series != null;
        getBasket().setBarSeries(series);
        getBasket().setTotalBars(series.getBarCount());
        System.out.println("-> Daten erfolgreich geladen. Datensaetze: " + getBasket().getTotalBars());
        return getBasket();
    }
}