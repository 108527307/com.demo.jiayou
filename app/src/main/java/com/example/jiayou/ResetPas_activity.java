package com.example.jiayou;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.logging.LogRecord;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import object.Cars;
import object.Users;
import utils.ActivityManager;

/**
 * Created by Administrator on 2016/6/9 0009.
 */
public class ResetPas_activity extends ActivityManager implements View.OnClickListener {
    EditText newPas,newPasOk;
    Button user_reset;
    Handler uihandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case 0010:
                    List<Users> u=(List<Users>)msg.obj;
                    Updata(u);
                    break;
                case 0020:
                    Toast.makeText(ResetPas_activity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                case 0101:
                    Toast.makeText(ResetPas_activity.this,"重置成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 0102:
                    Toast.makeText(ResetPas_activity.this,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
                initView();
    }
    /**
     *
初始化view
     * */
    private void initView() {
        newPas= (EditText) findViewById(R.id.newPassword);
        newPasOk= (EditText) findViewById(R.id.newPasswordOk);
        user_reset= (Button) findViewById(R.id.user_reset);
       user_reset.setOnClickListener(this);
    }
    /**
     *
查询原来的车辆信息
     * */
    private void QueryCars(String username){
        BmobQuery<Users> query=new BmobQuery<>();
        query.addWhereEqualTo("username",username);
        query.findObjects(this, new FindListener<Users>() {
            @Override
            public void onSuccess(List<Users> list) {
                Message msg=new Message();
                msg.arg1=0010;
                msg.obj=list;
                uihandler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {
                Message msg=new Message();
                msg.arg1=0020;
                msg.obj=s;
                uihandler.sendMessage(msg);
            }
        });
    }
    /**
     *
根据号码重置密码
     * */
private void Updata(List<Users> u){
    Users user=new Users();
    user.resetPasswordBySMSCode(this, String.valueOf(getIntent().getIntExtra("Code",-1)), newPas.getText().toString().trim(), new ResetPasswordByCodeListener() {

        @Override
        public void done(BmobException ex) {
            // TODO Auto-generated method stub
            if (ex == null) {
                Log.i("smile", "密码重置成功");
                Message msg = new Message();
                 msg.arg1 = 0101;
                uihandler.sendMessage(msg);
            } else {
                Log.i("smile", "重置失败：code =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                Message msg = new Message();
            msg.arg1 = 0102;
           msg.obj = ex.getLocalizedMessage();
            uihandler.sendMessage(msg);
            }
        }
    });
}
    @Override
    public void onClick(View v) {
        String newpas=newPas.getText().toString().trim();
        String newpasok=newPasOk.getText().toString().trim();
        if(newpas.equals("")||newpasok.equals(""))
            Toast.makeText(this,"请填完提交",Toast.LENGTH_LONG).show();
        else if(!newpas.equals(newpasok))
            Toast.makeText(this,"密码输入不一致",Toast.LENGTH_LONG).show();
        else{
              QueryCars(getIntent().getStringExtra("phone"));
        }
    }
}
