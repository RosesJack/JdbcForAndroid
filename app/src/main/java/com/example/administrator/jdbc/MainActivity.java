package com.example.administrator.jdbc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.jdbc.domin.Person;
import com.example.administrator.jdbc.util.JdbcUtilForAndroid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.add)
    Button mAdd;
    @InjectView(R.id.delete)
    Button mDelete;
    @InjectView(R.id.update)
    Button mUpdate;
    @InjectView(R.id.query)
    Button mQuery;
    @InjectView(R.id.show)
    TextView mShow;
    private String TAG = "MainActivity";
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private TextView textView;
    private String name;
    private String id;
    private Context context = this;
    private JdbcUtilForAndroid mJdbcUtilForAndroid;
    private String mTable_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mJdbcUtilForAndroid = new JdbcUtilForAndroid(this);
        mTable_name = "test";
    }

    @OnClick({R.id.add, R.id.delete, R.id.update, R.id.query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                mJdbcUtilForAndroid.ExcuteInsert(mTable_name, new String[]{"name", "infomation"}, new String[]{"wang", "wangs infomation"}, null);
                break;
            case R.id.delete:
                mJdbcUtilForAndroid.ExcuteDelete(mTable_name, new String[]{"name"}, new String[]{"wang"}, null);
                break;
            case R.id.update:
                mJdbcUtilForAndroid.ExcuteUpdate(mTable_name, new String[]{"name"}, new String[]{"wang changed"}, new String[]{"name"}, new String[]{"wang"}, null);
                break;
            case R.id.query:
                mJdbcUtilForAndroid.ExcuteQuery(Person.class, mTable_name, new String[]{"name", "infomation"}, null, null, new JdbcUtilForAndroid.OnQueryListener() {
                    @Override
                    public <T> void onQueryed(List<T> ts) {
                        List<Person> persons = (List<Person>) ts;
                        Log.i(TAG, "查询结果:" + persons);
                        mShow.setText(persons.toString());
                    }
                });
                break;
        }
    }
}
