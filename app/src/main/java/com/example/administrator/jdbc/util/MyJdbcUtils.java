package com.example.administrator.jdbc.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;

public class MyJdbcUtils {

    public static String drivername;
    public static String url;
    public static String username;
    public static String password;

    public MyJdbcUtils(Context context) {
        try {
            //文件的输入流
            InputStream inputStream = context.getResources().getAssets().open("db.properties");
            //创建properties对象
            Properties p = new Properties();
            //把文件的输入流放到对象里面
            p.load(inputStream);
            drivername = p.getProperty("drivername");
            url = p.getProperty("url");
            username = p.getProperty("username");
            password = p.getProperty("password");
            System.out.println(drivername);
            System.out.println(url);
            System.out.println(username);
            System.out.println(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws Exception {

        Class.forName(drivername);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }


    public void clearConn(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }
}






