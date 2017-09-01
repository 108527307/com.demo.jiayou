package com.example.jiayou;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;

import object.Cars;
import utils.ActivityManager;


public class WeiZhangMain extends ActivityManager {


	private String defaultChepai = "宁"; // 粤=广东

	private TextView query_city;
	private Button btn_query;

    private Cars cara;

Button back;
	 String chepaiNumberStr1 ;
	 String chejiaNumberStr1;
	 String engineNumberStr1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.csy_activity_main);
		cara=(Cars)getIntent().getBundleExtra("car").get("cars");
		chepaiNumberStr1 = cara.getCarId()
				.trim();
		 chejiaNumberStr1 = cara.getVIN().trim();
		 engineNumberStr1 = cara.getEngineId().trim();
		// 标题
		Log.i("初始化服务代码","");
		Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
		weizhangIntent.putExtra("appId",1804);
		weizhangIntent.putExtra("appKey", "583a9177be72255b7cd67feee0ad71cc");
		startService(weizhangIntent);
		Log.i("开启服务","ad");
		// ********************************************************

		// 选择省份缩写
		query_city = (TextView) findViewById(R.id.cx_city);
		back=(Button)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_query = (Button) findViewById(R.id.btn_query);


		query_city.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(WeiZhangMain.this, ProvinceList.class);
				startActivityForResult(intent, 1);
			}
		});

		btn_query.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 获取违章信息
				CarInfo car = new CarInfo();
				String quertCityStr = null;
				String quertCityIdStr = null;


				final String chepaiNumberStr = cara.getCarId()
						.trim();
				if (query_city.getText() != null
						&& !query_city.getText().equals("")) {
					quertCityStr = query_city.getText().toString().trim();

				}

				if (query_city.getTag() != null
						&& !query_city.getTag().equals("")) {
					quertCityIdStr = query_city.getTag().toString().trim();
					car.setCity_id(Integer.parseInt(quertCityIdStr));
				}

				Intent intent = new Intent();

				car.setChejia_no(chejiaNumberStr1);
				car.setChepai_no( chepaiNumberStr);

				car.setEngine_no(engineNumberStr1);

				Bundle bundle = new Bundle();
				bundle.putSerializable("carInfo", car);
				intent.putExtras(bundle);

				boolean result = checkQueryItem(car);

				if (result) {
					intent.setClass(WeiZhangMain.this, WeizhangResult.class);
					startActivity(intent);

				}
			}
		});
	}
	/**
	 *
处理返回信息
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;

		switch (requestCode) {
			case 1:
				Bundle bundle1 = data.getExtras();
				String cityId = bundle1.getString("city_id");
				setQueryItem(Integer.parseInt(cityId));

				break;
		}
	}

	// 根据城市的配置设置查询项目
	private void setQueryItem(int cityId) {

		InputConfigJson cityConfig = WeizhangClient.getInputConfig(cityId);
         Log.i("cityconfig",cityConfig.toString());
		Log.i("casc","fad");
		// 没有初始化完成的时候;
		if (cityConfig != null) {
			CityInfoJson city = WeizhangClient.getCity(cityId);

			query_city.setText(city.getCity_name());
			query_city.setTag(cityId);

			int len_chejia = cityConfig.getClassno();
			int len_engine = cityConfig.getEngineno();


			// 车架号
			if (len_chejia == 0) {
				//row_chejia.setVisibility(View.GONE);
			} else {
				//row_chejia.setVisibility(View.VISIBLE);
				//setMaxlength(chejia_number, len_chejia);
				if (len_chejia == -1) {
					//chejia_number.setHint("请输入完整车架号");
				} else if (len_chejia > 0) {
					Log.i("车架号长度",chejiaNumberStr1.length()+""+len_chejia);
					int num=chejiaNumberStr1.length()-len_chejia;
					String temp=chejiaNumberStr1.substring(num);
					chejiaNumberStr1=temp;
					Log.i("车架号",temp);
					//chejia_number.setHint("请输入车架号后" + len_chejia + "位");
				}
			}

			// 发动机号
			if (len_engine == 0) {
				//row_engine.setVisibility(View.GONE);
			} else {
				//row_engine.setVisibility(View.VISIBLE);
				//setMaxlength(engine_number, len_engine);
				if (len_engine == -1) {
					//engine_number.setHint("请输入完整车发动机号");
				} else if (len_engine > 0) {
					Log.i("车架号长度",engineNumberStr1.length()+""+len_engine);
					//int num=chepaiNumberStr1.length()-len_engine
					String temp=engineNumberStr1.substring(engineNumberStr1.length()-len_engine);
					engineNumberStr1=temp;
					Log.i("发动机号",temp);
					//engine_number.setHint("请输入发动机后" + len_engine + "位");
				}
			}
		}
	}

	// 提交表单检测
	private boolean checkQueryItem(CarInfo car) {
		if (car.getCity_id() == 0) {
			Toast.makeText(WeiZhangMain.this, "请选择查询地", Toast.LENGTH_LONG).show();
			return false;
		}

		if (car.getChepai_no().length() != 7) {
			Toast.makeText(WeiZhangMain.this, "您输入的车牌号有误", Toast.LENGTH_LONG).show();
			return false;
		}

		if (car.getCity_id() > 0) {
			InputConfigJson inputConfig = WeizhangClient.getInputConfig(car
					.getCity_id());
			int engineno = inputConfig.getEngineno();
			int registno = inputConfig.getRegistno();
			int classno = inputConfig.getClassno();

			// 车架号
			if (classno > 0) {
				if (car.getChejia_no().equals("")) {
					Toast.makeText(WeiZhangMain.this, "输入车架号不为空", Toast.LENGTH_LONG).show();
					return false;
				}

				if (car.getChejia_no().length() != classno) {
					Toast.makeText(WeiZhangMain.this, "输入车架号后" + classno + "位",
							Toast.LENGTH_LONG).show();
					return false;
				}
			} else if (classno < 0) {
				if (car.getChejia_no().length() == 0) {
					Toast.makeText(WeiZhangMain.this, "输入全部车架号", Toast.LENGTH_LONG).show();
					return false;
				}
			}

			//发动机
			if (engineno > 0) {
				if (car.getEngine_no().equals("")) {
					Toast.makeText(WeiZhangMain.this, "输入发动机号不为空", Toast.LENGTH_LONG).show();
					return false;
				}

				if (car.getEngine_no().length() != engineno) {
					Toast.makeText(WeiZhangMain.this,
							"输入发动机号后" + engineno + "位", Toast.LENGTH_LONG).show();
					return false;
				}
			} else if (engineno < 0) {
				if (car.getEngine_no().length() == 0) {
					Toast.makeText(WeiZhangMain.this, "输入全部发动机号", Toast.LENGTH_LONG).show();
					return false;
				}
			}

			// 注册证书编号
			if (registno > 0) {
				if (car.getRegister_no().equals("")) {
					Toast.makeText(WeiZhangMain.this, "输入证书编号不为空", Toast.LENGTH_LONG).show();
					return false;
				}

				if (car.getRegister_no().length() != registno) {
					Toast.makeText(WeiZhangMain.this,
							"输入证书编号后" + registno + "位", Toast.LENGTH_LONG).show();
					return false;
				}
			} else if (registno < 0) {
				if (car.getRegister_no().length() == 0) {
					Toast.makeText(WeiZhangMain.this, "输入全部证书编号", Toast.LENGTH_LONG).show();
					return false;
				}
			}
			return true;
		}
		return false;

	}


}
