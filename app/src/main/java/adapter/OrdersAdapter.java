package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jiayou.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/11 0011.
 */
public class OrdersAdapter extends BaseAdapter {
    private List<Map<String,Object>> Data;
    private LayoutInflater layoutInflater;
    public OrdersAdapter(Context context,List<Map<String,Object>> list){
        Data=list;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return Data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new OrderHolder();
            convertView=layoutInflater.inflate(R.layout.item_dindan,null);
            viewHolder.GasStationName=(TextView)convertView.findViewById(R.id.GasStationName);
            viewHolder.GasClass=(TextView)convertView.findViewById(R.id.GasClass);
            viewHolder.GasNum=(TextView)convertView.findViewById(R.id.GasNum);
            viewHolder.Address=(TextView)convertView.findViewById(R.id.Address);
            viewHolder.IsComplete=(TextView)convertView.findViewById(R.id.IsComplete);
            viewHolder.Time=(TextView)convertView.findViewById(R.id.Time);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(OrderHolder)convertView.getTag();
        }
            viewHolder.GasStationName.setText(Data.get(position).get("GasStationName").toString());
            viewHolder.GasClass.setText(Data.get(position).get("GasClass").toString().trim());
            viewHolder.GasNum.setText("   "+Data.get(position).get("GasNum").toString()+" 升");
            viewHolder.Address.setText(Data.get(position).get("Address").toString());
            viewHolder.Time.setText(Data.get(position).get("Time").toString());
        viewHolder.IsComplete.setText(Data.get(position).get("flag").equals(new Boolean("true"))?"订单完成":"未完成");
        return convertView;
    }
    public static class OrderHolder{
        private TextView Time;
        private TextView IsComplete;
        public TextView GasStationName;
        public TextView GasClass;
        public TextView GasNum;
        public TextView Address;
    }
}
