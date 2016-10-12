package com.example.administrator.jdbc.activity;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/10/11.
 */
public class MyApplication extends Application {

    private static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
    }

    public static Context getContext() {
        return mApplicationContext;
    }
}
