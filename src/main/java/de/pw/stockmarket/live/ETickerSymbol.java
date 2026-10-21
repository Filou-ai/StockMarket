package de.pw.stockmarket.live;

public enum ETickerSymbol {

    BASF("BFFAF"),
    MICROSOFT("MSFT"),
    WIENERBERGER("WBRBF"),
    AIR_LIQUIDE("AIQUF"),
    AIRBUS("EADSF");

    private final String ticker;

    ETickerSymbol(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return this.ticker;
    }
}
