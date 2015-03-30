package com.application.timmy.connectivity;

public class BEResponse {

    private BEResponseCode statusCode;
    private String response;

    public BEResponse(BEResponseCode statusCode, String responseString) {
        super();
        this.statusCode = statusCode;
        this.response = responseString;
    }

    /**
     * @return
     */
    public BEResponseCode getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     */
    public void setStatusCode(BEResponseCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response
     */
    public void setResponse(String response) {
        this.response = response;
    }
}
