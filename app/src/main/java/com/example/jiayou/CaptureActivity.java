package com.example.jiayou;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cache.Cache;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import object.Cars;
import object.Users;
import utils.ActivityManager;

/**
 *
 * 描述: 扫描界面
 */
public class CaptureActivity extends ActivityManager implements Callback {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private String carobjectid;
	private ImageView back2carlist;
	private List<String> listk;
    private Button bacbt;
	private int num=0;
	private List<String> list;
	private Switch light;

	private Handler uihandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case 00001:
					Toast.makeText(CaptureActivity.this, "提交成功", Toast.LENGTH_LONG).show();

					finish();break;
				case 00002:
					Toast.makeText(CaptureActivity.this, "提交失败"+new Cache().GetObjectId(CaptureActivity.this)+"---"+msg.arg1+"---", Toast.LENGTH_LONG).show();
					break;
				case 00003:
					Toast.makeText(CaptureActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();break;
				case 00004:Toast.makeText(CaptureActivity.this, "提交失败111"+new Cache().GetObjectId(CaptureActivity.this)+"---"+msg.obj.toString()+"---", Toast.LENGTH_LONG).show();
			}
		}
	};
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr_scan);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
       light= (Switch) findViewById(R.id.light);
		light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				         light();
			}
		});
		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        bacbt=(Button)findViewById(R.id.btnBack);
		bacbt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(1200);
		mQrLineView.startAnimation(animation);
	}

	boolean flag = true;
/**
 * 开关闪光灯
* */
	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
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
	 *更新云端数据库
	 * */
	private void addcar2user(){
		list=(new Cache().Getcachecars(getApplicationContext()));
		list.add(carobjectid);
		Users u=new Users();
		u.setCar_id(list);
		u.update(CaptureActivity.this, new Cache().GetObjectId(CaptureActivity.this), new UpdateListener() {

			@Override
			public void onSuccess() {
				UpCars(CaptureActivity.this);
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
	/**
	 *更新云端汽车表
	 * */
	private void AddCar(final Cars car){


		Log.i("ccac", "save");
		car.save(CaptureActivity.this, new SaveListener() {
			@Override
			public void onSuccess() {
				carobjectid = car.getObjectId();
				Log.i("cacc", carobjectid);
				addcar2user();
			}

			@Override
			public void onFailure(int i, String s) {
				Log.i("cacc", "失败");
			}
		});

	}
	/**
	 *处理扫描结果
	 * */
	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		if(getData(result)==null){
			finish();
			return;

				}
		Cars c=getData(result);
		if(JudgeBiaoDan(c.getBodyLevel(),c.getEngine(),c.getTransmission(),c.getCarHeadLight())!=0){
			Toast.makeText(this,ErrorMsg(JudgeBiaoDan(c.getBodyLevel(),c.getEngine(),c.getTransmission(),c.getCarHeadLight())),Toast.LENGTH_LONG).show();
			finish();
		}
            JudgeCarInfo(c);
		AddCar(c);
		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
		//handler.sendEmptyMessage(R.id.restart_preview);
	}
	/**
	 *解析数据
	 * */
private Cars getData(String result){
	Cars c=new Cars();
	String []re=result.split("\\:");
	if(re.length==22) {


		c.setCarBrand(re[1]);
		c.setCarClass(re[3]);
		c.setCarId(re[5]);
		c.setEngineId(re[7]);
		c.setKm(re[9]);
		c.setFlue(re[11]);
		c.setBodyLevel(re[13]);
		c.setEngine(re[15]);
		c.setTransmission(re[17]);
		c.setCarHeadLight(re[19]);
		c.setVIN(re[21]);
		c.setUserId(new Cache().GetObjectId(getApplicationContext()));
		return c;
	}else{
		Toast.makeText(this, "请扫描正确的二维码", Toast.LENGTH_LONG).show();
		return null;
	}
}
	/**
	 *初始化摄像头
	 * */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width
					/ mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height
					/ mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}
	/**
	 *初始化提示音
	 * */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}
	/**
	 *检查用户填写合理性
	 * */
	private int JudgeBiaoDan(String body,String engine,String tra,String carhead){
		if(body.length()!=4||!body.substring(1,2).equals("门")||!body.substring(3,4).equals("座")||!body.substring(0,1).matches("[0-9]")||!body.substring(2,3).matches("[0-9]"))
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
	/**
	 *判断录入汽车信息
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
	 *提示用户
	 * */
	public void notificationMethod(String title,String msg) {
		// 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
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
				.setSound(Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "6"))
				.setContentIntent(pendingIntent3).setNumber(1).build(); // 需要注意build()是在API

		notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。

		manager.notify(num, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
		num++;



	}
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
}