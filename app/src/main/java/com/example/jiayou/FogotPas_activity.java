package com.example.jiayou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import object.Users;
import utils.ActivityManager;

/**
 * Created by Administrator on 2016/6/9 0009.
 */
public class FogotPas_activity extends ActivityManager implements View.OnClickListener {
    private Button user_fogot,getCode;
    private EditText phone,code;
    private int index=60;
    Timer tim;
     Users u=null;
    private Handler Uihandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case 0001:
                    Toast.makeText(FogotPas_activity.this, "验证错误:" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    code.setText("");
                    getCode.setText("获取验证码");
                    index=60;
                    break;
                case 0002:
                    Toast.makeText(FogotPas_activity.this, "验证成功",  Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(FogotPas_activity.this,ResetPas_activity.class);
                    intent.putExtra("phone",phone.getText().toString().trim());
                    startActivity(intent);
                    finish();
                    break;
                case 0103:
                    Toast.makeText(FogotPas_activity.this, "账户异常："+msg.obj.toString(),  Toast.LENGTH_SHORT).show();
                    index=60;
                    getCode.setText("获取验证码");
                    break;
                case 0004:
                    getCode.setText("("+index+")后可再次获取");
                    if(index==0)
                    {

                        tim.cancel();
                        Message m=new Message();
                        m.arg1=0006;
                        getCode.setClickable(true);
                        Uihandler.sendMessage(m);

                    }
                    break;
                case 0005:
                    Toast.makeText(FogotPas_activity.this, msg.obj.toString(),  Toast.LENGTH_SHORT).show();break;
                case 0006:
                    code.setText("");
                    getCode.setText("获取验证码");
                    index=60;
                    break;
                case 0102:
                    versms(phone.getText().toString().trim(),code.getText().toString().trim());
                default:break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_pwd);
        initView();
    }

    private void initView() {
       user_fogot= (Button) findViewById(R.id.user_fogot);
        getCode= (Button) findViewById(R.id.getCode);
        phone= (EditText) findViewById(R.id.phone);
        code= (EditText) findViewById(R.id.code);
        getCode.setOnClickListener(this);
        user_fogot.setOnClickListener(this);
    }
    /**
     * 发送短信
     */
    private void getsms(String phone)
    {
        tim=new Timer();
        tim.schedule(new TimerTask() {
            @Override
            public void run() {
                Message m = new Message();
                m.arg1 = 0004;

                getCode.setClickable(false);
                index--;
                Uihandler.sendMessage(m);
            }
        },0,1000);
        BmobSMS.requestSMSCode(this, phone, "叼炸天模板", new RequestSMSCodeListener() {

            @Override
            public void done(Integer arg0, BmobException arg1) {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.arg1 = 0005;
                //Log.i("sgvs", arg1.toString());
                String a = arg1 != null ? (arg1.toString()) : ("发送成功");
                msg.obj = a;
                Uihandler.sendMessage(msg);
            }
        });




    }
    /**
     * 验证短信验证码
     * @param phone
     * @param smscode
     */
    private void versms(String phone,String smscode)
    {

        BmobSMS.verifySmsCode(FogotPas_activity.this, phone, smscode, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException arg0) {
                if (arg0 == null) {
                    Message msg = new Message();
                    msg.arg1 = 0002;
                    Uihandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.arg1 = 0001;
                    msg.obj=arg0;
                    Uihandler.sendMessage(msg);
                }

            }
        });
    }
    /**
     * 判断是否存在该用户

    * */
    private void Judge(  String phone){
        BmobQuery<Users> query = new BmobQuery<Users>();
        query.addWhereEqualTo("username", phone);
        query.findObjects(this, new FindListener<Users>() {
            @Override
            public void onSuccess(List<Users> list) {
                if (list.size() == 1) {
                    Message msg = new Message();
                    msg.arg1 = 0102;
                   // msg.obj = NewPas;
                    Uihandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int i, String s) {
                Message msg = new Message();
                msg.arg1 = 0103;
                msg.obj = s;
                Uihandler.sendMessage(msg);
            }
        });
    }
    @Override
    public void onClick(View v) {
        String Phone = phone.getText().toString().trim();
        switch (v.getId()) {

            case R.id.getCode:

                if (Phone.equals(""))
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
                else {
                    getsms(Phone);
                }
                break;
            case R.id.user_fogot:
                String Code = code.getText().toString().trim();
                if (Code.equals(""))
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
                else if (Phone.equals(""))
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
                else {
                    Judge(Phone);
                }
                break;
            default:break;
        }
    }


}
