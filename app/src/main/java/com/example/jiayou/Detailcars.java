package com.example.jiayou;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import object.Cars;
import utils.ActivityManager;

public class Detailcars extends ActivityManager implements View.OnClickListener {
	private TextView carid;
	private TextView carbrand;
	private TextView carclass;
	private TextView engineid;
	private TextView km;
	private TextView Vin;
	private TextView flue;
	private TextView bodylevel;
	private TextView engine;
	private TextView transmission;
	private TextView carheadlight;
	private Button back;
	private Button weizhang;
	private Cars car;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.carsdetaillayout);
	initview();
	setInformation();
}
	/**
	 *设置信息
	 * */
private void setInformation(){
	  car=(Cars)getIntent().getBundleExtra("car").get("cars");
	carid.setText(car.getCarId());
	carbrand.setText(car.getCarBrand());
	carclass.setText(car.getCarClass());
	engine.setText(car.getEngine());
	engineid.setText(car.getEngineId());
	km.setText(car.getKm());
	flue.setText(car.getFlue());
	Vin.setText(car.getVIN());
	bodylevel.setText(car.getBodyLevel());
	carheadlight.setText(car.getCarHeadLight());
	transmission.setText(car.getTransmission());
	weizhang.setOnClickListener(this);

}
	/**
	 *初始化信息
	 * */
private void initview() {
	// TODO Auto-generated method stub
	carid=(TextView) findViewById(R.id.carid);
	carbrand=(TextView) findViewById(R.id.carbrand);
	carclass=(TextView) findViewById(R.id.carclass);
	engineid=(TextView) findViewById(R.id.engineid);
	Vin=(TextView)findViewById(R.id.VIN);
	km=(TextView) findViewById(R.id.km);
	weizhang=(Button)findViewById(R.id.find);
	flue=(TextView) findViewById(R.id.flue);
	bodylevel=(TextView) findViewById(R.id.bodylevel);
	engine=(TextView) findViewById(R.id.engine);
	carheadlight=(TextView) findViewById(R.id.carheadlight);
	transmission=(TextView) findViewById(R.id.trsanmission);
	back=(Button)findViewById(R.id.back);
	back.setOnClickListener(this);

}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.back:
				finish();break;
			case R.id.find:
				Log.i("dawwd","418516");
				Intent intent=new Intent(Detailcars.this,WeiZhangMain.class);
				Bundle b=new Bundle();
				b.putSerializable("cars", car);
				intent.putExtra("car", b);
				startActivity(intent);
				Log.i("dawwd","das");
				break;
		}


	}
}
