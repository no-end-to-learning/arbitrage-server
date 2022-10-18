package me.qiujun.arbitrage.adapter.gate.exception;

import me.qiujun.arbitrage.adapter.gate.bean.response.GateErrorResponse;

public class GateException extends RuntimeException {

    private GateErrorResponse errorResponse;

    public GateException() {
    }

    public GateException(String message) {
        super(message);
    }

    public GateException(String message, Throwable cause) {
        super(message, cause);
    }

    public GateException(Throwable cause) {
        super(cause);
    }

    public GateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GateException(GateErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public GateErrorResponse getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String getMessage() {
        if (errorResponse != null) {
            return String.format("%s - %s", errorResponse.getLabel(), errorResponse.getMessage());
        }
        return super.getMessage();
    }

}
