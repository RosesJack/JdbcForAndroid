package com.example.administrator.jdbc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;

public class MD5Util {
    public static String encord(String password)
            throws NoSuchAlgorithmException {
        if (TextUtils.isEmpty(password)) {
            return null;
        }
        MessageDigest digest = MessageDigest.getInstance("md5");
        byte[] passwordByte = digest.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : passwordByte) {
            int passwordInt = b & 0xff;
            String passwordStr = Integer.toHexString(passwordInt);
            if (passwordStr.length() == 1) {
                sb.append("0");
            }
            sb.append(passwordStr);
        }
        return sb.toString();
    }
}
