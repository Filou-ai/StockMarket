package de.pw.stockmarket.loader;

import de.pw.stockmarket.live.ETickerSymbol;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;
import top.cptl.tiingo4j.apis.TiingoApi;
import top.cptl.tiingo4j.enums.RESAMPLE_FREQUENCY;
import top.cptl.tiingo4j.models.Price;
import top.cptl.tiingo4j.requestParameters.PriceParameters;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class TiingoDownloader extends AbstractDataLoader {

    private static String API_TOKEN;
    private static String API_URL;

    public TiingoDownloader(String url, String token) {
        super();
        API_TOKEN = token;
        API_URL = url;
    }

    @Override
    public BarSeries loadStockData(ETickerSymbol symbol, long timesSpan) throws Exception {
        TiingoApi tiingo = new TiingoApi(API_TOKEN);

        LocalDate today = LocalDate.now();
        LocalDate ago = LocalDate.now().minusDays(timesSpan);

        PriceParameters parameters = new PriceParameters()
                .setStartDate(ago.toString())
                .setEndDate(today.toString())
                .setResampleFrequency(RESAMPLE_FREQUENCY.DAILY);

        List<Price> prices = tiingo.getPrices(symbol.getTicker(), parameters);

        BarSeries series = new BaseBarSeriesBuilder()
                .withName(symbol.name())
                .withNumTypeOf(DecimalNum.class)
                .build();

        prices.subList(0, prices.size() - 1) .forEach(price -> {
            Instant instantDirect = Instant.parse(price.getDate());
            ZonedDateTime zonedDateTime = instantDirect.atZone(ZoneId.systemDefault());

            double open = price.getOpen();
            double high = price.getHigh();
            double low  = price.getLow();
            double close = price.getLow();
            double volume = price.getVolume();

            series.addBar(zonedDateTime, open, high, low, close, volume);
        });

        return series;
    }
}
