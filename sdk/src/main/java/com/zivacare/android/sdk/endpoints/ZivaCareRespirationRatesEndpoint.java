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

/**
 * Created by Stelian Morariu on 22/6/2015.
 */
public class ZivaCareRespirationRatesEndpoint extends
        ZivaCareEndpoint {
    public static final String[] DATA_NAMES = new String[]{};

    public ZivaCareRespirationRatesEndpoint(@NonNull ZivaCareSDK sdk,ZivaCareConfig config) {
        super(sdk);
        this.config = config;
    }

    @Override
    public String getType() {
        return TYPE_RESPIRATION_RATES;
    }

    @Override
    public String[] getDataNames() {
        return DATA_NAMES;
    }
}
