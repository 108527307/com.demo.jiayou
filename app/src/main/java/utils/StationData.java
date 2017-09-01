package utils;




import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import object.Petrol;
import object.Station;

public class StationData {
	/*
	 * 
	 * 方法名：
	 * 作者：
	 * 作用：本方法用于获取聚合的数据
	 * **/
	Handler mHandler=new Handler();
	
	public StationData(Handler mHandler) {
		super();
		this.mHandler = mHandler;
	}
/**
 * 获取聚合数据*/
	public void getStationData(double lat,double lon,int distance,Context context)
	{
		Parameters params=new Parameters();
		params.add("lat",lat);//维度
		params.add("lon",lon);//经度
		params.add("r", distance);
		JuheData.executeWithAPI(context,7, "http://apis.juhe.cn/oil/local", JuheData.GET, params, new DataCallBack()
		{

			@Override
			public void onFailure(int arg0, String arg1, Throwable arg2) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain(mHandler, 0x02, arg1);
				mHandler.sendMessage(msg);
			}
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				Log.i("asdf","fasdg");
				Message msg = Message.obtain(mHandler, 0x03, null);
				msg.sendToTarget();
			}

			@Override
			public void onSuccess(int arg0, String arg1) {




				ArrayList<Station> list;
				try {
					list = parser(arg1);

					Log.i("聚合数据返回数据", list.size()+"");
					Message msg=Message.obtain(mHandler,0x01,list);
							msg.sendToTarget();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
			}
			
		});
	}

	/**
	 * 解析返回数据*/
	private ArrayList<Station> parser(String str) throws JSONException
	{
		str=str.replaceAll("gastprice\":null","gastprice\":{\"null\":\"null\"}");

		JSONObject jsonObject=new JSONObject(str);
    int code=jsonObject.getInt("error_code");
		if(code!=0)
		{
			try {
				ArrayList<Station> list=parser(str);
				//注意这个地方利用Handler往前台传送是否获取数据成功
				if(list!=null&&mHandler!=null)
				{
					//Message msg=Message.obtain(mHandler,0x01,list);
					//msg.sendToTarget();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<Station> list=new ArrayList<Station>();
		JSONObject object=new JSONObject(str);
		JSONObject resultJson=object.getJSONObject("result");
		if(object.getInt("resultcode")==200)
		{
			JSONArray dataJson=resultJson.getJSONArray("data");
			for(int i=0;i<dataJson.length();i++)
			{
				JSONObject json=dataJson.getJSONObject(i);
				Station station=new Station();
				station.setName(json.getString("name"));//加油站名称
				station.setArea(json.getString("areaname"));//城市区域
				station.setAddr(json.getString("address"));//加油站地址
				station.setBrand(json.getString("brandname"));//运营商类型
				station.setDistance(json.getInt("distance"));//
				station.setLat(json.getDouble("lat"));//纬度
				station.setLon(json.getDouble("lon"));//经度
				JSONObject priceJson = json.getJSONObject("price");//省控油价
				ArrayList<Petrol> priceList = new ArrayList<Petrol>();
				Iterator<String> priceI = priceJson.keys();
				while (priceI.hasNext()) {
				Petrol p = new Petrol();
				String key = priceI.next();
				String value = priceJson.getString(key);
				p.setType(key.replace("E", "") + "#");
				p.setPrice(value + "元/升");
				priceList.add(p);
				}
				station.setPriceList(priceList);
				//表示本站的油价
				//fuck
				if(json.getJSONObject("gastprice")!=null) {
					JSONObject gastPriceJson = json.getJSONObject("gastprice");//加油站油价
					ArrayList<Petrol> gastPriceList = new ArrayList<Petrol>();
					Iterator<String> gastPriceI = gastPriceJson.keys();
					while (gastPriceI.hasNext()) {

						Petrol p = new Petrol();

						String key = gastPriceI.next();

						String value = gastPriceJson.getString(key);

						p.setType(key);

						p.setPrice(value + "元/升");

						gastPriceList.add(p);

					}

					station.setGastPriceList(gastPriceList);
				}
				list.add(station);
								

			}
		}
		else
		{//如果放回数据有误，就将错误的返回码返给前台
			Message msg = Message.obtain(mHandler, 0x02, object.getInt("resultcode"));
			
			msg.sendToTarget();
		}
		return list;
	}
	

}
