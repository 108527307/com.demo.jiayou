package com.example.jiayou;


import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import fragment.AppointFragment;
import myview.FloatingActionButton;
import service.MyService;
import utils.ActivityManager;
import utils.MyPlayer;
import utils.MyReceiver;
import Mods.Myconst;


public class SongList_activity extends ActivityManager implements AdapterView.OnItemClickListener,
        View.OnClickListener {
    public static Handler mhander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {

                switch (msg.what) {
                    case 0:
                        int shijian = msg.arg1;
                        Log.i("chang", "msg.arg1:" + shijian);
                        break;
                    case 1:
                        try {
                            ImageButton b1 = (ImageButton) goup.findViewById(R.id.play);
                            FloatingActionButton b = (FloatingActionButton) AppointFragment.parent
                                    .findViewById(R.id.play);

                            b.setIcon(R.drawable.pause_btn);
                            b1.setBackgroundResource(R.drawable.pause_btn);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                        break;
                    default:

                        break;

                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            // TODO Auto-generated method stub

        }

    };


    public static final int NOTIFY_1 = 0x1001;//通知
    Button back;
    ImageButton up;
    ImageButton dowm;
    ImageButton play;
    ListView listView;
    public static View goup = null;
    public static ArrayList<HashMap<String, String>> listData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setContentView(R.layout.songlist_layout);

        listView = (ListView) findViewById(R.id.ls1);
        listView.setOnItemClickListener(this);
        init();

        listData = new ArrayList<HashMap<String, String>>();
        Binder();

        ImageButton b = (ImageButton) findViewById(R.id.play);
        try {
            if (MyService.mplyer.isplaying()) {

                b.setBackgroundResource(R.drawable.pause_btn);

            } else {

                b.setBackgroundResource(R.drawable.play_btn);

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void init() {

        up = (ImageButton) findViewById(R.id.up);
        up.setOnClickListener(this);
        dowm = (ImageButton) findViewById(R.id.down);
        dowm.setOnClickListener(this);
        play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(this);
        back = (Button) findViewById(R.id.backtomain);
        back.setOnClickListener(this);

    }

    //获取内存卡歌曲，数据加载至listdata，并初始化及设置适配器
    private void Binder() {


//
        listData = (ArrayList<HashMap<String, String>>) getIntent().getBundleExtra("b").getSerializable("songs");

        // SimpleAdapter sim = new SimpleAdapter(this.getActivity(), listData,
        // android.R.layout.simple_list_item_1,
        // new String[] { Myconst.MUSIC_TITLE },
        // new int[] { android.R.id.text1 });
        SimpleAdapter sim = new SimpleAdapter(SongList_activity.this, listData,
                R.layout.music_list_item_layout, new String[]{
                Myconst.MUSIC_TITLE, Myconst.MUSIC_LENGTHstr,
                Myconst.MUSIC_ALBUM}, new int[]{R.id.music_title,
                R.id.music_duration, R.id.music_Artist});

        listView.setAdapter(sim);

//另开线程设置专辑图片
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = 0; i < listView.getChildCount(); i++) {
                    String ids = listData.get(i).get(Myconst.MUSIC_ID);
                    ViewGroup v = (ViewGroup) listView.getChildAt(i);
                    ImageButton im = (ImageButton) v.findViewById(R.id.albumImage);
                    im.setImageBitmap(MyPlayer.getArtwork(SongList_activity.this,
                            Integer.getInteger(ids), true));
                }
            }
        }).start();
    }

    /**
     * 列表item短按事件监听
     */
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        HashMap<String, String> hs = listData.get(arg2);
        // 得到列 列就是在循环时每一次的put 这样就能获取到这首歌曲的路径了 此路径的表示为/mnt/sdcard/XXX.mp3
        Log.i(MyReceiver.tag, "" + arg2 + "        " + arg3 + arg1);
        String path = hs.get(Myconst.MUSIC_PATH);
        String name = hs.get(Myconst.MUSIC_TITLE);
        MyService.startActionplayorpause(SongList_activity.this, arg2);
        MyService.msindex = arg2;

        ImageButton b = (ImageButton) findViewById(R.id.play);
        FloatingActionButton b1 = (FloatingActionButton) AppointFragment.parent.findViewById(R.id.play);
        b.setBackgroundResource(R.drawable.pause_btn);
        b1.setIcon(R.drawable.pause_btn);

        Intent in = new Intent();
        in.putExtra(Myconst.MUSIC_TITLE, name);
        in.setAction(MyReceiver.ACTION_PLAYTIME_CHANGED);
        MyService.mloca.sendBroadcast(in);
    }

    public interface onmp3changed {
        void onchange(int index);

    }

    onmp3changed monmp3;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //ImageButton b = (ImageButton) v;
        FloatingActionButton b1 = (FloatingActionButton) AppointFragment.parent.findViewById(R.id.play);
        switch (v.getId()) {
            case R.id.up:
                // Intent in = new Intent(getActivity(), MyService.class);
                // in.setAction(MyService.action_up);
                //
                // getActivity().startService(in);
                //up.setBackground(R.drawable.);
                MyService.startActionup(SongList_activity.this);
                MyService.Notifityname();
                if (!MyService.mplyer.isplaying()) {
                    play.setBackgroundResource(R.drawable.pause_btn);
                    b1.setIcon(R.drawable.pause_btn);
                }
                break;
            case R.id.play:
                try {
                    if (MyService.mplyer.isplaying()) {

                        b1.setIcon(R.drawable.play_btn);
                        play.setBackgroundResource(R.drawable.play_btn);
                    } else {
                        play.setBackgroundResource(R.drawable.pause_btn);
                        b1.setIcon(R.drawable.pause_btn);

                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                MyService.startActionplayorpause(SongList_activity.this);
                MyService.Notifityname();
                break;
            case R.id.down:
                MyService.startActiondown(SongList_activity.this);
                if (!MyService.mplyer.isplaying()) {
                    play.setBackgroundResource(R.drawable.pause_btn);
                    b1.setIcon(R.drawable.pause_btn);
                }
                MyService.Notifityname();
                break;

            case R.id.backtomain:
                finish();
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
}
