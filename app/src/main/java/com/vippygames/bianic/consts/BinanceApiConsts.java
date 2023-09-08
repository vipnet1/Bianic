package com.vippygames.bianic.consts;

public abstract class BinanceApiConsts {
    public static final String MAIN_ENDPOINT = "https://api.binance.com";
    public static final String ACCOUNT_ENDPOINT = "/api/v3/account";
    public static final String TICKER_PRICE_ENDPOINT = "/api/v3/ticker/price";

    // take 30 seconds from timestamp, and give 30 seconds to complete request
    public static final String RECEIVE_WINDOW = "60000";
    public static final long SUBSTRUCTED_TIMESTAMP_FROM_REQUEST = 30000;

    public static final String API_KEY_HEADER_NAME = "X-MBX-APIKEY";

    public static final String USDT_SYMBOL = "USDT";
    public static final String BTC_SYMBOL = "BTC";
}
