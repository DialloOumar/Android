package com.oumardiallo636.gtuc.troskymate.Utility;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by oumar on 3/28/18.
 */

public class HttpCaller {

    private static OkHttpClient okHttpClient;

    private HttpCaller(){
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getInstance(){
        if (okHttpClient == null){
            new HttpCaller();
            return okHttpClient;
        }else {
            return okHttpClient;
        }
    }

}
