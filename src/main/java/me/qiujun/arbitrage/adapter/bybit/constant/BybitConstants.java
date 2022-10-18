package me.qiujun.arbitrage.adapter.bybit.constant;

public class BybitConstants {

    public final static String HEADER_TIMESTAMP = "X-BAPI-TIMESTAMP";

    public final static String HEADER_API_KEY = "X-BAPI-API-KEY";

    public final static String HEADER_SIGNATURE = "X-BAPI-SIGN";

    public final static String HEADER_SIGNATURE_TYPE = "X-BAPI-SIGN-TYPE";

    public final static String HEADER_SIGNATURE_TYPE_VALUE = "2";

    public final static String HEADER_RECEIVE_WINDOW = "X-BAPI-RECV-WINDOW";

    public final static String HEADER_RECEIVE_WINDOW_VALUE = "5000";

    public final static String HEADER_SIGNATURE_REQUIRED = HEADER_SIGNATURE + ": #";

}
