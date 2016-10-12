package com.example.administrator.jdbc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jdbc.domin.Person;
import com.example.administrator.jdbc.util.JdbcUtilForAndroid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private TextView textView;
    private String name;
    private String id;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv_name);
        JdbcUtilForAndroid jdbcUtilForAndroid = new JdbcUtilForAndroid(this);
        String sql = "select * from test where id = ?";
        Map<Integer, String> sqlParameter = new HashMap<>();
        sqlParameter.put(1, "1");
        String table_name = "test";
//        jdbcUtilForAndroid.ExcuteQuery(Person.class, table_name, new String[]{"name"}, new String[]{"id"}, new String[]{"1"}, new JdbcUtilForAndroid.OnQueryListener() {
//            @Override
//            public void onQueryed() {
//                Log.i(TAG, "查询结果回调");
//            }
//        });
        jdbcUtilForAndroid.ExcuteInsert("test", new String[]{"name", "infomation"}, new String[]{"代码新添加的name", "新添加的infomation"}, null);
        jdbcUtilForAndroid.ExcuteDelete(table_name, new String[]{"name"}, new String[]{"王力宏"}, null);
        final String TAG = "MyTest";
        jdbcUtilForAndroid.ExcuteQuery(Person.class, "test", new String[]{"name", "infomation"}, null, null, new JdbcUtilForAndroid.OnQueryListener() {
            @Override
            public <T> void onQueryed(List<T> ts) {
                List<Person> persons = (List<Person>) ts;
                Log.i(TAG, "查询结果:" + persons);
            }
        });
    }
}
