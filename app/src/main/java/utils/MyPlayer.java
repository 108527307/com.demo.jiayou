package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;


import com.example.jiayou.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import service.MyService;
/**
 * 音乐管理类*/

public class MyPlayer {
	private MediaPlayer media=null;

	public String mplayfilename="null" ;

	public MyPlayer() {
		// TODO Auto-generated constructor stub

		media=new MediaPlayer();
		mplayfilename="null";
	}

	public void Play(String path) {
		mplayfilename = path;
		try {

			media.reset();
			media.setDataSource(path);
			media.prepare();
			media.start();
			media.setOnCompletionListener(
					new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							MyService.startActiondown(MyService.mapp_context);

						}
					});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Play() {
		try {
			media.start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				media.reset();
				media.setDataSource(mplayfilename);
				media.prepare();
				media.start();
			} catch (Exception e2) {
				// TODO: handle exception
			}

			e.printStackTrace();
		}
	}

	public void Pause() {
		try {

			if (media.isPlaying()) {
				media.pause();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}

	}

	public int GetPlayerTime() {
		int i = 10000;
		try {
			i = media.getCurrentPosition();
		} catch (Exception e) {
			// TODO: handle exception
			initPlayer(mplayfilename);
			i = media.getCurrentPosition();
		}
		return i;
	}

	public void initPlayer(String path) {
		try {

			media.reset();
			media.setDataSource(path);
			media.prepare();
			media.setOnCompletionListener(
					new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							MyService.startActiondown(MyService.mapp_context);

						}
					});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean isplaying() {
		boolean b = false;
		try {
			b = media.isPlaying();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();


		}
		return b;
	}

	public void myseekto(int ms)

	{
		try {
			media.seekTo(ms);
		} catch (Exception e) {
			// TODO: handle exception
			initPlayer(mplayfilename);
			media.seekTo(ms);
		}

	}

	/**
	 * 获取默认专辑图片
	 *
	 * @param context
	 * @return
	 */
	public static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.raw.music5), null, opts);
	}

	/**
	 * 从文件当中获取专辑封面位图
	 *
	 * @param context
	 * @param songid
	 * @param
	 * @return
	 */
	public static Bitmap getArtworkFromFile(Context context, long songid
	) {
		Bitmap bm = null;
		if ( songid < 0) {
			throw new IllegalArgumentException(
					"Must specify  a song id");
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			FileDescriptor fd = null;
			if (songid>= 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}

			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 100;
			// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 根据options参数，减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bm;
	}

	/**
	 * 获取专辑封面位图对象
	 *
	 * @param context
	 * @param song_id
	 * @param
	 * @param allowdefalut
	 * @return
	 */
	public static Bitmap getArtwork(Context context, long song_id,
									boolean allowdefalut) {

		if (song_id >0) {
			Bitmap bm = getArtworkFromFile(context, song_id);
			if (bm != null) {
				return bm;
			}
		}
		if (allowdefalut) {
			return getDefaultArtwork(context);
		}
		return null;
	}
	public  void release() {
		// TODO Auto-generated method stub

		media.release();
	}
}
