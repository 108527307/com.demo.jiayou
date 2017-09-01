package com.example.jiayou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.List;
import java.util.logging.LogRecord;

import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import object.Users;
import service.MyService;
import utils.ActivityManager;

/**
 * Created by Administrator on 2016/6/8 0008.
 */
public class UpdatePas_activity extends ActivityManager implements View.OnClickListener {

    private EditText Old,New,NewOk;
    Button Ok,back;
    private Handler Uihandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case 0101:
                    Toast.makeText(getApplicationContext(), "密码已修改", Toast.LENGTH_SHORT).show();
                    SharedPreferences user = getSharedPreferences("user", getApplicationContext().MODE_PRIVATE);
                    SharedPreferences.Editor ed = user.edit();
                    ed.clear();
                    ed.commit();
                    Intent intent=new Intent(UpdatePas_activity.this, MyService.class);
                    android.app.ActivityManager myAM = (android.app.ActivityManager) UpdatePas_activity.this
                            .getSystemService(Context.ACTIVITY_SERVICE);
                    List<android.app.ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(1000);
                    if(myList.size()>0)
                        stopService(intent);
                    Intent intent1 = new Intent(UpdatePas_activity.this, Login_activity.class);
                    startActivity(intent1);
                    finish();
                    break;
                case 0102:
                    UpdatePas(msg.obj.toString());
                    break;
                case 0103:
                    Toast.makeText(getApplicationContext(),"发生错误："+msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        initView();
    }

    private void initView() {
        back= (Button) findViewById(R.id.iv_back);
        back.setOnClickListener(this);
        Old= (EditText) findViewById(R.id.oldPassword);
        New= (EditText) findViewById(R.id.newPassword);
        NewOk= (EditText) findViewById(R.id.newPasswordOk);
        Ok= (Button) findViewById(R.id.user_update);
        Ok.setOnClickListener(this);

    }
    /**
     *
判断老密码是否正确
     * */
    private void JudgePas(String OldPas, final String NewPas){
        BmobQuery<Users> query = new BmobQuery<Users>();
        query.addWhereEqualTo("password", OldPas);
        query.findObjects(this, new FindListener<Users>() {
            @Override
            public void onSuccess(List<Users> list) {
                if(list.size()==1)
                {
                    Message msg=new Message();
                    msg.arg1=0102;
                    msg.obj=NewPas;
                    Uihandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int i, String s) {
                Message msg=new Message();
                msg.arg1=0103;
                msg.obj=s;
                Uihandler.sendMessage(msg);
            }
        });
    }
    /**
     *更新密码

     * */
private void UpdatePas(String Newpas){


    Users user=new Users();
    user.setCar_id(new Cache().Getcachecars(this));
    user.setPassword(Newpas);
    user.update(this, new Cache().GetObjectId(this),new UpdateListener() {
        @Override
        public void onSuccess() {
           Message msg=new Message();
            msg.arg1=0101;
            Uihandler.sendMessage(msg);
        }

        @Override
        public void onFailure(int i, String s) {

        }
    });

}
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.user_update) {
            String OldPas = Old.getText().toString().trim();
            String NewPas=New.getText().toString().trim();
            String NewOkPas=NewOk.getText().toString().trim();
            if("".equals(OldPas)||"".equals(NewOkPas)||"".equals(NewPas)){
                Toast.makeText(this,"请完成输入后提交",Toast.LENGTH_LONG).show();
            }else if(!NewOkPas.equals(NewPas))
                Toast.makeText(this,"两次密码不一致",Toast.LENGTH_LONG).show();
            else{
                JudgePas(OldPas,NewPas);
            }
        }else if(v.getId()==R.id.iv_back){
            finish();
        }
    }
}
