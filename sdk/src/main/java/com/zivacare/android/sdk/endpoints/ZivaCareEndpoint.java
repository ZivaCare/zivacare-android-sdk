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
package com.zivacare.android.sdk.endpoints;

import android.support.annotation.NonNull;

import com.zivacare.android.sdk.ZivaCareConfig;
import com.zivacare.android.sdk.ZivaCareSDK;
import com.zivacare.android.sdk.network.ZivacareCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abl on 04/06/2015.
 */
public abstract class ZivaCareEndpoint {

    public static final String TYPE_PROFILE = "profile";
    public static final String TYPE_ACTIVITIES = "activities";
    public static final String TYPE_BLOOD_GLUCOSES = "blood_glucoses";
    public static final String TYPE_BLOOD_OXYGENS = "blood_oxygens";
    public static final String TYPE_BLOOD_PRESSURES = "blood_pressures";
    public static final String TYPE_FALLS = "falls";
    public static final String TYPE_BODY_FATS = "body_fats";
    public static final String TYPE_BMIS = "bmis";
    public static final String TYPE_GENETICS = "genetics";
    public static final String TYPE_HEART_RATES = "heart_rates";
    public static final String TYPE_HEIGHTS = "heights";
    public static final String TYPE_LOCATIONS = "locations";
    public static final String TYPE_MEALS = "meals";
    public static final String TYPE_RESPIRATION_RATES = "respiration_rates";
    public static final String TYPE_SLEEPS = "sleeps";
    public static final String TYPE_SLEEP_SUMMARY = "sleep_summary";
    public static final String TYPE_STEPS = "steps";
    public static final String TYPE_WEIGHTS = "weights";

    public static final String OP_INSERT = "insert";
    public static final String OP_UPDATE = "update";

    private static final String API_URL_GENERAL = "/api/v%s/human/%s";
    private static final String API_URL_CODE = "/api/v%s/human/%s/%s";
    private static final String API_URL_DATE = "/api/v%s/human/%s/daily/%s";
    private static final String API_URL_PERIOD = "/api/v%s/human/%s/period/%s/%s";

    private static final String USER_CODE = "user_code";
    private static final String SOURCE = "source";
    private static final String DATA = "data";
    private static final String OP = "op";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd");

    static {
        DATE_FORMAT.setLenient(true);
    }

    protected ZivaCareConfig config = null;
    protected ZivaCareSDK mSdk;

    public ZivaCareEndpoint(@NonNull ZivaCareSDK sdk) {
        mSdk = sdk;
    }

    /**
     * Get all endpoint data
     *
     * @param version
     */
    public void getAll(@NonNull int version, @NonNull ZivacareCallback callback) {
        mSdk.callEndpoint(
                String.format(API_URL_GENERAL, version, getType()), config, callback);
    }

    /**
     * Get all endpoint data filtered by code
     *
     * @param version
     * @param code
     */
    public void getByCode(int version, String code, @NonNull ZivacareCallback callback) {
        mSdk.callEndpoint(
                String.format(API_URL_CODE, version, getType(), code), config, callback);
    }

    /**
     * Get all endpoint data filtered by date
     *
     * @param version
     * @param date
     */
    public void getByDate(int version, Date date, @NonNull ZivacareCallback callback) {
        mSdk.callEndpoint(
                String.format(API_URL_DATE, version, getType(),
                        DATE_FORMAT.format(date)), config, callback);
    }

    /**
     * Get all endpoint data filtered by period
     *
     * @param version
     * @param startDate
     * @param endDate
     */
    public void getByPeriod(int version, Date startDate,
                            Date endDate, @NonNull ZivacareCallback callback) {
        mSdk.callEndpoint(
                String.format(API_URL_PERIOD, version, getType(),
                        DATE_FORMAT.format(startDate),
                        DATE_FORMAT.format(endDate)), config, callback);
    }

    /**
     * Get the default ZivaCareSDK date format to be used
     *
     * @return DateFormat
     */
    public static DateFormat getEndpointDateFormat() {
        return DATE_FORMAT;
    }

    /**
     * Get the default dataNames for posting data to the endpoint
     * <p/>
     * []
     */
    public abstract String[] getDataNames();

    /**
     * This method will return the API endpoint type string (TYPE constant)
     */
    public abstract String getType();

    /**
     * Setting the config is not mandatory if there is a system property
     * "access_token" set up and valid
     *
     * @param config
     */
    public void setConfig(ZivaCareConfig config) {
        this.config = config;
    }

    /**
     * Send data to the endpoint to update or insert
     *
     * @param version    - endpoint version
     * @param operation  - OP_INSERT or OP_UPDATE
     * @param source     - source from where the data is sent
     * @param dataValues - an array of arrays with the parameter values <b>without
     *                   <i>user_code & source</i></b>
     */
    public void setData(int version, String operation,
                        String source, Object[][] dataValues, @NonNull ZivacareCallback callback) {
        setData(version, operation, source, null, dataValues, callback);
    }

    /**
     * Send data to the endpoint to update or insert
     *
     * @param version    - endpoint version
     * @param operation  - OP_INSERT or OP_UPDATE
     * @param source     - source from where the data is sent
     * @param dataNames  (<i>not mandatory</i> - if null will be taken the default ones
     *                   for the current endpoint)<br>
     *                   - a String array with the names of the parameters for each
     *                   parameter array for dataValues, <b>without <i>user_code &
     *                   source</i></b>
     * @param dataValues - an array of arrays with the parameter values <b>without
     *                   <i>user_code & source</i></b>
     */
    public void setData(@NonNull int version, @NonNull String operation,
                        @NonNull String source,  String[] dataNames,
                        @NonNull Object[][] dataValues, @NonNull ZivacareCallback callback) {
        Object[] dataArray = new Object[dataValues.length];
        for (int x = 0; x < dataValues.length; x++) {
            Map<String, Object> innerDataMap = new LinkedHashMap<String, Object>();
            innerDataMap.put(USER_CODE, this.config.getZivaUserCode());
            innerDataMap.put(SOURCE, source);
            for (int i = 0; i < dataValues[x].length; i++) {
                innerDataMap
                        .put(dataNames != null && dataNames.length > 0 ? dataNames[i]
                                : getDataNames()[i], dataValues[x][i]);
            }
            dataArray[x] = innerDataMap;
        }

        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put(OP, operation);
        dataMap.put(DATA, dataArray);

        mSdk.callEndpoint(String.format(API_URL_GENERAL, version, getType()),
                config,dataMap, callback);
    }

}
