/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ZivaCare
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zivacare.android.sdk.network;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zivacare.android.sdk.ZivaCareConfig;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Stelian Morariu on 23/6/2015.
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     * Get a default handler for successful network requests. This handler will call
     * the {@code onSuccess()} method of the passed {@link ZivaCareCallback}.
     *
     * @param callback
     * @return
     */
    public static Response.Listener<JSONObject> getDefaultHandler(final ZivaCareCallback callback) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String message = "";
                if (response != null) {
                    message = response.toString();
                }
                callback.onSuccess(new ZivaCareResponse(200, message));
            }
        };
    }


    /**
     * Get a default handler for failed network requests. This handler will call
     * the {@code onError()} method of the passed {@link ZivaCareCallback}.
     *
     * @param callback
     * @return
     */
    public static Response.ErrorListener getDefaultErrorHandler(final ZivaCareCallback callback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(new ZivaCareResponse(getSafeStatusCode(error), error.getMessage()));
            }
        };
    }

    /**
     * Get a handler for successful user creation requests. This handler will call
     * the {@code onSuccess()} method of the passed {@link ZivaCareCallback} and will also
     * save the received ids and token in the current {@link ZivaCareConfig} and write them to cache.
     *
     * @param callback
     * @return
     */
    public static Response.Listener<JSONObject> getCreateUserHandler(final ZivaCareCallback callback,
                                                                     final String clientSecret,
                                                                     final ZivaCareConfig mConfig) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(new ZivaCareResponse(200, response.toString()));

                // Get client secret and put them in cache
                mConfig.setClientSecret(clientSecret);
                try {
                    mConfig.writeCacheFile(
                            "{\"" + ZivaCareConfig.CLIENT_SECRET + "\":\""
                                    + clientSecret + "\"}", false);
                    // Get special token and client data and put them in cache
                    mConfig.setSpecialTokenFromResponse(response.toString());
                    mConfig.setClientIdFromResponse(response.toString());
                    mConfig.setClientUserIdFromResponse(response.toString());
                    mConfig.setClientUserNameFromResponse(response.toString());
                    mConfig.writeCacheFile(response.toString(), true);
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
    }

    /**
     * Get a handler for successful delete requests. This handler will call
     * the {@code onSuccess()} method of the passed {@link ZivaCareCallback} and will also
     * clear the received ids and tokens from the current {@link ZivaCareConfig} and from the cache.
     *
     * @param callback
     * @return
     */
    public static Response.Listener<JSONObject> getDeleteUserHandler(final ZivaCareCallback callback,
                                                                     final ZivaCareConfig mConfig) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(new ZivaCareResponse(200, response.toString()));

                try {
                    mConfig.writeCacheFile("", false);
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                } finally {
                    mConfig.clean();
                }
            }
        };
    }

    /**
     * Get a handler for successful login requests. This handler will call
     * the {@code onSuccess()} method of the passed {@link ZivaCareCallback} and will also
     * save the access token in the current {@link ZivaCareConfig} and write it to cache.
     *
     * @param callback
     * @return
     */
    public static Response.Listener<JSONObject> getLoginHandler(final ZivaCareCallback callback,
                                                                final ZivaCareConfig mConfig) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(new ZivaCareResponse(200, response.toString()));

                // Get access token and put it in cache
                mConfig.setAccessTokenFromResponse(response.toString());

                try {
                    mConfig.writeCacheFile(response.toString(), true);
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
    }

    /**
     * Get a handler for successful set user requests. This handler will call
     * the {@code onSuccess()} method of the passed {@link ZivaCareCallback} and will also
     * write the response to cache.
     *
     * @param callback
     * @return
     */
    public static Response.Listener<JSONObject> getSetUserHandler(final ZivaCareCallback callback,
                                                                  final ZivaCareConfig mConfig) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(new ZivaCareResponse(200, response.toString()));

                try {
                    mConfig.writeCacheFile(response.toString(), true);
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
    }

    private static int getSafeStatusCode(VolleyError error) {
        int code = -1;
        if (error != null) {
            if (error.networkResponse != null) {
                code = error.networkResponse.statusCode;
            }
        }
        return code;
    }


}
