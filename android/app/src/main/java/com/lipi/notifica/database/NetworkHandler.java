package com.lipi.notifica.database;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkHandler {

    public interface NetworkListener {
        void onComplete(Result result);
    }

    public static class Result {
        public boolean success;
        public String result;
        public int code;

        public Result() {}
    }

    public final static String BASE_URL = "http://192.168.0.106:8000/";
    public final static String GET_METHOD = "GET";
    public final static String POST_METHOD = "POST";
    public final static String PUT_METHOD = "PUTT";
    public final static String DELETE_METHOD = "DELETE";

    private final String mUsername, mPassword;
    private final boolean mJson;

    // Create a network handler to handle HTTP requests and responses.
    // Pass both username and password as null for no authentication.
    // Set json to true if request and responses are to be json data.
    public NetworkHandler(String username, String password, boolean json) {
        mUsername = username;
        mPassword = password;
        mJson = json;
    }

    // Create a HTTP request of method 'method'.
    // Set postData to null for get request only.
    public String request(String address, String method, String postData) throws IOException, HttpNotOkException {

        // Create a connection to given address
        URL url = new URL(BASE_URL + address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            // Authorize with basic authentication if necessary
            if (mUsername != null && mPassword != null) {
                String authorizationString = "Basic " + Base64.encodeToString(
                        (mUsername + ":" + mPassword).getBytes(),
                        Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", authorizationString);
            }

            // Set request method and other headers
            connection.setRequestMethod(method);
            if (mJson) {
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
            }

            // If post data is present, add it to the output stream of connection
            if (postData != null && postData.length() > 0) {
                connection.setDoOutput(true);

                // TODO: Check with fixed length mode instead of chunked mode
                //connection.setFixedLengthStreamingMode(postData.length());
                connection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                out.write(postData.getBytes());
                out.flush();
            }

            // handle issues
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK)
                throw new HttpNotOkException(statusCode);

            // Get the response and disconnect when done
            InputStream in = new BufferedInputStream(connection.getInputStream());
            return new Scanner(in).useDelimiter("\\A").next();
        }
        finally {
            connection.disconnect();
        }
    }

    // Async Network Handler
    private class AsyncRequest extends AsyncTask<Void, Void, Void> {
        private final String mAddress, mMethod, mData;
        private final Result mResult = new Result();
        private final NetworkListener mNetworkListener;

        public AsyncRequest(NetworkListener networkListener, String address, String method, String data) {
            mAddress = address;
            mMethod = method;
            mData = data;
            mNetworkListener = networkListener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mResult.result = request(mAddress, mMethod, mData);
                mResult.code = HttpURLConnection.HTTP_OK;
                mResult.success = true;
            } catch (IOException e) {
                mResult.code = HttpURLConnection.HTTP_BAD_REQUEST;
                mResult.success = false;
                e.printStackTrace();
            } catch (HttpNotOkException e) {
                mResult.code = e.status;
                mResult.success = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (mNetworkListener != null)
                mNetworkListener.onComplete(mResult);
        }
    }


    // Helper async request methods for GET, POST, PUT, DELETE

    public void get(String address, NetworkListener networkListener) {
        new AsyncRequest(networkListener, address, GET_METHOD, null).execute();
    }

    public void post(String address, String data, NetworkListener networkListener) {
        new AsyncRequest(networkListener, address, POST_METHOD, data).execute();
    }

    public void put(String address, String data, NetworkListener networkListener) {
        new AsyncRequest(networkListener, address, PUT_METHOD, data).execute();
    }

    public void delete(String address, NetworkListener networkListener) {
        new AsyncRequest(networkListener, address, DELETE_METHOD, null).execute();
    }

}
