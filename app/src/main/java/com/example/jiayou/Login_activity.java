package com.example.jiayou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Mods.Staticobject;
import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import object.Users;
import service.MyService;
import utils.ActivityManager;

public class Login_activity extends ActivityManager implements OnClickListener {
    private EditText username;
    private EditText password;
    private Button submit;
    private TextView regist, user_fogetPassword;
    private String name;
    private String pas;
    Users bu = new Users();
    private List<String> list = new ArrayList<String>();
    private Handler uihandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0001:
                    Toast.makeText(Login_activity.this, "登陆成功", 0).show();
                    break;
                case 0002:
                    Toast.makeText(Login_activity.this, "用户名或密码错误", 0).show();
                    password.setText("");


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initview();

    }
    /**
     *
初始化view
     * */
    private void initview() {
        user_fogetPassword = (TextView) findViewById(R.id.user_fogetPassword);
        user_fogetPassword.setOnClickListener(this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        regist = (TextView) findViewById(R.id.regist);
        submit.setOnClickListener(this);
        regist.setOnClickListener(this);
    }
    /**
     *
显示提示信息，并处理退出程序事件
     * */
    private void showTips() {
        AlertDialog alertDialog = new AlertDialog.Builder(Login_activity.this)
                .setTitle("退出程序")
                .setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Login_activity.this, MyService.class);
                        android.app.ActivityManager myAM = (android.app.ActivityManager) Login_activity.this
                                .getSystemService(Context.ACTIVITY_SERVICE);
                        myAM.killBackgroundProcesses(getPackageName());
                        android.os.Process.killProcess(android.os.Process.myPid());
                       // FirstApplication.getInstance().exit();
                        Login_activity.this.finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); //创建对话框
        alertDialog.show(); // 显示对话框
    }

    /**
     *
处理点击退出事件
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.showTips();
            return false;
        }
        return false;
    }
    /**
     *
更新本地缓存信息
     * */
    private void cache() {


        BmobQuery<Users> query = new BmobQuery<Users>();
        query.getObject(getApplicationContext(), bu.getObjectId(), new GetListener<Users>() {

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(Users arg0) {
                // TODO Auto-generated method stub
                list = arg0.getCar_id();
                Log.i("list", list.toString());
                Users u = new Users();
                u.setObjectId(bu.getObjectId());
                u.setUsername(name);
                u.setCar_id(list);
                Log.i("list", list.toString());
                Staticobject.currentuser = u;
                new Cache().Writecache(getApplicationContext(), bu.getObjectId(), name, list);
            }
        });


    }
    /**
     *
处理点击事件
     * */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.submit:
                name = username.getText().toString();
                pas = password.getText().toString();
                Log.i("c错误", name);
                Log.i("c错误", pas);
                bu.setUsername(name);
                bu.setPassword(pas);

                Log.i("username", bu.getUsername());

                bu.login(Login_activity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        uihandler.sendEmptyMessage(0001);
                        Intent intent = new Intent();
                        cache();
                        intent.putExtra("ObjectId", bu.getObjectId());
                        intent.setClass(Login_activity.this, Main_activity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        Log.i("c错误", arg1);
                        uihandler.sendEmptyMessage(0002);
                    }
                });
                break;
            case R.id.regist:
                Intent intent = new Intent(Login_activity.this, Regist.class);
                startActivity(intent);
                break;
            case R.id.user_fogetPassword:
                Intent int1 = new Intent(this, FogotPas_activity.class);
                startActivity(int1);
            default:
                break;

        }
    }


}

