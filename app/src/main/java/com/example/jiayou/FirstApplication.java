package com.example.jiayou;

import android.app.Activity;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.thinkland.sdk.android.JuheSDKInitializer;

import java.util.LinkedList;
import java.util.List;

public class FirstApplication extends Application {
	private List<Activity> mList = new LinkedList();
	private static FirstApplication instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
         //初始化聚合和百度
		SDKInitializer.initialize(getApplicationContext());
		JuheSDKInitializer.initialize(getApplicationContext());
	}
//	private FirstApplication() {
//	}
//	public synchronized static FirstApplication getInstance() {
//		if (null == instance) {
//			instance = new FirstApplication();
//		}
//		return instance;
//	}
//	// add Activity
//	public void addActivity(Activity activity) {
//		mList.add(activity);
//	}
//
//	public void exit() {
//		try {
//			for (Activity activity : mList) {
//				if (activity != null)
//					activity.finish();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			System.exit(0);
//		}
//	}
//	public void onLowMemory() {
//		super.onLowMemory();
//		System.gc();
//	}
}
