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
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration class for ZivaCareSDK<br>
 * This will hold all needed user data used later for post & get requests
 * authorisations
 *
 * @author abl
 */
public class ZivaCareConfig {

    public static final String URL_AUTH = "/oauth/v2/get-access-token";
    public static final String URL_CREATE_USER = "/api/v1/app/create-user";
    public static final String URL_SET_APP_USER = "/api/v1/app/set-application-user-data-source-token";
    public static final String URL_REFRESH = "/oauth/v2/token";
    public static final String URL_EXTERN = "/extern/en/iframe";

    public static final String ACCESS_TOKEN_QUERY_PARAM = "?access_token=";
    public static final String REFRESH_TOKEN_QUERY_PARAM = "?client_id=%s&client_secret=%s&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    public static final String DELETE_USER_QUERY_PARAM = "/api/v1/app/%s/users/%s";

    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_USER_ID = "clientUserId";
    public static final String CLIENT_USER_NAME = "clientUserName";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String SPECIAL_TOKEN = "specialToken";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String DATA_SOURCE_NAME = "dataSourceName";
    public static final String ZIVA_USER_CODE = "ziva_user_code";
    public static final String TOKEN = "token";
    public static final String SECRET = "secret";
    public static final String ERROR = "error";
    public static final String CODE = "code";
    public static final String MESSAGE = "message";

    private static final String STRING_EMPTY = "";
    private static final String STRING_NEWLINE = "\n";
    private static final String CONFIG_FILE = "config.properties";

    private boolean demo = true;
    private static boolean debugEnabled = false;
    private File cacheFile = null;
    private String specialToken = null;
    private String clientSecret = null;
    private String clientId = null;
    private String clientUserId = null;
    private String clientUserName = null;
    private String zivaUserCode = null;
    private String accessToken = null;
    private String mDevUrl;
    private String mApiUrl;

    /**
     * Constructor for ZivaCareConfig
     *
     * @param context - context of the app
     * @param demo    - if enabled then the get request will be called with
     *                access_token=demo
     */
    public ZivaCareConfig(@NonNull Context context, boolean demo) {
        this.demo = demo;
        readConfig(context);

        this.cacheFile = new File(context.getCacheDir().getPath() + "/" + "ziva_cache");
        if (cacheFile != null && cacheFile.exists()) {
            try {
                String cacheStr = readCacheFile();
                if (cacheStr != null && !cacheStr.trim().isEmpty()) {
                    setAccessTokenFromResponse(cacheStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the URL for the developer server.
     *
     * @return
     */
    public String getDevUrl() {
        return mDevUrl;
    }


    /**
     * Get the URL for the API server.
     *
     * @return
     */
    public String getApiUrl() {
        return mApiUrl;
    }

    public void clean() {
        this.accessToken = null;
        this.clientId = null;
        this.clientSecret = null;
        this.clientUserId = null;
        this.clientUserName = null;
        this.specialToken = null;
        this.zivaUserCode = null;
        System.setProperty(ACCESS_TOKEN, "");
    }

    /**
     * Get the accessToken for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.<br>
     * This will be used later on each request.
     *
     * @return String
     */
    public String getAccessToken() {
        if (accessToken == null
                && STRING_EMPTY.equals(System.getProperty(ACCESS_TOKEN,
                STRING_EMPTY))) {
            try {
                setAccessTokenFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return demo ? "demo" : (accessToken != null ? accessToken : System
                .getProperty(ACCESS_TOKEN));
    }

    /**
     * Get the cache file where the user data is/will be stored
     *
     * @return File
     */
    public File getCacheFile() {
        return cacheFile;
    }

    /**
     * Get the clientId for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.
     *
     * @return String
     */
    public String getClientId() {
        if (clientId == null) {
            try {
                setClientIdFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clientId;
    }

    /**
     * Get the clientSecret for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.
     *
     * @return String
     */
    public String getClientSecret() {
        if (clientSecret == null) {
            try {
                setClientSecretFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clientSecret;
    }

    /**
     * Get the clientUserid for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.
     *
     * @return String
     */
    public String getClientUserId() {
        if (clientUserId == null) {
            try {
                setClientUserIdFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clientUserId;
    }

    /**
     * Get the clientUserName for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.
     *
     * @return String
     */
    public String getClientUserName() {
        if (clientUserName == null) {
            try {
                setClientUserNameFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clientUserName;
    }

    /**
     * Method used for debugging post and get methods
     *
     * @return String
     */
    public String getConfigDebugStr() {
        StringBuilder dbgStr = new StringBuilder(
                "Ziva SDK ZivaCareConfig content: ");
        dbgStr.append("\n clientSecret: " + this.getClientSecret());
        dbgStr.append("\n specialToken: " + this.getSpecialToken());
        dbgStr.append("\n accessToken: " + this.getAccessToken());
        dbgStr.append("\n clientId: " + this.getClientId());
        dbgStr.append("\n clientUserId: " + this.getClientUserId());
        dbgStr.append("\n clientUserName: " + this.getClientUserName());
        try {
            dbgStr.append("\n cache file: " + readCacheFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dbgStr.toString();
    }

    /**
     * Get the specialToken for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.
     *
     * @return String
     */
    public String getSpecialToken() {
        if (specialToken == null) {
            try {
                setSpecialTokenFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return specialToken;
    }

    /**
     * Get the zivaUserCode for the user, if it's not set in the configuration
     * object, then it will be read from the cache file.
     *
     * @return String
     */
    public String getZivaUserCode() {
        if (zivaUserCode == null) {
            try {
                setZivaUserCodeFromResponse(readCacheFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return zivaUserCode;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * Use to read the ZivaCareSDK config that is cached
     *
     * @return String / json
     * @throws IOException
     */
    public String readCacheFile() throws IOException {
        if (!cacheFile.exists())
            return null;
        String strLine = STRING_EMPTY;
        StringBuilder text = new StringBuilder();
        FileReader fReader = null;
        BufferedReader bReader = null;
        try {
            fReader = new FileReader(cacheFile);
            bReader = new BufferedReader(fReader);
            while ((strLine = bReader.readLine()) != null) {
                text.append(strLine + STRING_NEWLINE);
            }
            return text.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bReader != null)
                bReader.close();
            if (fReader != null)
                fReader.close();
        }
        return null;
    }

    /**
     * Set the accessToken for the user in the configuration file
     *
     * @param accessTokenStr
     */
    public void setAccessToken(String accessTokenStr) {
        accessToken = accessTokenStr;
        System.setProperty(ACCESS_TOKEN, accessTokenStr);
    }

    /**
     * Set the accessToken in configuration object and in the cache file from a
     * Json formated String
     *
     * @param responseStr
     */
    public void setAccessTokenFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setAccessToken((String) json.get(ZivaCareConfig.ACCESS_TOKEN));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.ACCESS_TOKEN);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.ACCESS_TOKEN.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setAccessToken(responseStr1);
            }
        }
    }

    /**
     * Set the clientId for the user in the configuration file
     *
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Set the clientId in configuration object and in the cache file from a
     * Json formated String
     *
     * @param responseStr
     */
    public void setClientIdFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setClientId((String) json.get(ZivaCareConfig.CLIENT_ID));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.CLIENT_ID);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.CLIENT_ID.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setClientId(responseStr1);
            }
        }
    }

    /**
     * Set the clientSecret for the user in the configuration file
     *
     * @param clientSecret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Set the clientSecret in configuration object and in the cache file from a
     * Json formated String
     *
     * @param responseStr
     */
    public void setClientSecretFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setClientSecret((String) json
                    .get(ZivaCareConfig.CLIENT_SECRET));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.CLIENT_SECRET);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.CLIENT_SECRET.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setClientSecret(responseStr1);
            }
        }
    }

    /**
     * Set the clientUserId for the user in the configuration file
     *
     * @param clientUserId
     */
    public void setClientUserId(String clientUserId) {
        this.clientUserId = clientUserId;
    }

    /**
     * Set the clientUserId in configuration object and in the cache file from a
     * Json formated String
     *
     * @param responseStr
     */
    public void setClientUserIdFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setClientUserId((String) json
                    .get(ZivaCareConfig.CLIENT_USER_ID));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.CLIENT_USER_ID);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.CLIENT_USER_ID.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setClientUserId(responseStr1);
            }
        }
    }

    /**
     * Set the clientUserName for the user in the configuration file
     *
     * @param clientUserName
     */
    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    /**
     * Set the clientUserName in configuration object and in the cache file from
     * a Json formated String
     *
     * @param responseStr
     */
    public void setClientUserNameFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setClientUserName((String) json
                    .get(ZivaCareConfig.CLIENT_USER_NAME));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.CLIENT_USER_NAME);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.CLIENT_USER_NAME.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setClientUserName(responseStr1);
            }
        }
    }

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    /**
     * Set the specialToken for the user in the configuration object
     *
     * @param specialToken
     */
    public void setSpecialToken(String specialToken) {
        this.specialToken = specialToken;
    }

    /**
     * Set the specialToken in configuration object and in the cache file from a
     * Json formated String
     *
     * @param responseStr
     */
    public void setSpecialTokenFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setSpecialToken((String) json
                    .get(ZivaCareConfig.SPECIAL_TOKEN));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.SPECIAL_TOKEN);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.SPECIAL_TOKEN.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setSpecialToken(responseStr1);
            }
        }
    }

    /**
     * Set the zivaUserCode for the user in the configuration file
     *
     * @param zivaUserCode
     */
    public void setZivaUserCode(String zivaUserCode) {
        this.zivaUserCode = zivaUserCode;
    }

    /**
     * Set the zivaUserCode in configuration object and in the cache file from a
     * Json formated String
     *
     * @param responseStr
     */
    private void setZivaUserCodeFromResponse(String responseStr) {
        if (responseStr == null)
            return;
        try {
            JSONObject json = new JSONObject(responseStr);
            this.setZivaUserCode((String) json
                    .get(ZivaCareConfig.ZIVA_USER_CODE));
        } catch (Exception e) {
            if (isDebugEnabled())
                System.out.println("json error: " + e.getMessage());
            int index = responseStr.indexOf(ZivaCareConfig.ZIVA_USER_CODE);
            if (index > -1) {
                String responseStr1 = responseStr.substring(index
                        + ZivaCareConfig.ZIVA_USER_CODE.length() + 3);
                responseStr1 = responseStr1.substring(0,
                        responseStr1.indexOf("\""));
                this.setZivaUserCode(responseStr1);
            }
        }
    }

    /**
     * Use this method to write the access token and other important data to the
     * ZivaCareSDK cache
     *
     * @param content
     * @param append
     * @throws IOException
     */
    public void writeCacheFile(String content, boolean append)
            throws IOException {
        if (content == null)
            return;
        FileWriter writer = null;
        try {
            writer = new FileWriter(cacheFile, append);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    private void readConfig(Context context) {
        try {
            final Properties prop = new Properties();
            prop.load(context.getAssets().open(CONFIG_FILE));
            mDevUrl = prop.getProperty("ZIVA-DEV-URL");
            mApiUrl = prop.getProperty("ZIVA-API-URL");
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
    }
}
