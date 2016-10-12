package com.example.administrator.jdbc.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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
        StringBuilder sb = getQuerySql(table_name, selections, where, whereAgs);
        final String sql = sb.toString();
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
                    List<T> ts = new ArrayList<>();
                    while (resultSet.next()) {
                        //利用反射设置数据到person对象中
                        T t = clazz.newInstance();
                        Method[] methods = clazz.getDeclaredMethods();
                        for (Method method : methods) {
                            String methodName = method.getName();
                            for (String selection : selections) {
                                //得到set方法
                                if (methodName.contains("set") && methodName.contains(selection.substring(1, selection.length()))) {
                                    Log.i(TAG, "方法名称：" + methodName);
                                    method.invoke(t, resultSet.getString(selection));
                                }
                            }
                        }
                        ts.add(t);
                    }
                    Log.i(TAG, "ts的查询结果是：" + ts);
                    if (onQueryListener != null) {
                        onQueryListener.onQueryed(ts);
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
     * 根据传入参数拼接出搜索的字符串
     *
     * @param table_name 表名
     * @param selections 搜索结果
     * @param where      搜索条件
     * @param whereAgs   搜索条件参数
     * @return 字符
     */
    @NonNull
    private StringBuilder getQuerySql(String table_name, String[] selections, String[] where, String[] whereAgs) {
        //拼接sql语句
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        int len = selections.length;
        for (int i = 0; i < len; i++) {
            sb.append(selections[i]);
            if (i < len - 1) {
                sb.append(",");
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
        return sb;
    }

    /**
     * 根据传入参数拼接出删除的字符串
     *
     * @param table_name 表名
     * @param where      搜索条件
     * @param whereAgs   搜索条件参数
     * @return 字符
     */
    @NonNull
    private StringBuilder getDeleteSql(String table_name, String[] where, String[] whereAgs) {
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
        return sb;
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
     * @param table_name
     * @param selection
     * @param selectionArg
     * @param where
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
     * @param table_name
     * @param selections
     * @param where
     * @param whereAgs
     * @param onDeleteListener
     */

    public void ExcuteDelete(String table_name, String[] where, final String[] whereAgs, final OnDeleteListener onDeleteListener) {
        //DELETE FROM test WHERE NAME= 'wang'
        StringBuilder sb = getDeleteSql(table_name, where, whereAgs);
        final String sql = sb.toString();
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
                    boolean result = preparedStatement.execute();
                    if (onDeleteListener != null) {
                        onDeleteListener.onDeleted(result);
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
     * @param table_name
     * @param where
     * @param whereAgs
     * @param onInsertListener
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
                    boolean result = preparedStatement.execute();
                    if (onInsertListener != null) {
                        onInsertListener.onInserted(result);
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
     * @param table_name
     * @param where
     * @param whereAgs
     * @param onUpdateListener
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
                    int result = preparedStatement.executeUpdate();
                    if (onUpdateListener != null) {
                        onUpdateListener.onUpdated(result);
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
