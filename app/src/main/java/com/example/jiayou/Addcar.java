package com.example.jiayou;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Mods.Staticobject;
import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import object.Cars;
import object.Users;
import utils.ActivityManager;

public class Addcar extends ActivityManager implements OnClickListener{
	private TextView carbrand;
	private EditText VIN;
	private EditText carid;
	private EditText engineid;
	private EditText km;
	private EditText flue;
	private EditText bodylevel;
	private EditText engine;
	private EditText transmission;
	private EditText carheadlight;
	private Button submit;
	private String carobjectid;
	private Button back2carlist;
	private List<String> listk;
	private int num=0;
   private OptionsPickerView pvOptions;
	private ArrayList<String> options1Items = new ArrayList<>();
	private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
	View vMasker;
	private List<String> list;
	private Handler uihandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 00001:
				Toast.makeText(Addcar.this, "提交成功", Toast.LENGTH_LONG).show();
				submit.setClickable(true);
				finish();break;
			case 00002:
				Toast.makeText(Addcar.this, "提交失败"+new Cache().GetObjectId(Addcar.this)+"---"+msg.arg1+"---", Toast.LENGTH_LONG).show();
				submit.setClickable(true);break;
			case 00003:
                Toast.makeText(Addcar.this, msg.obj.toString(), Toast.LENGTH_LONG).show();break;
			case 00004:Toast.makeText(Addcar.this, "提交失败111"+new Cache().GetObjectId(Addcar.this)+"---"+msg.obj.toString()+"---", Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

		setContentView(R.layout.addcars);
		initview();
		initPickerView();
	}
	/**
	 *初始化车牌选择器
	 * */
	private void initPickerView() {
		pvOptions=new OptionsPickerView(this);
		String[] CarClasses=Staticobject.carClasses;
		String[] str=Staticobject.strs;
		for(int i=0;i<CarClasses.length;i++){
			options1Items.add(CarClasses[i]);
			ArrayList<String> options2_Items=new ArrayList<>();
			String[] temp=str[i].split("\n");
			for(int j=0;j<temp.length;j++){
				String[] sp=temp[j].split("]");
				options2_Items.add(sp[0].trim()+"-"+sp[1].trim());
			}
			options2Items.add(options2_Items);
		}
pvOptions.setPicker(options1Items,options2Items,true);
		pvOptions.setTitle("请选择");
		pvOptions.setCyclic(true);
		pvOptions.setSelectOptions(1);
		pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				String t1 = options1Items.get(options1);
				String t2 = options2Items.get(options1).get(option2);
				carbrand.setText(t1 + "-" + t2);
				vMasker.setVisibility(View.GONE);
			}
		});
		}
	/**
	 *初始化view
	 * */
	private void initview() {
		//Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);
		// TODO Auto-generated method stub
		vMasker=findViewById(R.id.vMasker);
		carbrand=(TextView) findViewById(R.id.carbrand);
		//carbrand.setEnabled(false);
		VIN=(EditText)findViewById(R.id.VIN);
		carbrand.setClickable(true);
		carbrand.setOnClickListener(this);
		carid=(EditText) findViewById(R.id.carid1);
		engineid=(EditText) findViewById(R.id.engineid);
		km=(EditText) findViewById(R.id.km);
		flue=(EditText) findViewById(R.id.flue);
		bodylevel=(EditText) findViewById(R.id.bodylevel);
		engine=(EditText) findViewById(R.id.engine);
		transmission=(EditText) findViewById(R.id.transmission);
		carheadlight=(EditText) findViewById(R.id.carheadlight);
		back2carlist=(Button) findViewById(R.id.back2carslist);
		vMasker=findViewById(R.id.vMasker);
		back2carlist.setClickable(true);
		back2carlist.setOnClickListener(this);
		submit=(Button) findViewById(R.id.submitcar);
		submit.setOnClickListener(this);
		list=new ArrayList<>();
		
	}
	/**
	 *更新云端cars数据库
	 * */
	public void UpCars(Context context){
		SharedPreferences user=context.getSharedPreferences("user", Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor=user.edit();
		BmobQuery<Users> query=new BmobQuery<Users>();
		query.getObject(context, new Cache().GetObjectId(context), new GetListener<Users>() {

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Users arg0) {
				// TODO Auto-generated method stub
				listk = arg0.getCar_id();
				Set<String> lists = new HashSet<String>(listk);
				editor.putStringSet("car_id", lists);
				editor.commit();
				uihandler.sendEmptyMessage(00001);
				setResult(1024);
				finish();
			}
		});
	}
	/**
	 *更新云端用户数据库
	 * */
	private void addcar2user(){
        list=(new Cache().Getcachecars(getApplicationContext()));
		list.add(carobjectid);
				Users u=new Users();
				u.setCar_id(list);
				u.update(Addcar.this, new Cache().GetObjectId(Addcar.this), new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						UpCars(Addcar.this);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Message message = new Message();
						message.obj = carobjectid;
						message.arg1 = arg0;
						message.what = 00002;
						uihandler.sendMessage(message);
					}
				});

		
	}

	private void AddCar(final Cars car){


				car.save(Addcar.this, new SaveListener() {
					@Override
					public void onSuccess() {
                       carobjectid=car.getObjectId();
						addcar2user();
					}

					@Override
					public void onFailure(int i, String s) {
                           Log.i("cacc","失败");
					}
				});

	}
	/**
	 *检查用户填写合理性
	 * */
	private int JudgeBiaoDan(String body,String engine,String tra,String carhead){

               if(body.length()!=4||(!body.substring(1,2).equals("门")||!body.substring(3,4).equals("座"))||!body.substring(0,1).matches("[0-9]")||!body.substring(2,3).matches("[0-9]"))
				   return 1;
		       if(!engine.equals("好")&&!engine.equals("异常"))
				   return 2;
		       if(!tra.equals("好")&&!tra.equals("异常"))
				   return 3;
		       if(!carhead.equals("好")&&!carhead.equals("异常"))
			       return 4;
		           return 0;
	}
	/**
	 * 返回错误信息
	* */
	private String ErrorMsg(int num)
	{
		switch (num){
			case 1:
				return "车身等级填写错误";
			case 2:
				return "引擎状况填写错误";
			case 3:
				return "变速器性能填写错误";
			case 4:
				return "车灯状况填写错误";
			default:
			return "0";
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub


		if(v.getId()==R.id.submitcar)
		{
			String[] sp=carbrand.getText().toString().split("-");
			String CarBrand=sp[0];

			String Vin=VIN.getText().toString();
			String CarId=carid.getText().toString();
			String EngineId=engineid.getText().toString();
			String Km=km.getText().toString();
			String Flue=flue.getText().toString();
			String BodyLevel=bodylevel.getText().toString();
			String Engine=engine.getText().toString();
			String Transmission=transmission.getText().toString();
			String CarHeadLight=carheadlight.getText().toString();
			if("".equals(CarBrand)||"".equals(Vin)||"".equals(CarId)||"".equals(EngineId)||"".equals(Km)||"".equals(Flue)||"".equals(BodyLevel)||"".equals(Engine)||"".equals(Transmission)||"".equals(CarHeadLight))
			{
				Toast
				.makeText(this, "填完再提交", Toast.LENGTH_SHORT).show();
			}else {
				String CarClass=sp[1]+"-"+sp[2];
				Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5|WJ]{1}[A-Z0-9]{6}$");
				Matcher match = pattern.matcher(CarId);
				if (!match.matches()) {
					Toast.makeText(this, "额，这个车牌号不对啊", Toast.LENGTH_SHORT).show();
				} else if (EngineId.length() < 6) {
					Toast.makeText(this, "发动机号不对啊", Toast.LENGTH_SHORT).show();
				} else if (JudgeBiaoDan(BodyLevel, Engine, Transmission, CarHeadLight) != 0) {
					Toast.makeText(this, ErrorMsg(JudgeBiaoDan(BodyLevel, Engine, Transmission, CarHeadLight)), Toast.LENGTH_LONG).show();
				} else {
					final Cars c = new Cars();
					c.setCarBrand(CarBrand);
					c.setCarClass(CarClass);
					c.setEngine(Engine);
					c.setEngineId(EngineId);
					c.setKm(Km);
					c.setVIN(Vin);
					c.setCarId(CarId);
					c.setFlue(Flue);
					c.setBodyLevel(BodyLevel);
					c.setTransmission(Transmission);
					c.setCarHeadLight(CarHeadLight);
					c.setUserId(new Cache().GetObjectId(getApplicationContext()));
					submit.setClickable(false);
					JudgeCarInfo(c);
					AddCar(c);
				}

			}
		}else if(v.getId()==R.id.back2carslist){
			setResult(1024);
			finish();
		}else if(v.getId()==R.id.carbrand){
			pvOptions.show();
		}
		
	}
	/**
	 *判断用户车辆情况
	 * */
public void JudgeCarInfo(Cars car){
	String Km= car.getKm();
	String Flue=car.getFlue();
	String Transmission=car.getTransmission();
	String CarHeadlight=car.getCarHeadLight();
	String Engine=car.getEngine();
	if(Integer.parseInt(Km)>15000)
		notificationMethod("车辆超程","车辆里程数已超15000km，请尽快维护车辆");
	if(Integer.parseInt(Flue)<20)
		notificationMethod("油量过低","车辆油量已不足20%，请尽快加油");
	if(Transmission.equals("异常"))
		notificationMethod("车辆变速器异常","车辆变速器发生异常，请尽快维修");
	if(CarHeadlight.equals("异常"))
		notificationMethod("车辆车灯异常","车辆车灯发生异常，请尽快维修");
	if(Engine.equals("异常"))
		notificationMethod("车辆引擎异常","车辆引擎发生异常，请尽快维修");

}
	/**
	 *发通知提示用户
	 * */
	public void notificationMethod(String title,String msg) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0,
						new Intent(this, Main_activity.class), 0);
				Notification notify3 = new Notification.Builder(this)
						.setSmallIcon(R.drawable.zenconspencil)
						.setTicker("加油宝：" + msg)
						.setContentTitle(title)
						.setContentText(msg)
						.setDefaults(Notification.DEFAULT_SOUND)
						.setDefaults(Notification.DEFAULT_LIGHTS)
						.setSound(Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,"6"))
						.setContentIntent(pendingIntent3).setNumber(1).build(); // 需要注意build()是在API
				// level16及之后增加的，API11可以使用getNotificatin()来替代
				notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
         //  Time t=new Time();
		//t.setToNow();
		manager.notify(num, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
        num++;



	}
}
