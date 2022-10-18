package me.qiujun.arbitrage.adapter.binance.exception;

import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceErrorResponse;

public class BinanceException extends RuntimeException {

    private BinanceErrorResponse errorResponse;

    public BinanceException() {
    }

    public BinanceException(String message) {
        super(message);
    }

    public BinanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BinanceException(Throwable cause) {
        super(cause);
    }

    public BinanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BinanceException(BinanceErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public BinanceErrorResponse getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String getMessage() {
        if (errorResponse != null) {
            return String.format("%s - %s", errorResponse.getCode(), errorResponse.getMsg());
        }
        return super.getMessage();
    }

}
