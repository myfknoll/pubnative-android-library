// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.library.utils.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PubnativeHttpRequest {

    private static final String   TAG                = PubnativeHttpRequest.class.getSimpleName();
    // Request properties
    protected static     int      sConnectionTimeout = 4000; // 4 seconds
    // Inner
    protected            Listener mListener          = null;
    protected            Handler  mHandler           = null;

    //==============================================================================================
    // Listener
    //==============================================================================================
    public interface Listener {

        /**
         * Called when the HttpRequest is about to start
         *
         * @param request request that is about to start
         */
        void onPubnativeHttpRequestStart(PubnativeHttpRequest request);

        /**
         * Called when the HttpRequest has just finished with a valid String response
         *
         * @param request request that have just finished
         * @param result  string with the given response from the server
         */
        void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result);

        /**
         * Called when the HttpRequest fails, after this method the request will be stopped
         *
         * @param request   request that have just failed
         * @param exception exception with more info about the error
         */
        void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets a timeout for establishing connection with the server, if not specified default is 4000 ms
     *
     * @param connectionTimeout time in milliseconds
     */
    public static void setConnectionTimeout(int connectionTimeout) {

        Log.v(TAG, "setConnectionTimeout");
        sConnectionTimeout = connectionTimeout;
    }

    /**
     * This method will start a new request to the given URL
     *
     * @param context   valid Context object
     * @param urlString URL where the request will be done
     * @param listener  valid Listener for callbacks
     */
    public void start(final Context context, final String urlString, Listener listener) {

        Log.v(TAG, "start: " + urlString);
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
        if (mListener == null) {
            Log.w(TAG, "Warning: null listener specified");
        }
        if (TextUtils.isEmpty(urlString)) {
            invokeFail(new IllegalArgumentException("PubnativeHttpRequest - Error: null or empty url, dropping call"));
        } else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        invokeStart();
                        String userAgent = SystemUtils.getWebViewUserAgent(context);
                        if (TextUtils.isEmpty(userAgent)) {
                            invokeFail(new Exception("PubnativeHttpRequest - Error: User agent cannot be retrieved"));
                        } else {
                            initiateRequest(urlString, userAgent);
                        }
                    }
                });
            } else {
                invokeFail(new Exception("PubnativeHttpRequest - Error: internet connection not detected, dropping call"));
            }
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void initiateRequest(final String urlString, final String userAgent) {

        Log.v(TAG, "initiateRequest");
        new Thread(new Runnable() {

            @Override
            public void run() {

                doRequest(urlString, userAgent);
            }
        }).start();
    }

    protected void doRequest(String urlString, String userAgent) {

        Log.v(TAG, "doRequest: " + urlString);
        try {
            // 1. Create connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            // 2. Set connection properties
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(sConnectionTimeout);
            connection.setDoInput(true);
            // 3. Do request
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String resultString = stringFromInputString(inputStream);
                if (resultString == null) {
                    invokeFail(new Exception("PubnativeHttpRequest - Error: invalid response from server"));
                } else {
                    invokeFinish(resultString);
                }
            } else {
                invokeFail(new Exception("PubnativeHttpRequest - Error: invalid status code: " + responseCode));
            }
        } catch (Exception exception) {
            invokeFail(exception);
        }
    }

    protected String stringFromInputString(InputStream inputStream) {

        Log.v(TAG, "stringFromInputString");
        String result = null;
        BufferedReader bufferReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "stringFromInputString - Error:" + e);
            stringBuilder = null;
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "stringFromInputString - Error:" + e);
                }
            }
        }
        if (stringBuilder != null) {
            result = stringBuilder.toString();
        }
        return result;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================
    protected void invokeStart() {

        Log.v(TAG, "invokeStart");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestStart(PubnativeHttpRequest.this);
                }
            }
        });
    }

    protected void invokeFinish(final String result) {

        Log.v(TAG, "invokeFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestFinish(PubnativeHttpRequest.this, result);
                }
            }
        });
    }

    protected void invokeFail(final Exception exception) {

        Log.v(TAG, "invokeFail: " + exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestFail(PubnativeHttpRequest.this, exception);
                }
            }
        });
    }
}
