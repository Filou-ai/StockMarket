package de.pw.stockmarket.loader;

import de.pw.stockmarket.live.ETickerSymbol;
import org.ta4j.core.BarSeries;

public abstract class AbstractDataLoader {

    public abstract BarSeries loadStockData(ETickerSymbol symbol, long timespan) throws Exception;
}
