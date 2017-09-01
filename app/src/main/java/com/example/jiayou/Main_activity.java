package com.example.jiayou;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fragment.AppointFragment;
import fragment.BaiduMapFragment;
import fragment.MineFragment;
import service.MyService;
import utils.ActivityManager;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 *
 * @author c_875
 */
public class Main_activity extends ActivityManager implements OnClickListener {
	private AppointFragment appointFragment;
	private BaiduMapFragment mapFragment;
	private MineFragment mineFragment;
	private View appointLayout;
	private View mapLayout;
	private View mineLayout;
	private ImageView appointImage;
    private ImageView mapImage;
	private ImageView mineImage;
	private TextView appointText;
	private TextView mapText;
	private TextView mineText;

	private FragmentManager fragmentManager;
	private static int indexs=0;
	public static String userobjectid;
	FragmentTransaction transaction;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.main_layout);

		initViews();

		fragmentManager = getFragmentManager();
		// 第一次启动时选中第1个tab
		setTabSelection(1);
	}
	   public void onResume() {
	   	// TODO Auto-generated method stub
	   	super.onResume();
		  if(indexs!=0&&indexs!=1&&indexs!=2){
			  if(transaction!=null)
			  hideFragments(transaction);
			  if(appointFragment!=null)
			  indexs=1;
			  setTabSelection(indexs);
		  }
	   }

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {

		appointLayout = findViewById(R.id.appoint_layout);
		mapLayout = findViewById(R.id.map_layout);
		mineLayout = findViewById(R.id.mine_layout);
		appointImage = (ImageView) findViewById(R.id.appoint_image);
		mapImage = (ImageView) findViewById(R.id.map_image);
		mineImage = (ImageView) findViewById(R.id.mine_image);
		appointText = (TextView) findViewById(R.id.appoint_text);
		mapText = (TextView) findViewById(R.id.map_text);
		mineText = (TextView) findViewById(R.id.mine_text);
		appointLayout.setOnClickListener(this);
		mapLayout.setOnClickListener(this);
		mineLayout.setOnClickListener(this);

		userobjectid=getIntent().getStringExtra("ObjectId");
	}  /**
	 *
树立退出事件
	 * */
	private void showTips(){
		AlertDialog alertDialog = new AlertDialog.Builder(Main_activity.this)
				.setTitle("退出程序")
				.setMessage("是否退出程序")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent=new Intent(Main_activity.this,MyService.class);
						android.app.ActivityManager myAM = (android.app.ActivityManager) Main_activity.this
								.getSystemService(Context.ACTIVITY_SERVICE);
						List<android.app.ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(1000);
						if(myList.size()>0)
						stopService(intent);
						//mplyer.release();
						//FirstApplication.getInstance().exit();
						Main_activity.this.finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).create(); //创建对话框
		alertDialog.show(); // 显示对话框
		}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			this.showTips();
			return false;
		}
		return false;
	}
	/**
	 *
处理点击tab事件
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.appoint_layout:

			setTabSelection(0);
			break;
		case R.id.map_layout:

			setTabSelection(1);
			break;
		case R.id.mine_layout:

			setTabSelection(2);
			break;

		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 *
	 * @param index
	 *
	 */

	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		//indexs= String.valueOf(index);
		clearSelection();
		// 开启一个Fragment事务
		 transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			// 当点击了订单tab时，改变控件的图片和文字颜色
			appointImage.setImageResource(R.drawable.shopping_home_tab_order_selected);
			appointText.setTextColor(getResources().getColor(R.color.csy_blue));
			if (appointFragment == null) {
				// 如果appointFragment为空，则创建一个并添加到界面上
				appointFragment = new AppointFragment();//new一个这个fragment对象
				transaction.add(R.id.content, appointFragment);//将这个fragment对象加入fragment事物
			} else {
				// 如果appointFragment不为空，则直接将它显示出来
				transaction.show(appointFragment);

			}
			break;
		case 1:
			// 当点击了地图tab时，改变控件的图片和文字颜色
			mapImage.setImageResource(R.drawable.shopping_home_tab_found_selected);
			mapText.setTextColor(getResources().getColor(R.color.csy_blue));
			if (mapFragment == null) {
				// 如果mapFragment为空，则创建一个并添加到界面上
				mapFragment = new BaiduMapFragment();
				transaction.add(R.id.content, mapFragment);
			} else {
				// 如果mapFragment不为空，则直接将它显示出来
				transaction.show(mapFragment);

			}
			break;
		case 2:
			// 当点击了我的tab时，改变控件的图片和文字颜色
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

				findViewById(R.id.toolbar).setElevation(0);
			}
			mineImage.setImageResource(R.drawable.shopping_home_tab_personal_selected);
			mineText.setTextColor(getResources().getColor(R.color.csy_blue));
			if (mineFragment == null) {
				// 如果mineFragment为空，则创建一个并添加到界面上
				mineFragment = new MineFragment();
				transaction.add(R.id.content, mineFragment);
			} else {
				// 如果mineFragment不为空，则直接将它显示出来
				transaction.show(mineFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		appointImage.setImageResource(R.drawable.shopping_home_tab_order);
		appointText.setTextColor(Color.parseColor("#82858b"));
		mapImage.setImageResource(R.drawable.shopping_home_tab_found);
		mapText.setTextColor(Color.parseColor("#82858b"));
		mineImage.setImageResource(R.drawable.shopping_home_tab_personal);
		mineText.setTextColor(Color.parseColor("#82858b"));

	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 *
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (appointFragment != null) {
			transaction.hide(appointFragment);
		}
		if (mapFragment != null) {
			transaction.hide(mapFragment);
		}
		if (mineFragment != null) {
			transaction.hide(mineFragment);
		}

	}

}
