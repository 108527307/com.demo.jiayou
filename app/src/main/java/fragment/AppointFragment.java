package fragment;

import com.example.jiayou.R;
import com.example.jiayou.SongList_activity;
import com.google.zxing.WriterException;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.OrdersAdapter;
import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import myview.CircleProgress;
import myview.FloatingActionButton;
import myview.SlideCutListView;
import myview.SlideCutListView.RemoveListener;
import object.Orders;
import service.MyService;
import Mods.Myconst;
import utils.QRcode;

public class AppointFragment extends Fragment implements AdapterView.OnItemClickListener, RemoveListener,View.OnClickListener{
private SlideCutListView DinDanList;
	private List<Orders> Orderlist;
	private List<Map<String,Object>> lists=new ArrayList<>();
	static public RelativeLayout parent;
	private Dialog dialog;
	private FloatingActionButton play;
	private FloatingActionButton down;
	private FloatingActionButton songlist;
	private CircleProgress circleProgress;
	private OrdersAdapter orderAdapter;
	private Dialog dialog1;
	private ImageView Zxing;
	public static ArrayList<HashMap<String, String>> listData = new ArrayList<>();
	private ViewGroup Music;
	private Handler UiHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.arg1){
				case 0001:
					CancelDialog();
					orderAdapter.notifyDataSetChanged();
					break;
				case 0002:
					orderAdapter.notifyDataSetChanged();
					break;
				default:break;
			}
		}
	};
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		LayoutInflater inflaters = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent = (RelativeLayout) inflaters.inflate(R.layout.appoint_layout, null);
		init(parent);
		return parent;
	}

	@Override
	public void onResume() {
		super.onResume();
		Refresh();
	}

	private void init(View v) {
        play=(FloatingActionButton)v.findViewById(R.id.play);
		songlist=(FloatingActionButton)v.findViewById(R.id.playlist);
		down=(FloatingActionButton)v.findViewById(R.id.down);
		play.setOnClickListener(this);
		songlist.setOnClickListener(this);
		down.setOnClickListener(this);
		Music= (ViewGroup) v.findViewById(R.id.multiple_actions);
		Music.bringToFront();
		DinDanList=(SlideCutListView)v.findViewById(R.id.DinDanList);
		orderAdapter=new OrdersAdapter(getActivity(),lists);
		DinDanList.setAdapter(orderAdapter);
		DinDanList.setOnItemClickListener(this);
		DinDanList.setRemoveListener(this);
		View diaview=View.inflate(getActivity(), R.layout.bitmap_dialog, null);
		 dialog1=new Dialog(getActivity(),R.style.dialog);
		dialog1.setContentView(diaview);
		View diaView=View.inflate(getActivity(), R.layout.mydialog, null);
		circleProgress=(CircleProgress)diaView.findViewById(R.id.progress);
		dialog=new Dialog(getActivity(),R.style.dialog);
		dialog.setContentView(diaView);
		Zxing=(ImageView)diaview.findViewById(R.id.ZxingImage);
       Binder();
		MyService.startActionplayorpause(getActivity());

		play.setIcon(R.drawable.play_btn);
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
	}
	private void Refresh(){
		ShowDialog();
		getData();
	}
	/**
	 *初始化音乐服务
	 * */
	private void Binder() {


		if (MyService.mslistData.size()!=0) {
			listData = MyService.mslistData;

		} else {
			ContentResolver cr = getActivity().getContentResolver();
			Cursor cur = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null, null, null, null);
			int num=cur.getCount();
			try {
				listData.clear();
				if (num > 0) {
					for (cur.moveToFirst(); !cur.isAfterLast(); cur
							.moveToNext()) {

						Long ll = cur
								.getLong(cur
										.getColumnIndex(MediaStore.Audio.Media.DURATION));

						if (ll < 60000)
							continue;
						HashMap<String, String> hs = new HashMap<String, String>();
						hs.put(Myconst.MUSIC_ALBUM, cur.getString(cur
								.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
						hs.put(Myconst.MUSIC_LENGTH,
								cur.getString(cur
										.getColumnIndex(MediaStore.Audio.Media.DURATION)));
						hs.put(Myconst.MUSIC_PATH, cur.getString(cur
								.getColumnIndex(MediaStore.Audio.Media.DATA)));
						hs.put(Myconst.MUSIC_TITLE, cur.getString(cur
								.getColumnIndex(MediaStore.Audio.Media.TITLE)));

						Long id = cur.getLong(cur
								.getColumnIndex(MediaStore.Audio.Media._ID));
						hs.put(Myconst.MUSIC_ID, id.toString());

						int musicSize = Integer
								.parseInt(cur.getString(cur
										.getColumnIndex(MediaStore.Audio.Media.DURATION)));
						hs.put(Myconst.MUSIC_LENGTHstr,
								GetFormatTime(musicSize));
						listData.add(hs);
					}
				}

			} catch (Exception e) {
				System.out.println("binde报错" + e.getMessage());
			} finally {
				if (!cur.isClosed()) {
					cur.close();
				}
			}

		}


	}
	/**
	 *获取当前时间
	 * */
	public static String GetFormatTime(int time) {
		SimpleDateFormat sim = new SimpleDateFormat("mm:ss");
		return sim.format(time);
	}
	/**
	 *获取订单
	 * */
	private void getData(){
		String ObjectId=new Cache().GetObjectId(getActivity());
		BmobQuery<Orders> query=new BmobQuery<>();
		query.addWhereEqualTo("UserObjectId",ObjectId);
		query.order("-createdAt");
		query.setLimit(200);
		query.findObjects(getActivity(), new FindListener<Orders>() {
			@Override
			public void onSuccess(List<Orders> list) {
				Orderlist=list;
				lists.clear();
				if(list.size()==0)
					Toast.makeText(getActivity(),"无订单记录",Toast.LENGTH_SHORT).show();
				else
				{

					for(int i=0;i<list.size();i++){
						String str2[]=list.get(i).getCreatedAt().split(" ");

						String str[]=list.get(i).getGasStationId().split("-");
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("GasStationName", str[0]);
						map.put("GasClass",list.get(i).getGasClass());
						map.put("GasNum",list.get(i).getGasNum());
						map.put("Address",str[1]);
						map.put("flag",list.get(i).isFlag());
						map.put("objectid",list.get(i).getObjectId());
						map.put("Time",str2[0].substring(5)+" "+str2[1].substring(0,5));
						lists.add(map);

					}
				}
				if(dialog.isShowing()){
					Message msg=new Message();
					msg.arg1=0001;
					UiHandler.sendMessage(msg);
				}else{
					Message m=new Message();
					m.arg1=0002;
					UiHandler.sendMessage(m);
				}
				//CancelDialog();
				//orderAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int i, String s) {
				Toast.makeText(getActivity(),"查询失败"+i+s,Toast.LENGTH_LONG).show();
				if(dialog.isShowing()){
				//CancelDialog();
					Message msg1=new Message();
				msg1.arg1=0001;
				UiHandler.sendMessage(msg1);
				}else{
					Message m=new Message();
					m.arg1=0002;
					UiHandler.sendMessage(m);
				}
			}
		});
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Orders order=Orderlist.get(position);
		if(order.isFlag()){
			Toast.makeText(getActivity(),"此订单已完成",Toast.LENGTH_LONG).show();

		} else {
			String url = order.getObjectId();
			try {
				Zxing.setImageBitmap(new QRcode().createQRImage(url));
			} catch (WriterException e) {
				e.printStackTrace();
			}
			dialog1.show();
		}
	}

	@Override
	public void removeItem(SlideCutListView.RemoveDirection direction, final int position) {

		if(direction== SlideCutListView.RemoveDirection.LEFT){
			Orders order=new Orders();
            order.delete(getActivity(),lists.get(position).get("objectid").toString(), new DeleteListener() {
				@Override
				public void onSuccess() {
					Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_LONG).show();
					//lists.clear();
					getData();
					//orderAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFailure(int i, String s) {
					Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
         switch(v.getId()){
			 case R.id.down:
				 MyService.startActiondown(getActivity());
				 play.setIcon(R.drawable.pause_btn);
				MyService.Notifityname();
				 break;
			 case R.id.play:
				 try {

					 if (MyService.mplyer.isplaying()) {

						 play.setIcon(R.drawable.play_btn);
					 } else {
						 play.setIcon(R.drawable.pause_btn);

					 }
				 } catch (Exception e) {
					 // TODO: handle exception
				 }finally {
					 MyService.startActionplayorpause(getActivity());
					 MyService.Notifityname();
				 }
				 break;
			 case R.id.playlist:
				 Intent intent=new Intent(getActivity(),SongList_activity.class);
				 Bundle ba=new Bundle();
				 ba.putSerializable("songs",(Serializable)listData);
				 intent.putExtra("b", ba);
				 startActivity(intent);

		 }
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
}
