package com.example.administrator.jdbc.util;

import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/14.
 * 单例的Toast
 */
public class ToastUtil {
    private static Toast mToast;

    public static void show(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(UIUtil.getContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }
}
