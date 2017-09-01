package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jiayou.R;

import java.util.List;
import java.util.Map;

public class CarsAdapter extends BaseAdapter {
	private List<Map<String,Object>> data=null;
	private LayoutInflater inflater;
	public CarsAdapter(Context context,List<Map<String,Object>> data)
	{
		this.inflater=LayoutInflater.from(context);
	    this.data=data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	     CarViewHolder viewholder=null;
	     if(convertView==null)
	     {
	    	 viewholder=new CarViewHolder();
	    	 convertView=inflater.inflate(R.layout.carsitem	, null);
	    	 viewholder.CarBrand=(TextView) convertView.findViewById(R.id.carbrand);
	    	 viewholder.Carid=(TextView) convertView.findViewById(R.id.carid);
	    	convertView.setTag(viewholder);
	     }else{
	    	 viewholder=(CarViewHolder) convertView.getTag();
	     }
	     viewholder.CarBrand.setText(data.get(position).get("CarBrand").toString());
		Log.i("carbrand", data.get(position).get("CarBrand").toString());
		viewholder.Carid.setText(data.get(position).get("CarId").toString());
	     return convertView;
	}
public static class CarViewHolder{
	public TextView CarBrand;
	public TextView Carid;
}
}
