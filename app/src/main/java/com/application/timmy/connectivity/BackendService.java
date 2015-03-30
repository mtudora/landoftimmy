package com.application.timmy.connectivity;

import org.json.JSONException;
import org.json.JSONObject;

public class BackendService {
    private static String BASE_URL = "http://api.timmystudios.com/api/office.php?pretty";
    private static BackendService instance = null;
    
    /**
     * @return - this singleton's instance
     */
    public static synchronized BackendService getInstance() {
        if (instance == null) {
            instance = new BackendService();
        }
        return instance;
    }

    public JSONObject getOfficeConfiguration() throws ConnectivityException {
        BEResponse response = HttpCaller.requestHttpGet(BASE_URL);
        if (response.getStatusCode().isCodeSuccessful()) {
            try {
                return new JSONObject(response.getResponse());
            } catch (JSONException je) {
                throw new ConnectivityException(je);
            }
        } else {
            throw new ConnectivityException(response.getStatusCode());
        }
    }


}
