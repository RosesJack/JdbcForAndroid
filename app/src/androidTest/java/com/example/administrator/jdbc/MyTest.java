package com.example.administrator.jdbc;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.example.administrator.jdbc.domin.Person;
import com.example.administrator.jdbc.util.JdbcUtilForAndroid;
import com.example.administrator.jdbc.util.UIUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2016/10/11.
 */
public class MyTest extends InstrumentationTestCase {
    public void test() throws Exception {
        String TAG = "MyTest";
        //测试反射
        Class clazz = Person.class;
        clazz.getDeclaredMethods();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method :
                methods) {
            String methodName = method.getName();
            Log.i(TAG, "方法名称：" + methodName);
        }
    }

    public void testQuery() {
        final String TAG = "MyTest";
        JdbcUtilForAndroid jdbcUtilForAndroid = new JdbcUtilForAndroid(UIUtil.getContext());
        jdbcUtilForAndroid.ExcuteQuery(Person.class, "test", new String[]{"name"}, new String[]{"id"}, new String[]{"1"}, new JdbcUtilForAndroid.OnQueryListener() {
            @Override
            public <T> void onQueryed(List<T> ts) {
                Log.i(TAG, "查询结果回调");
                List<Person> persons = (List<Person>) ts;
                Log.i(TAG, "查询结果:" + persons);
            }
        });
    }
}
