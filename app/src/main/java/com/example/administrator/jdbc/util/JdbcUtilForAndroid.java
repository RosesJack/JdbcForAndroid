package com.example.administrator.jdbc.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.administrator.jdbc.activity.MyApplication;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */
public class JdbcUtilForAndroid {
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Context context;
    private String TAG = "JdbcUtilForAndroid";

    public JdbcUtilForAndroid(Context context) {
        this.context = context;
    }

    /**
     * 执行数据库搜索的方法
     *
     * @param clazz           class对象
     * @param table_name      表名
     * @param selections      要得到的结果
     * @param where           搜索条件
     * @param whereAgs        搜索条件的参数
     * @param onQueryListener 结果传出的回调接口
     * @param <T>             反射的类的泛型
     */
    public <T> void ExcuteQuery(final Class<T> clazz, String table_name, final String[] selections, String[] where, final String[] whereAgs, final OnQueryListener onQueryListener) {
        final String sql = getQuerySql(table_name, selections, where, whereAgs);
        Log.i(TAG, "sql = " + sql);
        new Thread() {
            @Override
            public void run() {
                MyJdbcUtils myJdbcUtils = new MyJdbcUtils(context);
                //1.加载驱动
                try {
                    connection = myJdbcUtils.getConnection();
                    //2.创建连接
                    preparedStatement = connection.prepareStatement(sql);
                    if (whereAgs != null && whereAgs.length > 0) {
                        int len = whereAgs.length;
                        for (int i = 1; i < len + 1; i++) {
                            preparedStatement.setString(i, whereAgs[i - 1]);
                        }
                    }
                    resultSet = preparedStatement.executeQuery();
                    final List<T> ts = new ArrayList<>();
                    while (resultSet.next()) {
                        //利用反射设置数据到person对象中
                        T t = clazz.newInstance();
                        Method[] methods = clazz.getDeclaredMethods();
                        for (Method method : methods) {
                            String methodName = method.getName();
                            if (selections == null || selections.length <= 0) {
                                if (methodName.contains("set")) {
                                    Log.i(TAG, "搜索的结果全部设置");
                                    String fieldName = changeString(methodName);
                                    method.invoke(t, resultSet.getString(fieldName));
                                }
                            } else {
                                for (String selection : selections) {
                                    //得到set方法
                                    if (methodName.contains("set") && methodName.contains(selection.substring(1, selection.length()))) {
                                        Log.i(TAG, "方法名称：" + methodName);
                                        method.invoke(t, resultSet.getString(selection));
                                    }
                                }
                            }
                        }
                        ts.add(t);
                    }
                    Log.i(TAG, "ts的查询结果是：" + ts);
                    if (onQueryListener != null) {
                        MyApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                onQueryListener.onQueryed(ts);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    myJdbcUtils.clearConn(connection, preparedStatement, resultSet);
                }
            }
        }.start();
    }

    /**
     * 改变字符串
     * 如：setUsername ---> username
     *
     * @param result
     */
    public String changeString(String result) {
        result = result.substring(3, result.length());
        String strTemp = String.valueOf(result.charAt(0));
        String firstStr = strTemp.toLowerCase();
        return firstStr + result.substring(1, result.length());
    }

    /**
     * 根据传入参数拼接出搜索的字符串
     *
     * @param table_name 表名
     * @param selections 搜索结果
     * @param where      搜索条件
     * @param whereAgs   搜索条件参数
     * @return 字符
     */
    @NonNull
    private String getQuerySql(String table_name, String[] selections, String[] where, String[] whereAgs) {
        //拼接sql语句
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        int len;
        if (selections == null || selections.length <= 0) {
            sb.append(" * ");
        } else {
            len = selections.length;
            for (int i = 0; i < len; i++) {
                sb.append(selections[i]);
                if (i < len - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(" from " + table_name);
        if (where != null && where.length > 0 && whereAgs != null && whereAgs.length > 0) {
            sb.append(" where ");
            len = where.length;
            for (int i = 0; i < len; i++) {
                sb.append(where[i]);
                sb.append("=");
                sb.append("?");
                if (i < len - 1) {
                    sb.append(" and ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 根据传入参数拼接出删除的字符串
     *
     * @param table_name 表名
     * @param where      搜索条件
     * @param whereAgs   要删除条件参数
     * @return 字符
     */
    @NonNull
    private String getDeleteSql(String table_name, String[] where, String[] whereAgs) {
        //拼接sql语句
        //DELETE FROM test WHERE NAME= 'wang'
        StringBuilder sb = new StringBuilder();
        sb.append("delete from " + table_name);
        if (where != null && where.length > 0 && whereAgs != null && whereAgs.length > 0) {
            sb.append(" where ");
            int len = where.length;
            for (int i = 0; i < len; i++) {
                sb.append(where[i]);
                sb.append("=");
                sb.append("'" + whereAgs[i] + "'");
                if (i < len - 1) {
                    sb.append(" and ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 根据传入参数拼接出数据库添加操作的字符串
     *
     * @param table_name 表名
     * @param where      搜索条件
     * @param whereAgs   搜索条件参数
     * @return 字符
     */
    @NonNull
    private String getInsertSql(String table_name, String[] where, String[] whereAgs) {
        //拼接sql语句
        //INSERT INTO test(NAME,account) VALUES ( '老王','110')
        //拼接sql语句
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + table_name + " ( ");
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" values ( ");
        if (where != null && where.length > 0 && whereAgs != null && whereAgs.length > 0) {
            int len = where.length;
            for (int i = 0; i < len; i++) {
                sb.append(where[i]);
                sb2.append("'" + whereAgs[i] + "'");
                if (i < len - 1) {
                    sb.append(" , ");
                    sb2.append(" , ");
                } else {
                    sb.append(" ) ");
                    sb2.append(" ) ");
                }
            }
        }
        return sb.toString() + sb2.toString();
    }

    /**
     * 得到更新的数据库语句
     *
     * @param table_name   表名
     * @param selection    筛选的数据
     * @param selectionArg 筛选数据的条件
     * @param where        搜索条件
     * @return
     */
    private String getUpdateSql(String table_name, String[] selection, String[] selectionArg, String[] where) {
        //拼接sql语句
        //UPDATE  test SET  NAME='小王' WHERE id = '1'
        //拼接sql语句
        StringBuilder sb = new StringBuilder();
        sb.append("update " + table_name + " set ");
        int len = selection.length;
        for (int i = 0; i < len; i++) {
            sb.append(selection[i]);
            sb.append("=");
            sb.append("'" + selectionArg[i] + "'");
            if (i < len - 1) {
                sb.append(",");
            }
        }
        sb.append(" where ");
        if (where != null && where.length > 0) {
            len = where.length;
            for (int i = 0; i < len; i++) {
                sb.append(where[i]);
                sb.append("=");
                sb.append(" ? ");
                if (i < len - 1) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 数据库删除操作
     *
     * @param table_name       表名
     * @param where            条件
     * @param whereAgs         条件参数
     * @param onDeleteListener 结果回调
     */

    public void ExcuteDelete(String table_name, String[] where, final String[] whereAgs, final OnDeleteListener onDeleteListener) {
        //DELETE FROM test WHERE NAME= 'wang'
        final String sql = getDeleteSql(table_name, where, whereAgs);
        Log.i(TAG, "sql = " + sql);
        new Thread() {
            @Override
            public void run() {
                MyJdbcUtils myJdbcUtils = new MyJdbcUtils(context);
                //1.加载驱动
                try {
                    connection = myJdbcUtils.getConnection();
                    //2.创建连接
                    preparedStatement = connection.prepareStatement(sql);
                    final boolean result = preparedStatement.execute();
                    if (onDeleteListener != null) {
                        MyApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                onDeleteListener.onDeleted(result);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    myJdbcUtils.clearConn(connection, preparedStatement, resultSet);
                }
            }
        }.start();
    }


    /**
     * 执行数据库插入操作
     *
     * @param table_name       表名
     * @param where            条件
     * @param whereAgs         条件参数
     * @param onInsertListener 结果回调
     */
    public void ExcuteInsert(String table_name, String[] where, final String[] whereAgs, final OnInsertListener onInsertListener) {
        //INSERT INTO test(NAME,account) VALUES ( '老王','110')
        final String sql = getInsertSql(table_name, where, whereAgs);
        Log.i(TAG, "sql = " + sql);
        new Thread() {
            @Override
            public void run() {
                MyJdbcUtils myJdbcUtils = new MyJdbcUtils(context);
                //1.加载驱动
                try {
                    connection = myJdbcUtils.getConnection();
                    //2.创建连接
                    preparedStatement = connection.prepareStatement(sql);
                    final boolean result = preparedStatement.execute();
                    if (onInsertListener != null) {
                        MyApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                onInsertListener.onInserted(result);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    myJdbcUtils.clearConn(connection, preparedStatement, resultSet);
                }
            }
        }.start();
    }


    /**
     * 执行数据库更新操作
     *
     * @param table_name       表名
     * @param where            条件
     * @param whereAgs         条件参数
     * @param onUpdateListener 结果回调
     */
    public void ExcuteUpdate(String table_name, String[] selections, final String[] selectionArgs, final String[] where, final String[] whereAgs, final OnUpdateListener onUpdateListener) {
        //INSERT INTO test(NAME,account) VALUES ( '老王','110')
        Log.i(TAG, "ExcuteUpdate: 执行了");
        final String sql = getUpdateSql(table_name, selections, selectionArgs, where);
        Log.i(TAG, "sql = " + sql);
        new Thread() {
            @Override
            public void run() {
                MyJdbcUtils myJdbcUtils = new MyJdbcUtils(context);
                //1.加载驱动
                try {
                    connection = myJdbcUtils.getConnection();
                    //2.创建连接
                    preparedStatement = connection.prepareStatement(sql);
                    if (where != null && whereAgs.length > 0) {
                        int len = whereAgs.length;
                        for (int i = 0; i < len; i++) {
                            Log.i(TAG, "whereAgs:" + whereAgs[i]);
                            preparedStatement.setString(i + 1, whereAgs[i]);
                        }
                    }
                    final int result = preparedStatement.executeUpdate();
                    if (onUpdateListener != null) {
                        MyApplication.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                onUpdateListener.onUpdated(result);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    myJdbcUtils.clearConn(connection, preparedStatement, resultSet);
                }
            }
        }.start();
    }


    public interface OnQueryListener {
        <T> void onQueryed(List<T> ts);
    }


    public interface OnDeleteListener {
        void onDeleted(boolean result);
    }


    public interface OnUpdateListener {
        void onUpdated(int result);
    }


    public interface OnInsertListener {
        void onInserted(Boolean result);
    }
}
