package com.example.jiayou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cache.Cache;
import service.MyService;
import utils.ActivityManager;

/**
 * Created by Administrator on 2016/6/8 0008.
 */
public class MyAccount_activity extends ActivityManager implements View.OnClickListener {
    private LinearLayout myPwd;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
        Initinfor();
    }

    private void initView() {
        myPwd= (LinearLayout) findViewById(R.id.myPwd);
        logout= (Button) findViewById(R.id.logout);
        myPwd.setOnClickListener(this);
        logout.setOnClickListener(this);

    }
    /**
     *
初始化信息显示
     * */
private void Initinfor(){
    //Users user=getSharedPreferences
   String UserName=new Cache().GetName(this);
    String UserOb=new Cache().GetObjectId(this);
    TextView ob=(TextView)findViewById(R.id.myObject);
    ob.setText(UserOb);
    TextView name=(TextView)findViewById(R.id.myMobile);
    name.setText(UserName);
}
    /**
     *
处理点击事件
     * */
    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.logout:
              SharedPreferences user = getSharedPreferences("user", this.MODE_PRIVATE);
              SharedPreferences.Editor ed = user.edit();
              ed.clear();
              ed.commit();
              Intent intent=new Intent(MyAccount_activity.this, MyService.class);
              android.app.ActivityManager myAM = (android.app.ActivityManager) MyAccount_activity.this
                      .getSystemService(Context.ACTIVITY_SERVICE);
              List<android.app.ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(1000);
              if(myList.size()>0)
                  stopService(intent);
              Intent intent1 = new Intent(this, Login_activity.class);
              startActivity(intent1);
              finish();
              break;
          case R.id.myPwd:
                     Intent intent2=new Intent(this,UpdatePas_activity.class);
              startActivity(intent2);
              break;
          case R.id.iv_back:
              finish();
              break;
          default:break;
}
    }
}
