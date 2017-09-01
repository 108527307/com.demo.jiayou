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

/**
 * Created by Administrator on 2016/6/6 0006.
 */
public class SearchAdapter extends BaseAdapter {
    private List<Map<String,Object>> data=null;
    private LayoutInflater inflater;
    public SearchAdapter(Context context,List<Map<String,Object>> PoiInfo){
        this.data=PoiInfo;
        this.inflater=LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return data.size();
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
        ViewHolder viewholder=null;
        if(convertView==null)
        {
            viewholder=new ViewHolder();
            convertView=inflater.inflate(R.layout.searchitem, null);
            viewholder.Title=(TextView) convertView.findViewById(R.id.title);
            viewholder.addre=(TextView) convertView.findViewById(R.id.addre);
            convertView.setTag(viewholder);
        }else{
            viewholder=(ViewHolder) convertView.getTag();
        }
        viewholder.Title.setText(data.get(position).get("Title").toString());
       // Log.i("carbrand", data.get(position).get("CarBrand").toString());
        viewholder.addre.setText(data.get(position).get("addre").toString());
        return convertView;
    }
    public static class ViewHolder{
        public TextView Title;
        public TextView addre;
    }
}
