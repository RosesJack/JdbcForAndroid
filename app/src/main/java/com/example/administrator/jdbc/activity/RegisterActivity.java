package com.example.administrator.jdbc.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.jdbc.R;
import com.example.administrator.jdbc.domin.User;
import com.example.administrator.jdbc.util.JdbcUtilForAndroid;
import com.example.administrator.jdbc.util.MD5Util;
import com.example.administrator.jdbc.util.ToastUtil;
import com.example.administrator.jdbc.util.UIUtil;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private String table_name = "user";
    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.et_confirm_password)
    EditText mEtConfirmPassword;
    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.bt_register)
    Button mBtRegister;
    private String mUsername;
    private String mPassword;
    private JdbcUtilForAndroid mDb;
    /**
     * 访问数据库是否结束
     */
    private boolean isSearchOver = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.bt_register)
    public void onClick() {
        if (!isSearchOver) {
            return;
        }
        mUsername = mEtUsername.getText().toString();
        mPassword = mEtPassword.getText().toString();
        String confirmPassword = mEtConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            ToastUtil.show("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(mPassword)) {
            ToastUtil.show("请确认密码");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.show("密码不能为空");
            return;
        }
        if (!TextUtils.equals(mPassword, confirmPassword)) {
            ToastUtil.show("两次密码输入不同");
            return;
        }
        mDb = new JdbcUtilForAndroid(UIUtil.getContext());
        searchUserFromServer();
        //正在进行网络数据库操作
        isSearchOver = false;
    }

    /**
     * 查询服务器用户是否已经存在
     */
    private void searchUserFromServer() {
        mDb.ExcuteQuery(User.class, table_name, new String[]{"password"}, new String[]{"username"}, new String[]{mUsername}, new JdbcUtilForAndroid.OnQueryListener() {
            @Override
            public <T> void onQueryed(List<T> ts) {
                if (ts != null && ts.size() > 0) {
                    ToastUtil.show("用户已经存在");
                } else {
                    insertUserToServer();
                }
            }
        });
    }

    /**
     * 用户信息写入数据库
     */
    private void insertUserToServer() {
        try {
            mPassword = MD5Util.encord(mPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mDb.ExcuteInsert(table_name, new String[]{"username", "password"}, new String[]{mUsername, mPassword}, new JdbcUtilForAndroid.OnInsertListener() {
            @Override
            public void onInserted(Boolean result) {
                if (!result) {
                    ToastUtil.show("注册成功,跳转到登录页面");
                    finish();
                } else {
                    ToastUtil.show("注册失败，请重试！");
                }
                //TODO 用线程池实现 网络数据库的操作
                isSearchOver = true;
            }
        });
    }
}
