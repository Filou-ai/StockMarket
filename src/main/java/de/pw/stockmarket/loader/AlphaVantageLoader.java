package de.pw.stockmarket.loader;

import de.pw.stockmarket.live.ETickerSymbol;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlphaVantageLoader extends AbstractDataLoader{

    // Format von Alpha Vantage: yyyy-MM-dd
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static String API_TOKEN;
    private static String API_URL;

    public AlphaVantageLoader(String url, String token) {
        super();
        API_URL = url;
        API_TOKEN = token;
    }

    public BarSeries loadStockData(ETickerSymbol symbol, long timespan) throws Exception {
        // datatype=csv liefert ein kompaktes, leicht zu parsendes Format
        String urlString = API_URL
                + "&symbol=" + symbol.getTicker()
                + "&apikey=" + API_TOKEN
                + "&datatype=csv";

        URL url = URI.create(urlString).toURL();
        List<String> csvLines = getCsvLines(url);

        // ta4j Serie initialisieren (Präziser DecimalNum-Typ nach mdeverdelhan)
        BarSeries series = new BaseBarSeriesBuilder()
                .withName(symbol.name())
                .withNumTypeOf(DecimalNum.class)
                .build();

        // WICHTIG: Liste rückwärts durchlaufen, damit die ältesten Daten zuerst in ta4j landen
        String[] parts;
        for(int i = csvLines.size() - 1; i >= 0; i--) {
            parts = csvLines.get(i).split(",");
            if(parts.length != 6) {
                continue;
            }

            // CSV-Spalten: timestamp, open, high, low, close, volume
            LocalDate localDate = LocalDate.parse(parts[0], DATE_FORMATTER);
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

            double open = Double.parseDouble(parts[1]);
            double high = Double.parseDouble(parts[2]);
            double low = Double.parseDouble(parts[3]);
            double close = Double.parseDouble(parts[4]);
            double volume = Double.parseDouble(parts[5]);

            // Datenpunkt sicher hinzufügen
            series.addBar(zonedDateTime, open, high, low, close, volume);
        }

        return series;
    }

    private static List<String> getCsvLines(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if(conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP-Fehler von Alpha Vantage: " + conn.getResponseCode());
        }

        List<String> csvLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            // Erste Zeile ist der Header (timestamp, open, high, low, close, volume)
            String header = br.readLine();

            if(header == null || header.contains("Note") || header.contains("Error")) {
                throw new RuntimeException("API-Limit erreicht oder ungültiges Symbol. API-Antwort: " + header);
            }
            while ((line = br.readLine()) != null) {
                csvLines.add(line);
            }

        }
        return csvLines;
    }
}
