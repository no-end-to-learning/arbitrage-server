package me.qiujun.arbitrage.adapter.bybit.exception;

import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitBaseResponse;

public class BybitException extends RuntimeException {

    private BybitBaseResponse<?> errorResponse;

    public BybitException() {
    }

    public BybitException(String message) {
        super(message);
    }

    public BybitException(String message, Throwable cause) {
        super(message, cause);
    }

    public BybitException(Throwable cause) {
        super(cause);
    }

    public BybitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BybitException(BybitBaseResponse<?> errorResponse) {
        this.errorResponse = errorResponse;
    }

    public BybitBaseResponse<?> getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String getMessage() {
        if (errorResponse != null) {
            return String.format("%s - %s", errorResponse.getRetCode(), errorResponse.getRetMsg());
        }
        return super.getMessage();
    }

}
