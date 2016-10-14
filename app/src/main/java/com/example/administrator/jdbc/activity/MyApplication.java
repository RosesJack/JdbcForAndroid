package com.example.administrator.jdbc.activity;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Administrator on 2016/10/11.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static Context mApplicationContext;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getApplicationContext();
        mHandler = new Handler();
        // 设置未捕获的异常处理
        Thread.currentThread().setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        // 尽快kill进程意味着尽快重新开启主程序
                        Log.e(TAG, "异常被捕获了,异常信息是：" + ex.toString());
//                        android.os.Process.killProcess(android.os.Process
//                                .myPid());
                    }
                });
    }


    public static Context getContext() {
        return mApplicationContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }
}
