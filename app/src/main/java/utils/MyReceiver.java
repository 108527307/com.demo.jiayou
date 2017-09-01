package utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.jiayou.Main_activity;
import com.example.jiayou.R;
import com.example.jiayou.SongList_activity;

import Mods.Myconst;
import service.MyService;

/**
 * 广播接收者*/
public class MyReceiver extends BroadcastReceiver {
	public static String ACTION_DATACOME = "HEHEHEH";
	public static String ACTION_SEEKBARCHANGE_BYUSER = "DDDDHEHEHEH";
	public static String ACTION_PLAYTIME_CHANGED = "DDDDHEHEHJJJEH";
	public static String EXETER_PLAYTIME_CHANGED_CONTEXT = "JJEH";
	public static String tag = "chang";
	public static String HEADSET_PLUG=Intent.ACTION_HEADSET_PLUG;
	public MyReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		Log.i("chang", "onReceive(Context context, Intent intent)" + intent);
		String action = intent.getAction();
		if(HEADSET_PLUG.equals(action)){
			if(intent.hasExtra("state")) {
				if (intent.getIntExtra("state", 0) == 0) {
					MyService.startActionplayorpause(MyService.mapp_context);
				     Toast.makeText(MyService.mapp_context,"vsv",Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(MyService.mapp_context,"vssgfvdvf",Toast.LENGTH_LONG).show();
			}
		}else if (ACTION_DATACOME.equals(action)) {
			//com.chang.servermp3.MyService.getinitmthread();
			//Toast.makeText(context, "  数据准备完成！changhong  ", Toast.LENGTH_LONG)
					//.show();
		} else if (ACTION_PLAYTIME_CHANGED.equals(action)) {
			String title = intent.getStringExtra(Myconst.MUSIC_TITLE);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					MyService.mapp_context)
					.setSmallIcon(R.drawable.zenconspencil)
					.setContentTitle("正在播放").setContentText("" + title);
			Intent resultIntent = new Intent(MyService.mapp_context,
					Main_activity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder
					.create(MyService.mapp_context);

			stackBuilder.addParentStack(SongList_activity.class);

			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager nm = (NotificationManager) MyService.mapp_context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(SongList_activity.NOTIFY_1, mBuilder.build());

		} else if (ACTION_SEEKBARCHANGE_BYUSER.equals(action)) {

		}

	}
}
