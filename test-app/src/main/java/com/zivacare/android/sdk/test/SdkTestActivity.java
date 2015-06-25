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
package com.zivacare.android.sdk.test;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.zivacare.android.sdk.ZivaCareConfig;
import com.zivacare.android.sdk.ZivaCareSDK;
import com.zivacare.android.sdk.endpoints.ZivaCareActivitiesEndpoint;
import com.zivacare.android.sdk.endpoints.ZivaCareEndpoint;
import com.zivacare.android.sdk.endpoints.ZivaCareProfileEndpoint;
import com.zivacare.android.sdk.network.ZivaCareResponse;
import com.zivacare.android.sdk.network.ZivacareCallback;

public class SdkTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = SdkTestActivity.class.getSimpleName();
    private static String clientSecret = "insert_client_secret_here";
    private static String clientId = "insert_client_id_here";
    private static String clientUserId = "insert_user_email_here";
    private static String clientUserName = "insert_user_full_name_here";

    private ZivaCareSDK mZivaSdk;
    private CircularProgressButton btnTestCreateUser;
    private CircularProgressButton btnTestAccessToken;
    private CircularProgressButton btnTestDeleteUser;
    private CircularProgressButton btnTestRefreshToken;
    private CircularProgressButton btnTestEndPointGet;
    private CircularProgressButton btnTestEndPointPost;
    private CircularProgressButton btnTestCache;
    private CircularProgressButton btnClearCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTestCreateUser = (CircularProgressButton) findViewById(R.id.btn_test_createuser);
        btnTestCreateUser.setOnClickListener(this);
        btnTestCreateUser.setIndeterminateProgressMode(true);

        btnTestAccessToken = (CircularProgressButton) findViewById(R.id.btn_test_accesstoken);
        btnTestAccessToken.setOnClickListener(this);
        btnTestAccessToken.setIndeterminateProgressMode(true);

        btnTestDeleteUser = (CircularProgressButton) findViewById(R.id.btn_test_deleteuser);
        btnTestDeleteUser.setOnClickListener(this);
        btnTestDeleteUser.setIndeterminateProgressMode(true);

        btnTestRefreshToken = (CircularProgressButton) findViewById(R.id.btn_test_refreshtoken);
        btnTestRefreshToken.setOnClickListener(this);
        btnTestRefreshToken.setIndeterminateProgressMode(true);

        btnTestEndPointGet = (CircularProgressButton) findViewById(R.id.btn_test_endpoint_get);
        btnTestEndPointGet.setOnClickListener(this);
        btnTestEndPointGet.setIndeterminateProgressMode(true);

        btnTestEndPointPost = (CircularProgressButton) findViewById(R.id.btn_test_endpoint_put);
        btnTestEndPointPost.setOnClickListener(this);
        btnTestEndPointPost.setIndeterminateProgressMode(true);

        btnTestCache = (CircularProgressButton) findViewById(R.id.btn_test_cache);
        btnTestCache.setOnClickListener(this);
        btnTestCache.setIndeterminateProgressMode(true);

        btnClearCache = (CircularProgressButton) findViewById(R.id.btn_clear_cache);
        btnClearCache.setOnClickListener(this);
        btnClearCache.setIndeterminateProgressMode(true);

        final ZivaCareConfig config = new ZivaCareConfig(this, false);
        config.setClientSecret(clientSecret);
        config.setClientId(clientId);
        config.setClientUserId(clientUserId);
        config.setClientUserName(clientUserName);
        mZivaSdk = new ZivaCareSDK(this, config);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        ((CircularProgressButton) v).setProgress(10);

        switch (id) {
            case R.id.btn_test_createuser:
                createUser();
                break;
            case R.id.btn_test_accesstoken:
                getAccessToken();
                break;
            case R.id.btn_test_refreshtoken:
                refreshToken();
                break;
            case R.id.btn_test_deleteuser:
                deleteUser();
                break;
            case R.id.btn_test_endpoint_get:
                testEndpointGet();
                break;
            case R.id.btn_test_endpoint_put:
                testEndpointPost();
                break;
            case R.id.btn_test_cache:
                testCache();
                break;
            case R.id.btn_clear_cache:
                clearCache();
                break;
        }
    }

    private void createUser() {
        mZivaSdk.createUser(new ZivacareCallback() {
            @Override
            public void onSuccess(ZivaCareResponse response) {
                btnTestCreateUser.setProgress(0);
                createPopupWithResults(response,
                        "response code %s create user: ");
            }

            @Override
            public void onError(ZivaCareResponse response) {
                btnTestCreateUser.setProgress(0);
                createPopupWithResults(response,
                        "response code %s create user: ");
            }
        });
    }

    private void testCache() {
        try {
            btnTestCache.setProgress(0);
            final AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle("ZivaCareConfig and Cache test:")
                    .setMessage(mZivaSdk.getConfig().getConfigDebugStr())
                    .setPositiveButton(R.string.action_ok, null)
                    .show();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private void clearCache() {
        try {
            btnClearCache.setProgress(0);
            mZivaSdk.getConfig().clean();
            Toast.makeText(SdkTestActivity.this, "Cache cleared", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private void testEndpointPost() {
        ZivaCareEndpoint endpoint = new ZivaCareActivitiesEndpoint(mZivaSdk,
                mZivaSdk.getConfig());
        String[][] dataValues = new String[][]{{
                "2014-09-16T15:52:01+0000",
                "2014-09-16T15:55:01+0000", "jogging", "3", "1.1",
                "30", "100", "Europe/Bucharest"}};
        endpoint.setData(1, ZivaCareEndpoint.OP_INSERT,
                "fitbit", null, dataValues, new ZivacareCallback() {
                    @Override
                    public void onSuccess(ZivaCareResponse response) {
                        btnTestEndPointPost.setProgress(0);
                        createPopupWithResults(response,
                                "response code %s endpoint post: ");
                    }

                    @Override
                    public void onError(ZivaCareResponse response) {
                        btnTestEndPointPost.setProgress(0);
                        createPopupWithResults(response,
                                "response code %s endpoint post: ");
                    }
                });

    }

    private void testEndpointGet() {
        ZivaCareEndpoint endpoint = new ZivaCareProfileEndpoint(mZivaSdk,
                mZivaSdk.getConfig());
        endpoint.getAll(1, new ZivacareCallback() {
            @Override
            public void onSuccess(ZivaCareResponse response) {
                btnTestEndPointGet.setProgress(0);
                createPopupWithResults(response,
                        "response code %s endpoint get: ");
            }

            @Override
            public void onError(ZivaCareResponse response) {
                btnTestEndPointGet.setProgress(0);
                createPopupWithResults(response,
                        "response code %s endpoint get: ");
            }
        });

    }

    private void refreshToken() {
        mZivaSdk.refreshToken(new ZivacareCallback() {
            @Override
            public void onSuccess(ZivaCareResponse response) {
                btnTestRefreshToken.setProgress(0);
                createPopupWithResults(response,
                        "response code %s refresh token: ");
            }

            @Override
            public void onError(ZivaCareResponse response) {
                btnTestRefreshToken.setProgress(0);
                createPopupWithResults(response,
                        "response code %s refresh token: ");
            }
        });


    }

    private void deleteUser() {
        mZivaSdk.deleteUser(new ZivacareCallback() {
            @Override
            public void onSuccess(ZivaCareResponse response) {
                btnTestDeleteUser.setProgress(0);
                createPopupWithResults(response,
                        "response code %s delete user: ");
            }

            @Override
            public void onError(ZivaCareResponse response) {
                btnTestDeleteUser.setProgress(0);
                createPopupWithResults(response,
                        "response code %s delete user: ");
            }
        });


    }

    private void getAccessToken() {
        mZivaSdk.login(new ZivacareCallback() {
            @Override
            public void onSuccess(ZivaCareResponse response) {
                btnTestAccessToken.setProgress(0);
                createPopupWithResults(response,
                        "response code %s get token: ");
            }

            @Override
            public void onError(ZivaCareResponse response) {
                btnTestAccessToken.setProgress(0);
                createPopupWithResults(response,
                        "response code %s get token: ");
            }
        });
    }

    private void createPopupWithResults(ZivaCareResponse response,
                                        String strToFormat) {
        final AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle(String.format(strToFormat,
                        response.getResponseCode()))
                .setMessage(response.getResponseString())
                .setPositiveButton(R.string.action_ok, null)
                .show();

        if (ZivaCareConfig.isDebugEnabled()) {
            Log.d(LOG_TAG, String.format(strToFormat,
                    response.getResponseCode())
                    + response);
        }
    }


}
