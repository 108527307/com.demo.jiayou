package com.example.jiayou;




import cache.Cache;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobDate;
import utils.ActivityManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class Welcome_activity extends ActivityManager {
	private Handler handler=new Handler();
	private String ObjectId;
private boolean first()
{
	
	ObjectId=new Cache().GetObjectId(Welcome_activity.this);
	if("-1".equals(ObjectId)){
	return false;
	}else
	{
		return true;
	}
	
   
}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//              requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		Bmob.initialize(this, "0785ed0dcbd40b0acc360b6fb070931d");	
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent1=new Intent(Welcome_activity.this,Login_activity.class);
				Intent intent2=new Intent(Welcome_activity.this,Main_activity.class);

					if(!first())
					{
						startActivity(intent1);
					finish();
					}
					else {
						startActivity(intent2);
						finish();
					}

				
			}
		}, 2000);
		
	}

	
	
}
