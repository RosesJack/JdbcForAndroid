package com.example.administrator.jdbc.activity;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Administrator on 2016/10/11.
 */
public class MyApplication extends Application {

    private static Context mApplicationContext;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
        mHandler = new Handler();
    }


    public static Context getContext() {
        return mApplicationContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }
}
