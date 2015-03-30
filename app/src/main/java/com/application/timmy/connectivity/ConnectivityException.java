package com.application.timmy.connectivity;

import java.net.UnknownHostException;

import org.json.JSONException;

public class ConnectivityException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 6876657465399309566L;
    BEResponseCode beResponseCode;

    public ConnectivityException() {
        super();
        beResponseCode = BEResponseCode.UNKNOWN_ERROR;
    }

    public ConnectivityException(Throwable throwable) {
        super(throwable);
        if (throwable instanceof UnknownHostException) {
            beResponseCode = BEResponseCode.NOT_FOUND;
            return;
        }
        if (throwable instanceof JSONException) {
            beResponseCode = BEResponseCode.PARSE_RESPONSE_ERROR;
            return;
        }
        beResponseCode = BEResponseCode.UNKNOWN_ERROR;
    }

    public ConnectivityException(BEResponseCode beResponseCode) {
        super();
        this.beResponseCode = beResponseCode;
    }

    @Override
    public String getLocalizedMessage() {
        if (beResponseCode != null) {
            String message = beResponseCode.getAppropriateErrorMessage();
            if (message != null) {
                return message;
            }
        }
        return super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        if (beResponseCode != null) {
            String message = beResponseCode.getAppropriateErrorMessage();
            if (message != null) {
                return message;
            }
        }
        return super.getMessage();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString() + "; BEResponseCode: " + beResponseCode;
    }

    public BEResponseCode getBEResponseCode() {
        return beResponseCode;
    }

}
