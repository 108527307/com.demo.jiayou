package service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.example.jiayou.R;
import com.example.jiayou.SongList_activity;

import java.util.ArrayList;
import java.util.HashMap;

import fragment.AppointFragment;
import utils.MyPlayer;
import utils.MyReceiver;
import Mods.Myconst;

/**
 * 音乐月播放服务*/
public class MyService extends Service {
	public static Thread mdatathread = null;
	public Thread mseekbarthread = null;
	public MyReceiver mr = null;
	public static LocalBroadcastManager mloca = null;
	public static Context mapp_context = null;
	//public static fragment2 mfrag2 = null;
	public static ArrayList<HashMap<String, String>> zuijinplaylist = new ArrayList<HashMap<String, String>>();
	static public boolean misdatacome = false;
	static public int msindex = 0;
	public static ArrayList<HashMap<String, String>> mslistData = new ArrayList<HashMap<String, String>>();
	public static MyPlayer mplyer =null;
	public static final String ACTION_PLAR_ORPAUSE = "com.example.chang.action.PLAY";
	public static final String ACTION_UP = "com.example.chang.action.UP";
	public static final String ACTION_DOWN = "com.example.chang.action.DOWN";

	private static final String EXTRA_MP3PATH = "com.example.chang.extra.PATH";
//播放选中歌曲
	public static void startActionplayorpause(Context context, int MP3PATHindex) {

		Intent intent = new Intent(context, MyService.class);
		intent.setAction(ACTION_PLAR_ORPAUSE);
		intent.putExtra(EXTRA_MP3PATH, MP3PATHindex);
		context.startService(intent);
	}
//暂停或播放
	public static void startActionplayorpause(Context context) {

			 Intent intent = new Intent(context, MyService.class);
			 intent.setAction(ACTION_PLAR_ORPAUSE);

			 context.startService(intent);

	}
		/**
		 * 提示正在播放的音乐名字
		 * */
	public static void Notifityname(){
		Intent in = new Intent();
		if(mplyer==null)
			in.putExtra(Myconst.MUSIC_TITLE,"null");
		else {
			String SongName=MyService.mplyer.mplayfilename;
			String []t=SongName.split("/");

			in.putExtra(Myconst.MUSIC_TITLE, t[t.length-1]);

		}
			in.setAction(MyReceiver.ACTION_PLAYTIME_CHANGED);
			MyService.mloca.sendBroadcast(in);

	}
//上一首
	public static void startActionup(Context context) {

		Intent intent = new Intent(context, MyService.class);
		intent.setAction(ACTION_UP);
		context.startService(intent);
	}
//下一首
	public static void startActiondown(Context context) {

		Intent intent = new Intent(context, MyService.class);
		intent.setAction(ACTION_DOWN);
		context.startService(intent);
	}
//播放一首，将其加入最近播放列表
	private void handleActionplayorpause(int mp3index) {

		try {
			HashMap<String, String> hs = mslistData
					.get(mp3index);
			new zuijinthread();
			// 得到列 列就是在循环时每一次的put 这样就能获取到这首歌曲的路径了
			// 此路径的表示为/mnt/sdcard/XXX.mp3
			String path = hs.get(Myconst.MUSIC_PATH);

			mplyer.Play(path);
			SongList_activity.mhander.sendEmptyMessage(1);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void handleActionplayorpause() {

		if(mplyer.isplaying()==true)
		{
			mplyer.Pause();
		}
		else
		{
			mplyer.Play();
		}

	}

	private void handleActionup() {

		int i = MyService.msindex - 1;
		if (i < 0) {
			MyService.msindex = MyService.mslistData.size() - 1;
		} else {
			MyService.msindex--;
		}
		handleActionplayorpause(msindex);

	}

	private void handleActiondown() {

		int i = MyService.msindex + 1;
		if (i == MyService.mslistData.size()) {
			MyService.msindex = 0;
		} else {
			MyService.msindex++;
		}
		handleActionplayorpause(msindex);

	}

	static public Thread getinitdatathread(Context con) {

		if (mdatathread == null)
			mdatathread = new mydataThread(con);
		return mdatathread;
	}


	public Thread getinitseekhread(Context con) {

		if (mseekbarthread == null)
			mseekbarthread = new myseekbarThread(con);
		return mseekbarthread;
	}

	public MyService() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return null;

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.zenconspencil).setContentTitle("加油宝启动")
				.setContentText("还没有播放歌曲");
		Intent resultIntent = new Intent(this, SongList_activity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		stackBuilder.addParentStack(SongList_activity.class);

		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		startForeground(SongList_activity.NOTIFY_1, mBuilder.build());

		mloca = LocalBroadcastManager.getInstance(getApplicationContext());
		mr = new MyReceiver();
		IntentFilter i = new IntentFilter();
		i.addAction(MyReceiver.ACTION_DATACOME);
		i.addAction(MyReceiver.HEADSET_PLUG);
		i.addAction(MyReceiver.ACTION_PLAYTIME_CHANGED);
		i.addAction(MyReceiver.ACTION_SEEKBARCHANGE_BYUSER);
		mloca.registerReceiver(mr, i);
		mapp_context = getApplicationContext();
		mplyer=new MyPlayer();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		mloca.unregisterReceiver(mr);

		myseekbarThread.isrun = false;

		mplyer.release();
		// Mp3Thread.mp3Player.getMedia().release();
		// getinitdatathread(getApplicationContext()).stop();
		// getinitmthread().stop();
		// getinitseekhread(getApplicationContext()).stop();
		// SharedPreferences sh=getSharedPreferences("zuijin",
		// Context.MODE_PRIVATE);
		// Set<String> set=new TreeSet<String>();
		// for (int i = 0; i < zuijinplaylist.size(); i++) {
		//
		// HashMap<String, String> hs = MyService.zuijinplaylist
		// .get(i);
		//
		// String id = hs.get(Myconst.MUSIC_ID);
		// set.add(id);
		// }
		// SharedPreferences.Editor e=sh.edit();
		// e.clear();
		// e.putStringSet("z", set);
		// e.commit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		getinitdatathread(getApplicationContext());
		getinitseekhread(getApplicationContext());

		if (null != mslistData && mslistData.size() > 0)
			Log.i("chang", "" + mslistData.get(0));
		String action = intent.getAction();
		if (ACTION_DOWN.equals(action)) {
			handleActiondown();
		} else if (ACTION_UP.equals(action)) {
			handleActionup();
		} else if (ACTION_PLAR_ORPAUSE.equals(action)) {
			if (true == intent.hasExtra(EXTRA_MP3PATH)) {

				handleActionplayorpause(intent.getIntExtra(EXTRA_MP3PATH, 0));
			} else {
				handleActionplayorpause();
			}

		}

		return START_STICKY;
	}

	// thread
	static public class mydataThread extends Thread {

		private Context mcont = null;

		public mydataThread(Context con) {
			mcont = con;
			start();
		}

		@Override
		public void run() {
			try {

				if (mslistData == null)
					mslistData = new ArrayList<HashMap<String, String>>();
				ContentResolver cr = mcont.getContentResolver();
				Log.i("chang", " mydataThread " + cr);
				Cursor cur = cr.query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
						null, null, null);
				try {
					mslistData.clear();
					if (cur.getCount() > 0) {
						for (cur.moveToFirst(); !cur.isAfterLast(); cur
								.moveToNext()) {

							Long ll = cur
									.getLong(cur
											.getColumnIndex(MediaStore.Audio.Media.DURATION));

							if (ll < 60000)
								continue;
							HashMap<String, String> hs = new HashMap<String, String>();
							hs.put(Myconst.MUSIC_ALBUM,
									cur.getString(cur
											.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
							hs.put(Myconst.MUSIC_LENGTH,
									cur.getString(cur
											.getColumnIndex(MediaStore.Audio.Media.DURATION)));
							hs.put(Myconst.MUSIC_PATH,
									cur.getString(cur
											.getColumnIndex(MediaStore.Audio.Media.DATA)));
							hs.put(Myconst.MUSIC_TITLE,
									cur.getString(cur
											.getColumnIndex(MediaStore.Audio.Media.TITLE)));
							Long id = cur
									.getLong(cur
											.getColumnIndex(MediaStore.Audio.Media._ID));
							hs.put(Myconst.MUSIC_ID, id.toString());

							int musicSize = Integer
									.parseInt(cur.getString(cur
											.getColumnIndex(MediaStore.Audio.Media.DURATION)));
							hs.put(Myconst.MUSIC_LENGTHstr,
									AppointFragment.GetFormatTime(musicSize));

							mslistData.add(hs);
						}

					}

				} catch (Exception e) {
					System.out.println("报错 mydataThread " + e.getMessage());

				} finally {
					if (!cur.isClosed()) {
						cur.close();
					}
				}
//				Mp3Thread.mp3Player.initPlayer(mslistData.get(msindex).get(
//						Myconst.MUSIC_PATH));
				misdatacome = true;
				Intent in = new Intent();
				in.setAction(MyReceiver.ACTION_DATACOME);

				mloca.sendBroadcast(in);

			} catch (Exception e) {
				System.out.println("线程报错： mydataThread " + e.getMessage());
				misdatacome = false;
			}
		}

	}

	static public class myseekbarThread extends Thread {

		private Context mcont = null;

		public static Boolean isrun = true;

		public myseekbarThread(Context con) {
			mcont = con;
			start();
		}

		@Override
		public void run() {

			while (true) {
				// Mp3Thread.state = Myconst.SEEKBAR_PROGRESS;
				try {

					// System.out.println(mp3Player.GetPlayerTime());
					if (mplyer.isplaying() == true) {

						Message msg = SongList_activity.mhander.obtainMessage();
						msg.arg1 = mplyer.GetPlayerTime();
						msg.sendToTarget();
					}
					sleep(1000);

				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("线程报错：myseekbarThread" + e.getMessage());
				}

			}

		}
	}

	class zuijinthread extends Thread {

		public zuijinthread() {
			// TODO Auto-generated constructor stub
			start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> hs = MyService.mslistData
					.get(MyService.msindex);

			String id = hs.get(Myconst.MUSIC_ID);
			if (MyService.zuijinplaylist.size() == 0) {
				MyService.zuijinplaylist.add(hs);
				return;
			} else {
				for (int i = 0; i < MyService.zuijinplaylist.size(); i++) {
					HashMap<String, String> hsi = MyService.zuijinplaylist
							.get(i);
					if ((hsi.get(Myconst.MUSIC_ID)).equals(id))
						return;

				}
				MyService.zuijinplaylist.add(hs);
			}
		}
	}
}
