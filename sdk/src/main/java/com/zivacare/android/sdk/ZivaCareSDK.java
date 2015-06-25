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
package com.zivacare.android.sdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zivacare.android.sdk.network.NetworkUtils;
import com.zivacare.android.sdk.network.ZivaCareGetRequest;
import com.zivacare.android.sdk.network.ZivaCarePostRequest;
import com.zivacare.android.sdk.network.ZivacareCallback;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Ziva SDK class<br>
 * Holds the connection, authorisation and REST web services calls.<br>
 * All needed user data will be stored in the application cache
 *
 * @author abl
 */
public class ZivaCareSDK {

    private static final String LOG_TAG = ZivaCareSDK.class.getSimpleName();
    private ZivaCareConfig mConfig = null;
    private RequestQueue mRequestQueue;

    /**
     * ZivaCareSDK constructor
     *
     * @param config
     */
    public ZivaCareSDK(@NonNull Context context, ZivaCareConfig config) {
        mConfig = config;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * ZivaCareSDK constructor
     *
     * @param context Context of the app
     * @param demo    (if true then all get requests will use the demo access token
     */
    public ZivaCareSDK(@NonNull Context context, boolean demo) {
        mConfig = new ZivaCareConfig(context, demo);
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Call a ZivaCare API endpoint.
     *
     * @param endpointUrl - from ZivaCareEndpoint abstract class constant
     * @param config      - a ZivaCareConfig instance used in ZivaCareSDK instance
     * @param callback
     */
    public void callEndpoint(@NonNull String endpointUrl, @NonNull ZivaCareConfig config,
                             @NonNull ZivacareCallback callback) {
        String formatedEndpointUrl = mConfig.getApiUrl() + endpointUrl
                + ZivaCareConfig.ACCESS_TOKEN_QUERY_PARAM
                + (config != null ? config.getAccessToken() : System
                .getProperty(ZivaCareConfig.ACCESS_TOKEN));

        final ZivaCareGetRequest request = new ZivaCareGetRequest(formatedEndpointUrl,
                NetworkUtils.getDefaultHandler(callback), NetworkUtils.getDefaultErrorHandler(callback));
        mRequestQueue.add(request);
    }

    /**
     * Call a ZivaCare Api endpoint for POST-ing data
     *
     * @param endpoint
     * @param config
     * @param dataMap
     * @return String response
     * @throws Exception
     */
    public void callEndpoint(@NonNull String endpoint, @NonNull ZivaCareConfig config,
                             @NonNull Map dataMap,
                             @NonNull ZivacareCallback callback) {


        final ZivaCarePostRequest request = new ZivaCarePostRequest(
                mConfig.getApiUrl() + endpoint + ZivaCareConfig.ACCESS_TOKEN_QUERY_PARAM
                        + config.getAccessToken(),
                NetworkUtils.getDefaultHandler(callback),
                NetworkUtils.getDefaultErrorHandler(callback),
                dataMap);
        mRequestQueue.add(request);
    }

    /**
     * Call the set application user data source & token, and, not required,
     * secret
     *
     * @param dataSourceName
     * @param token
     * @param secret
     * @return Map with keys: @ZivaCareConfig.KEY_RESPONSE_CODE, @ZivaCareConfig.KEY_RESPONSE_STRING
     */
    public void setUser(@NonNull String dataSourceName, @NonNull String token,
                        String secret, @NonNull ZivacareCallback callback) {
        final Map<String, String> mapParameters = new HashMap<String, String>();
        mapParameters.put(ZivaCareConfig.CLIENT_ID, getConfig().getClientId());
        mapParameters.put(ZivaCareConfig.CLIENT_SECRET, getConfig()
                .getClientSecret());
        mapParameters.put(ZivaCareConfig.CLIENT_USER_ID, getConfig()
                .getClientUserId());
        mapParameters.put(ZivaCareConfig.DATA_SOURCE_NAME, dataSourceName);
        mapParameters.put(ZivaCareConfig.TOKEN, token);

        if (secret != null) {
            mapParameters.put(ZivaCareConfig.SECRET, secret);
        }

        final ZivaCarePostRequest request = new ZivaCarePostRequest(
               mConfig.getDevUrl() + ZivaCareConfig.URL_SET_APP_USER,
                NetworkUtils.getSetUserHandler(callback, mConfig),
                NetworkUtils.getDefaultErrorHandler(callback),
                mapParameters);

        mRequestQueue.add(request);
    }

    /**
     * Create user with the data from ZivaCare application settings.<br>
     * This method uses the data from the mConfig and/or mConfig cache.
     *
     * @throws Exception
     */
    public void createUser(@NonNull ZivacareCallback callback) {
        String clientId = this.getConfig().getClientId();
        String clientSecret = this.getConfig().getClientSecret();
        String clientUserId = this.getConfig().getClientUserId();
        String clientUserName = this.getConfig().getClientUserName();
        createUser(clientId, clientSecret, clientUserId, clientUserName, callback);
    }

    /**
     * Call to create user with the data from ZivaCare application settings
     *
     * @param clientId
     * @param clientSecret
     * @param clientUserId
     * @param clientUserName
     */
    public void createUser(String clientId, String clientSecret,
                           String clientUserId, String clientUserName,
                           @NonNull ZivacareCallback callback) {

        final Map<String, String> mapParameters = new HashMap<String, String>();
        mapParameters.put(ZivaCareConfig.CLIENT_ID, clientId);
        mapParameters.put(ZivaCareConfig.CLIENT_SECRET, clientSecret);
        mapParameters.put(ZivaCareConfig.CLIENT_USER_ID, clientUserId);
        mapParameters.put(ZivaCareConfig.CLIENT_USER_NAME, clientUserName);

        final ZivaCarePostRequest request = new ZivaCarePostRequest(
               mConfig.getDevUrl() + ZivaCareConfig.URL_CREATE_USER,
                NetworkUtils.getCreateUserHandler(callback, clientSecret, mConfig),
                NetworkUtils.getDefaultErrorHandler(callback), mapParameters);

        mRequestQueue.add(request);
    }

    /**
     * Call the delete user with the data from ZivaCare application settings.<br>
     * This method uses the data from the mConfig and/or mConfig cache.
     */
    public void deleteUser(@NonNull ZivacareCallback callback) {
        String clientId = this.getConfig().getClientId();
        deleteUser(clientId, callback);
    }

    /**
     * Call the delete user with the data from ZivaCare application settings
     *
     * @param clientId
     * @param callback
     */
    public void deleteUser(String clientId, @NonNull ZivacareCallback callback) {
        final String deleteUserUrl =mConfig.getDevUrl()
                + String.format(ZivaCareConfig.DELETE_USER_QUERY_PARAM,
                clientId, getConfig().getZivaUserCode());

        final ZivaCareGetRequest request = new ZivaCareGetRequest(Request.Method.DELETE, deleteUserUrl,
                NetworkUtils.getDeleteUserHandler(callback, mConfig),
                NetworkUtils.getDefaultErrorHandler(callback));

        mRequestQueue.add(request);
    }

    /**
     * Call a ZivaCare API endpoint
     *
     * @param endpoint - from ZivaCareEndpoint abstract class constant
     * @param version
     */
    public void callEndpoint(@NonNull String endpoint, @NonNull int version,
                             @NonNull ZivacareCallback callback) {
        String formatedEndpointUrl = mConfig.getApiUrl()
                + String.format(endpoint, version)
                + ZivaCareConfig.ACCESS_TOKEN_QUERY_PARAM
                + mConfig.getAccessToken();
        final ZivaCareGetRequest request = new ZivaCareGetRequest(formatedEndpointUrl,
                NetworkUtils.getDefaultHandler(callback),
                NetworkUtils.getDefaultErrorHandler(callback));
        mRequestQueue.add(request);
    }

    /**
     * Call a ZivaCare API endpoint
     *
     * @param endpoint - from ZivaCareEndpoint abstract class constant
     * @param version
     * @param date
     */
    public void callEndpoint(@NonNull String endpoint, @NonNull int version,
                             @NonNull Date date,
                             @NonNull ZivacareCallback callback) {
        String formatedEndpointUrl = mConfig.getApiUrl()
                + String.format(endpoint, version, date)
                + ZivaCareConfig.ACCESS_TOKEN_QUERY_PARAM
                + mConfig.getAccessToken();

        final ZivaCareGetRequest request = new ZivaCareGetRequest(formatedEndpointUrl,
                NetworkUtils.getDefaultHandler(callback),
                NetworkUtils.getDefaultErrorHandler(callback));
        mRequestQueue.add(request);
    }

    /**
     * Call a ZivaCare API endpoint
     *
     * @param endpoint  - from ZivaCareEndpoint abstract class constant
     * @param version
     * @param startDate
     * @param endDate
     */
    public void callEndpoint(@NonNull String endpoint, @NonNull int version,
                             @NonNull Date startDate, @NonNull Date endDate,
                             @NonNull ZivacareCallback callback) throws Exception {
        String formatedEndpointUrl = mConfig.getApiUrl()
                + String.format(endpoint, version, startDate, endDate)
                + ZivaCareConfig.ACCESS_TOKEN_QUERY_PARAM
                + mConfig.getAccessToken();
        final ZivaCareGetRequest request = new ZivaCareGetRequest(formatedEndpointUrl,
                NetworkUtils.getDefaultHandler(callback),
                NetworkUtils.getDefaultErrorHandler(callback));
        mRequestQueue.add(request);
    }

    /**
     * Call a ZivaCare API endpoint
     *
     * @param endpoint - from ZivaCareEndpoint abstract class constant
     * @param version
     * @param code
     */
    public void callEndpoint(@NonNull String endpoint, @NonNull int version,
                             @NonNull String code, @NonNull ZivacareCallback callback)
            throws Exception {
        String formatedEndpointUrl = mConfig.getApiUrl()
                + String.format(endpoint, version, code)
                + ZivaCareConfig.ACCESS_TOKEN_QUERY_PARAM
                + mConfig.getAccessToken();

        final ZivaCareGetRequest request = new ZivaCareGetRequest(formatedEndpointUrl,
                NetworkUtils.getDefaultHandler(callback),
                NetworkUtils.getDefaultErrorHandler(callback));
        mRequestQueue.add(request);
    }

    /**
     * Call the login with authorisation to get a access token.<br>
     * This method uses the data from the mConfig and/or mConfig cache.
     */
    public void login(@NonNull ZivacareCallback callback) {
        String clientSecret = this.getConfig().getClientSecret();
        String specialToken = this.getConfig().getSpecialToken();
        login(clientSecret, specialToken, callback);
    }

    /**
     * Call the login with authorisation to get a access token
     *
     * @param clientSecret
     * @param specialToken
     */
    public void login(String clientSecret, String specialToken,
                      @NonNull ZivacareCallback callback) {

        final Map<String, String> mapParameters = new HashMap<String, String>();
        mapParameters.put(ZivaCareConfig.SPECIAL_TOKEN, specialToken);
        mapParameters.put(ZivaCareConfig.CLIENT_SECRET, clientSecret);

        final ZivaCarePostRequest request = new ZivaCarePostRequest(
               mConfig.getDevUrl() + ZivaCareConfig.URL_AUTH,
                NetworkUtils.getLoginHandler(callback, mConfig),
                NetworkUtils.getDefaultErrorHandler(callback),
                mapParameters);

        mRequestQueue.add(request);
    }

    /**
     * Call a ZivaCare API refresh token.<br>
     * This method uses the data from the mConfig and/or mConfig cache.
     */
    public void refreshToken(@NonNull ZivacareCallback callback) {
        refreshToken(mConfig.getClientId(),
                mConfig.getClientSecret(),
                callback);
    }

    /**
     * Call a ZivaCare API refresh token
     *
     * @param clientId
     * @param clientSecret
     * @param callback
     */
    public void refreshToken(String clientId, String clientSecret,
                             @NonNull ZivacareCallback callback) {
        final String formatedRefreshUrl =mConfig.getDevUrl()
                + String.format(ZivaCareConfig.REFRESH_TOKEN_QUERY_PARAM,
                clientId, clientSecret);

        final ZivaCareGetRequest request = new ZivaCareGetRequest(formatedRefreshUrl,
                NetworkUtils.getDefaultHandler(callback), NetworkUtils.getDefaultErrorHandler(callback));

        mRequestQueue.add(request);
    }

    /**
     * Get the mConfig from the ZivaCareSDK instance
     *
     * @return ZivaCareConfig
     */
    public ZivaCareConfig getConfig() {
        return mConfig;
    }


}
