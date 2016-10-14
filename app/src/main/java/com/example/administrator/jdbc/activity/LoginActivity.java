package com.example.administrator.jdbc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class LoginActivity extends Activity {

    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.bt_login)
    Button mBtLogin;
    @InjectView(R.id.bt_register)
    Button mBtRegister;
    private String mUsername;
    private String mPassword;
    private JdbcUtilForAndroid mDb;
    private String table_name = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        mDb = new JdbcUtilForAndroid(UIUtil.getContext());
    }

    @OnClick({R.id.bt_login, R.id.bt_register})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bt_login:
                mPassword = mEtPassword.getText().toString();
                mUsername = mEtUsername.getText().toString();
                confirmUserOnServer();
                break;
            case R.id.bt_register:
                intent = new Intent(UIUtil.getContext(), RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void confirmUserOnServer() {
        try {
            mPassword = MD5Util.encord(mPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mDb.ExcuteQuery(User.class, table_name, null,
                new String[]{"username", "password"},
                new String[]{mUsername, mPassword},
                new JdbcUtilForAndroid.OnQueryListener() {
                    @Override
                    public <T> void onQueryed(List<T> ts) {
                        if (ts != null && ts.size() > 0) {
//                            List<User> users = (List<User>) ts;
                            Intent intent = new Intent(UIUtil.getContext(), HomeActivity.class);
//                            intent.putExtra("users", users);
                            startActivity(intent);
                            finish();
                            ToastUtil.show("登录成功！");
                        } else {
                            ToastUtil.show("用户名或密码错误");
                        }
                    }
                });
    }
}
