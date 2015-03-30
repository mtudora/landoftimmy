package com.application.timmy.connectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


import android.net.Uri;
import android.util.Log;

class HttpCaller {

    public static final String TAG = "HttpCaller";

    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int SOCKET_TIMEOUT = 60000;

    /**
     * Call a Http POST method on the specified REST service
     * 
     * @param url
     *            - REST service URL
     * @param requestHeaderParameters
     *            - Map<String, String> that contains the request header's parameters
     * @param requestParameters
     *            - Map<String, String> that contains the request's body parameters
     * @return - Map<String, Object> that contains the values for the following keys: RESPONSE_CODE_KEY - value of type BEResponseCode, RESPONSE_MESSAGE_KEY - value of type String,
     *         RESPONSE_JSON_OBJECT_KEY - value of type JSONObject
     * @throws ConnectivityException
     */
    public static BEResponse requestHttpPost(String url, Map<String, String> requestHeaderParameters, Map<String, String> requestParameters) throws ConnectivityException {
       
        HttpPost httpPostRequest = new HttpPost(url);
        String result = null;

        // build the request parameters
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (requestParameters != null) {
            for (String key : requestParameters.keySet()) {
                nameValuePairs.add(new BasicNameValuePair(key, requestParameters.get(key)));
            }
        }

        // build HttpParams and HttpClient
        HttpClient client = HttpCaller.buildHttpClient();

        if (requestHeaderParameters == null) {
            requestHeaderParameters = new HashMap<String, String>();
        }
        requestHeaderParameters.put("Accept", "application/json");

        // set the header 's parameters and the request entity
        HttpCaller.addRequestHeaderParameters(requestHeaderParameters, httpPostRequest);
        // build the request content entity
        try {
            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // send the request and get the response
        HttpResponse response;
        try {
            response = client.execute(httpPostRequest);
        } catch (IOException e) {
            throw new ConnectivityException(e);
        }
        BEResponseCode responseCode = BEResponseCode.findByCode(response.getStatusLine().getStatusCode());
        if (!responseCode.isCodeSuccessful()) {
            throw new ConnectivityException(responseCode);
        }
        result = getStringResult(response);
        return new BEResponse(responseCode, result);
    }

    public static BEResponse requestHttpPost(String url) throws ConnectivityException {
        
        HttpPost httpPostRequest = new HttpPost(url);
        String result = null;
       

        // build HttpParams and HttpClient
        HttpClient client = HttpCaller.buildHttpClient();     

        // send the request and get the response
        HttpResponse response;
        try {
            response = client.execute(httpPostRequest);
        } catch (IOException e) {
            throw new ConnectivityException(e);
        }
        BEResponseCode responseCode = BEResponseCode.findByCode(response.getStatusLine().getStatusCode());
        if (!responseCode.isCodeSuccessful()) {
            throw new ConnectivityException(responseCode);
        }
        result = getStringResult(response);
        return new BEResponse(responseCode, result);
    }
    
    /**
     * Call a Http Delete method on the specified REST service
     * 
     * @param url
     *            - REST service URL
     * @param requestHeaderParameters
     *            - Map<String, String> that contains the request header's parameters
     * @return - Map<String, Object> that contains the values for the following keys: RESPONSE_CODE_KEY - value of type BEResponseCode, RESPONSE_MESSAGE_KEY - value of type String,
     *         RESPONSE_JSON_OBJECT_KEY - value of type JSONObject
     * @throws ConnectivityException
     */
    public static BEResponse requestHttpDelete(String url, Map<String, String> requestHeaderParameters) throws ConnectivityException {

        HttpDelete httpDeleteRequest = new HttpDelete(url);
        String result = null;

        // build HttpParams and HttpClient
        HttpClient client = HttpCaller.buildHttpClient();

        if (requestHeaderParameters == null) {
            requestHeaderParameters = new HashMap<String, String>();
        }
        requestHeaderParameters.put("Accept", "application/json");

        // set the header 's parameters and the request entity
        HttpCaller.addRequestHeaderParameters(requestHeaderParameters, httpDeleteRequest);
        Log.i("AnalyticsApp", "url: " + url);

        // send the request and get the response
        HttpResponse response;
        try {
            response = client.execute(httpDeleteRequest);
        } catch (IOException e) {
            throw new ConnectivityException(e);
        }
        BEResponseCode responseCode = BEResponseCode.findByCode(response.getStatusLine().getStatusCode());

        if (!responseCode.isCodeSuccessful()) {
            throw new ConnectivityException(responseCode);
        }
        result = getStringResult(response);

        return new BEResponse(responseCode, result);
    }

    /**
     * Call a Http GET method on the specified REST service without authentification
     * 
     * @param url
     *            - REST service URL
     * @param urlParameters
     *            - HttpGet url parameters to be added to the request
     * @return - String containing the response
     * @throws ConnectivityException
     */
    public static BEResponse requestHttpGet(String url, Map<String, String> urlParameters, Map<String, String> requestHeaderParameters) throws ConnectivityException {
        if (urlParameters != null) {
            url = getUrlWithAddedParameters(url, urlParameters);
        }
        return HttpCaller.requestHttpGet(url, requestHeaderParameters);
    }

    /**
     * Call a Http GET method on the specified REST service without authentification
     * 
     * @param url
     *            - REST service URL
     * @return - String containing the response
     * @throws ConnectivityException
     */
    public static BEResponse requestHttpGet(String url) throws ConnectivityException {
        return HttpCaller.requestHttpGet(url, null, null);
    }

    /**
     * Call a Http GET method on the specified REST service with basic authentification
     * 
     * @param url
     *            - REST service URL
     * @param requestHeaderParameters
     *            - Map<String, String> that contains the request header's parameters
     * @param requestParameters
     *            - Map<String, String> that contains the request's body parameters
     * @return - String containing the response
     * @throws ConnectivityException
     */
    public static BEResponse requestHttpGet(String url, Map<String, String> requestHeaderParameters) throws ConnectivityException {


        HttpGet httpGetRequest = new HttpGet(url);
        String result = null;

        // build HttpParams and HttpClient
        HttpClient client = HttpCaller.buildHttpClient();

        if (requestHeaderParameters == null) {
            requestHeaderParameters = new HashMap<String, String>();
        }
        requestHeaderParameters.put("Accept", "application/json");

        // set the header 's parameters and the request entity
        HttpCaller.addRequestHeaderParameters(requestHeaderParameters, httpGetRequest);

        // send the request and get the response
        HttpResponse response;
        try {
            response = client.execute(httpGetRequest);
        } catch (IOException e) {
            throw new ConnectivityException(e);
        }
        BEResponseCode responseCode = BEResponseCode.findByCode(response.getStatusLine().getStatusCode());

        if (!responseCode.isCodeSuccessful()) {
            throw new ConnectivityException(responseCode);
        }
        result = getStringResult(response);

        return new BEResponse(responseCode, result);

    }

    private static void addRequestHeaderParameters(Map<String, String> requestHeaderParameters, HttpRequest httpRequest) {
        // set the header 's parameters and the request entity
        if (requestHeaderParameters != null && httpRequest != null) {
            for (String key : requestHeaderParameters.keySet()) {
                httpRequest.setHeader(key, requestHeaderParameters.get(key));
            }
        }
    }

    private static HttpClient buildHttpClient() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParams);
        // HttpClient client = AndroidHttpClient.newInstance("Android");
        // HttpClient client = new MyHttpClient(httpParams);
        return client;
    }

    private static String getStringResult(HttpResponse response) throws ConnectivityException {
        BEResponseCode responseCode = BEResponseCode.findByCode(response.getStatusLine().getStatusCode());
        if (!responseCode.isCodeSuccessful()) {
            // status code: response.getStatusLine().getStatusCode()
            throw new ConnectivityException(responseCode);
        }
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line);
                }
                return total.toString();
            }
        } catch (IOException e) {
            throw new ConnectivityException(e);
        }
        return null;
    }

    private static String getUrlWithAddedParameters(String url, Map<String, String> urlParameters) {
        if (urlParameters == null) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (String key : urlParameters.keySet()) {
            builder.appendQueryParameter(key, urlParameters.get(key));
        }
        return builder.build().toString();
    }
}
