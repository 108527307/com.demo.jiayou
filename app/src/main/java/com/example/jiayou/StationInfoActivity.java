package com.example.jiayou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.platform.comapi.location.CoordinateType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.PriceListAdapter;
import cache.Cache;
import cn.bmob.v3.listener.SaveListener;
import fragment.BaiduMapFragment;
import myview.FloatingActionButton;
import object.Orders;
import object.Station;
import utils.ActivityManager;


public class StationInfoActivity extends ActivityManager implements OnClickListener {

	private Context mContext;
	private TextView tv_title_right, tv_name, tv_distance, tv_area, tv_addr;
	private Button iv_back;
	private ScrollView sv;
	private ListView lv_gast_price, lv_price;
	private Station mStation;
	private double lat,lon;
	private ArrayList station_price,gast_price;
	private Spinner GasClass;
	private EditText GasNum;
	private Button Submit;
	Animation DinDanUp;
	Animation DinDanDown;
	private RelativeLayout DinDan;
	private ArrayAdapter<String> adapter;
	private FloatingActionButton DisplayDinDan;
	public static List<Activity> activityList = new LinkedList<Activity>();
	private static final String[] a={"       #93汽","       #92汽","       #95汽","       #0柴","       #97汽"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gasstation_info);
		mContext = this;
		initView();
	}

	private void initView() {
		//获得从前方activity的数据
		activityList.add(StationInfoActivity.this);
		DisplayDinDan=(FloatingActionButton)findViewById(R.id.yudin);
		DisplayDinDan.setOnClickListener(this);
		Intent intent=getIntent();
		sv=(ScrollView)findViewById(R.id.sv);
		sv.setOnClickListener(this);
		Bundle extras=intent.getExtras();
		mStation=extras.getParcelable("s");
		lat=extras.getDouble("locLat", 0);
		lon=extras.getDouble("locLon",0);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_addr = (TextView) findViewById(R.id.tv_addr);
		iv_back = (Button) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		GasClass=new Spinner(StationInfoActivity.this);
		GasClass=(Spinner)findViewById(R.id.youhao);
		GasNum=(EditText)findViewById(R.id.shuliang);
		Submit=(Button)findViewById(R.id.submit);
		Submit.setOnClickListener(this);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, a);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		GasClass.setAdapter(adapter);
		DinDan=(RelativeLayout)findViewById(R.id.dindan);
		tv_title_right = (TextView) findViewById(R.id.tv_title_button);
		tv_title_right.setText("导航 >");
		tv_title_right.setOnClickListener(this);
		tv_title_right.setVisibility(View.VISIBLE);
		//表示本站油价
		this.lv_gast_price=(ListView)findViewById(R.id.lv_gast_price);
		//表示省空油价
		this.lv_price=(ListView)findViewById(R.id.lv_price);
		iv_back = (Button) findViewById(R.id.iv_back);
		iv_back.setVisibility(View.VISIBLE);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		lv_gast_price = (ListView) findViewById(R.id.lv_gast_price);
		lv_price = (ListView) findViewById(R.id.lv_price);
		sv = (ScrollView) findViewById(R.id.sv);
		setValues(mStation);
	}
	/**
	 *
更新云端订单表
	 * */
	private void AddOrders(final Orders order){
		order.save(StationInfoActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(StationInfoActivity.this, "预约提交成功！", Toast.LENGTH_SHORT).show();
				iv_back.setClickable(true);
				DisplayDinDan.setClickable(true);
				finish();


			}

			@Override
			public void onFailure(int i, String s) {
				Toast.makeText(StationInfoActivity.this,i+s,Toast.LENGTH_LONG).show();
				iv_back.setClickable(true);
				DisplayDinDan.setClickable(true);

			}
		});
	}
	/**
	 *显示相关信息

	 * */
	private void setValues(Station s) {
		// TODO Auto-generated method stub
		tv_name.setText(s.getName());
		tv_distance.setText(String.valueOf(s.getDistance())+"m");
		tv_area.setText(s.getArea());
		tv_addr.setText(s.getAddr());
		station_price=s.getGastPriceList();
		gast_price=s.getPriceList();

		PriceListAdapter station_adapter=new PriceListAdapter(mContext, station_price);
		lv_gast_price.setAdapter(station_adapter);
		PriceListAdapter adapter=new PriceListAdapter(mContext, gast_price);
		lv_price.setAdapter(adapter);
	}
	/**
	 *
导航
	 * */
	private void routeplanToNavi() {
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode	eNode = null;

		sNode=new BNRoutePlanNode(lon,lat,"我的位置", CoordinateType.GCJ02 );
		Log.i("d",mStation.toString());
		eNode=new BNRoutePlanNode(mStation.getLon(),mStation.getLat(),"目的地", CoordinateType.BD09LL);
		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new RoutePlanListener(sNode));
		}
	}
	/**
	 *
获取最优路径
	 * */
	public class RoutePlanListener implements BaiduNaviManager.RoutePlanListener {

		private BNRoutePlanNode mBNRoutePlanNode = null;

		public RoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

			for (Activity ac : activityList) {

				if (ac.getClass().getName().endsWith("BNGuideActivity")) {

					return;
				}
			}
			Intent intent = new Intent(StationInfoActivity.this, BNGuideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(BaiduMapFragment.ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);

		}

		@Override
		public void onRoutePlanFailed() {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "算路失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
			case R.id.yudin:
				DinDanAnimation();break;
			case R.id.Lin2:
				if(DinDan.isShown())
					DinDanAnimation();
				break;
		case R.id.tv_title_button:

			routeplanToNavi();

			break;
			case R.id.submit:
				iv_back.setClickable(false);
				DisplayDinDan.setClickable(false);
				String Class=(String)GasClass.getSelectedItem();
				String GasStationId=tv_name.getText().toString()+"  -  "+tv_addr.getText().toString();
				String Num=GasNum.getText().toString();
				String UserObjectId=new Cache().GetObjectId(StationInfoActivity.this);
				String UserName=new Cache().GetName(StationInfoActivity.this);
				Pattern pattern = Pattern.compile("^[0-9]*[1-9][0-9]*$");
				Matcher match=pattern.matcher(Num);
				if(Num.equals("")||!match.matches()){
					Toast.makeText(StationInfoActivity.this,"加油量为空或格式错误，请重新输入",Toast.LENGTH_SHORT).show();
				}else {
					//SimpleDateFormat formatter =   new    SimpleDateFormat    ("yyyy年MM月dd日    HH:mm:ss     ");
					//Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
					Orders order=new Orders();
					order.setGasNum(Num);
					//order.(curDate);
					order.setGasClass(Class);
					order.setUserName(UserName);
					order.setUserObjectId(UserObjectId);
					order.setGasStationId(GasStationId);
					order.setFlag(false);
					AddOrders(order);
				}break;
		default:
			break;
		}
	}
	/**
	 *
订单页面动画
	 * */
	public void DinDanAnimation() {
		try {
			if (DinDan.getVisibility() == View.GONE) {
				DinDan.startAnimation(DinDanUp);
				DinDan.setVisibility(View.VISIBLE);
			} else {
				DinDan.startAnimation(DinDanDown);
				DinDan.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			DinDanUp = AnimationUtils.loadAnimation(
					StationInfoActivity.this, R.anim.push_up_in);
			DinDanDown = AnimationUtils.loadAnimation(
					StationInfoActivity.this, R.anim.push_up_out);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
