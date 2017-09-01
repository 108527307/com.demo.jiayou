package com.example.jiayou;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.CarsAdapter;
import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import myview.CircleProgress;
import myview.FloatingActionButton;
import myview.SlideCutListView;
import myview.SlideCutListView.RemoveListener;
import object.Cars;
import object.Users;
import utils.ActivityManager;

public class Carslist extends ActivityManager implements OnClickListener,OnItemClickListener,RemoveListener {
	private FloatingActionButton add;
	private Button back2mine;
	private SlideCutListView list;
	private Dialog dialog;
	private ImageView zxing;

	private CircleProgress circleProgress;
	List<Cars>  carlist=null;
	RelativeLayout parent=null;
	final private List<Map<String,Object>> listss=new ArrayList<Map<String,Object>>();

	CarsAdapter carsadapter=null;
	private Handler UiHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
				case 0001:carsadapter.notifyDataSetChanged();
					CancelDialog();
					break;
				case 0002:
					//CancelDialog();
					carsadapter.notifyDataSetChanged();
					break;
				default:break;
			}
		}
	};
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);

	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	parent = (RelativeLayout) inflater.inflate(R.layout.cars_list, null);
	setContentView(parent);

	initview();
	carsadapter=new CarsAdapter(Carslist.this,listss);
	list.setAdapter(carsadapter);
	Refreshen();



}


	private void ShowDialog()
	{
		circleProgress.setRadius(0.2f);
		circleProgress.startAnim();
		dialog.show();
	}
	private void CancelDialog()
	{
		circleProgress.stopAnim();
		dialog.cancel();
		//orderAdapter.notifyDataSetChanged();
	}
	/**
	 *刷新界面
	 * */
	private void Refreshen(){
		ShowDialog();
		GetData();
	}
	/**
	 *初始化view
	 * */
private void initview() {
	// TODO Auto-generated method stub
	zxing=(ImageView)findViewById(R.id.button);
	zxing.setOnClickListener(this);
	View diaView=View.inflate(Carslist.this, R.layout.mydialog, null);
	diaView=View.inflate(Carslist.this, R.layout.mydialog, null);
	add=(FloatingActionButton) findViewById(R.id.add);
	back2mine=(Button) findViewById(R.id.backtomine);
	list=(SlideCutListView) findViewById(R.id.listview);
	list.setRemoveListener(this);
	dialog=new Dialog(Carslist.this,R.style.dialog);
	dialog.setContentView(diaView);
	circleProgress=(CircleProgress)diaView.findViewById(R.id.progress);
	add.setOnClickListener(this);
	list.setOnItemClickListener(this);
	back2mine.setOnClickListener(this);
	carlist=new ArrayList<Cars>();
}
	/**
	 *数据获取
	 * */
 public  void GetData()
{
	 List<String> lists=new Cache().Getcachecars(getApplicationContext());
	Log.i("asdas",lists.toString());
	BmobQuery<Cars> query=new BmobQuery<Cars>("Cars");
	query.addWhereContainedIn("objectId", lists);
	query.findObjects(getApplicationContext(), new FindListener<Cars>() {

		@Override
		public void onSuccess(List<Cars> arg0) {
			// TODO Auto-generated method stub
			carlist=arg0;
			listss.clear();
			Log.i("qiche1",arg0.toString());

			if(carlist.size()==0){
				Log.i("sa","无车辆");
			}else {

				for (int i = 0; i < carlist.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("CarBrand", carlist.get(i).getCarBrand());
					map.put("CarId", carlist.get(i).getCarId());
					map.put("Car", carlist.get(i));
					listss.add(map);
				}

			}
                if(dialog.isShowing()){
					Message msg=new Message();
					msg.arg1=0001;
					UiHandler.sendMessage(msg);

				}else{
					Message msg=new Message();
					msg.arg1=0002;
					UiHandler.sendMessage(msg);
				}
			//CancelDialog();
		}

		@Override
		public void onError(int arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.i("qiche",arg1.toString());
		}
	});

	}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()){
        case R.id.add:
		Intent intent=new Intent(Carslist.this,Addcar.class);
		startActivityForResult(intent,1024);
            break;
        case R.id.backtomine:
            finish();
            break;
		case R.id.button:
			//zxing.setElevation(0);
			Intent intent1=new Intent(Carslist.this,CaptureActivity.class);
			startActivityForResult(intent1,1024);
			break;
        default:break;
	}
}
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	Intent intent=new Intent(Carslist.this,Detailcars.class);
	Bundle b=new Bundle();
	b.putSerializable("cars", carlist.get(position));
	intent.putExtra("car", b);
	startActivity(intent);
}
	/**
	 *处理不同activity返回的结果
	 * */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
	if(requestCode==resultCode){
         Refreshen();
	}
}
	/**
	 *处理滑动item事件
	 * */
	@Override
	public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
		if(direction== SlideCutListView.RemoveDirection.LEFT){
			final int p=position;
			String CarObjectid=carlist.get(position).getObjectId();
			final String  Objectid=carlist.get(position).getUserId();
			Cars c=new Cars();
			c.delete(Carslist.this, CarObjectid, new DeleteListener() {
				@Override
				public void onSuccess() {
                 List<String> Carlists=new Cache().Getcachecars(Carslist.this);
					Carlists.remove(p);
					new Cache().SetcacheCars(Carslist.this,Carlists);
					Users u=new Users();
					u.setCar_id(Carlists);
					u.update(Carslist.this,Objectid, new UpdateListener() {
						@Override
						public void onSuccess() {
							Toast.makeText(Carslist.this, "车辆已删除", Toast.LENGTH_SHORT).show();
							//listss.clear();
							GetData();
							//carsadapter.notifyDataSetChanged();
						}

						@Override
						public void onFailure(int i, String s) {
							Toast.makeText(Carslist.this, s, Toast.LENGTH_SHORT).show();
						}
					});

				}

				@Override
				public void onFailure(int i, String s) {

				}
			});
		}
	}
}
