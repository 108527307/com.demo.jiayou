package com.example.jiayou;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import adapter.StationListAdapter;
import object.Station;
import utils.ActivityManager;


public class StationListActivity extends ActivityManager
{

	private Context mContext;
	private ListView lv_station;
	private Button iv_back;
	private List<Station>stationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		mContext = this;
		initView();
	}

	private void initView() {
		Intent intent=getIntent();
		Bundle extras=intent.getExtras();
		stationList=extras.getParcelableArrayList("list");
		lv_station=(ListView)findViewById(R.id.lv_station);
		iv_back = (Button) findViewById(R.id.iv_back);
		iv_back.setVisibility(View.VISIBLE);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lv_station = (ListView) findViewById(R.id.lv_station);
		setValues(stationList);

	}

	/**
	 *
	 * 这个方法用于设置数据
	 * **/
	private void setValues(List<Station> list) {
		// TODO Auto-generated method stub
		StationListAdapter stationadapter=new StationListAdapter(mContext, list);
		lv_station.setAdapter(stationadapter);
	    lv_station.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,StationInfoActivity.class);
				intent.putExtra("s", (Parcelable) stationList.get(position));
				intent.putExtra("locLat", getIntent().getDoubleExtra("locLat", 0));
				intent.putExtra("locLon", getIntent().getDoubleExtra("locLon", 0));
				startActivity(intent);
			}
		});
	}


}
