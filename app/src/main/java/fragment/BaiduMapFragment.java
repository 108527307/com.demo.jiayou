package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.platform.comapi.location.CoordinateType;
import com.example.jiayou.BNGuideActivity;
import com.example.jiayou.R;
import com.example.jiayou.Search_activity;
import com.example.jiayou.StationInfoActivity;
import com.example.jiayou.StationListActivity;
import com.gc.materialdesign.widgets.Dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import object.Petrol;
import object.Station;
import utils.StationData;


public class BaiduMapFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Context mContext;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private BDLocationListener mListener = new MyLocationListener();
    private ImageView iv_list, iv_loc,Daohang;
    private Toast mToast;
    private TextView tv_title_right, tv_name, tv_distance, tv_price_a, tv_price_b,start,end;
    private LinearLayout ll_summary;
    private Dialog selectDialog , loadingDialog;
    private StationData stationData;
    private BDLocation loc;
    private PoiInfo startlo,endlo;

    private ArrayList<Station> mList=new ArrayList<>();
    private Station mStation = null;

    private int mDistance = 3000;
    private Marker lastMarker = null;
    public static List<Activity> activityList = new LinkedList<Activity>();

    private static final String APP_FOLDER_NAME = "jiayou";
    private Button mDb06ll = null;
    private String mSDCardPath = null;

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";
    private String CoorType="";
    private ImageView jiaohuan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contactsLayout = inflater.inflate(R.layout.activity_main,
                container, false);
        //BaiduMapFragment.setContentView(R.layout.activity_main);
        mContext =getActivity();
        activityList.add(getActivity());
        BNOuterLogUtil.setLogSwitcher(true);
        initListener();
        if (initDirs()) {
            initNavi();
        }
        stationData = new StationData(mHandler);
        initView(contactsLayout);
        return contactsLayout;
    }
    private void initListener() {



        if (mDb06ll != null) {
            mDb06ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                }
            });
        }

    }
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default :
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
//            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
/**
*初始化百度导航
* */
    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("asd", authinfo);
                        Toast.makeText(getActivity(), authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                Toast.makeText(getActivity(), "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
                Toast.makeText(getActivity(), "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(getActivity(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, null);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void routeplanToNavi(PoiInfo start,PoiInfo end) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        sNode=new BNRoutePlanNode(start.location.longitude,start.location.latitude,start.name,CoorType );
        eNode=new BNRoutePlanNode(end.location.longitude,end.location.latitude,end.name, CoordinateType.BD09LL);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true, new RoutePlanListener(sNode));
        }
    }
/**
 * 路线规划类及回调
*
* */
    public class RoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public RoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNGuideActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(getActivity(), BNGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(getActivity(), "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSetting(){
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };
    /**
     * 初始化控件
    * */
    private void initView(View contactsLayout) {
        start= (TextView) contactsLayout.findViewById(R.id.start);
        end= (TextView) contactsLayout.findViewById(R.id.end);
        Daohang= (ImageView) contactsLayout.findViewById(R.id.btn_search1);
        Daohang.setOnClickListener(this);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        iv_list = (ImageView) contactsLayout.findViewById(R.id.iv_list);
        iv_list.setOnClickListener(this);
        iv_loc = (ImageView) contactsLayout.findViewById(R.id.iv_loc);
        iv_loc.setOnClickListener(this);
        tv_title_right = (TextView) contactsLayout.findViewById(R.id.tv_title_button);
        ll_summary = (LinearLayout) contactsLayout.findViewById(R.id.ll_summary);
        ll_summary.setOnClickListener(this);
        tv_name = (TextView) contactsLayout.findViewById(R.id.tv_name);
        tv_distance = (TextView) contactsLayout.findViewById(R.id.tv_distance);
        tv_price_a = (TextView) contactsLayout.findViewById(R.id.tv_price_a);
        tv_price_b = (TextView) contactsLayout.findViewById(R.id.tv_price_b);
          jiaohuan= (ImageView) contactsLayout.findViewById(R.id.jiaohuan);
        jiaohuan.setOnClickListener(this);
        mMapView = (MapView) contactsLayout.findViewById(R.id.bmapView);
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 高精度;
        // Battery_Saving:低精度.
        option.setCoorType("bd09ll"); // 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09
        // 返回百度经纬度坐标系 ：bd09ll
        option.setScanSpan(0);// 设置扫描间隔，单位毫秒，当<1000(1s)时，定时定位无效
        option.setIsNeedAddress(true);// 设置是否需要地址信息，默认为无地址
        option.setNeedDeviceDirect(true);// 在网络定位时，是否需要设备方向
        mLocationClient.setLocOption(option);

    }
    /**
     *设置marker
     * */
    public void setMarker(List<Station> list) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.marker, null);
        final TextView tv = (TextView) view.findViewById(R.id.tv_marker);
        for (int i = 0; i < list.size(); i++) {
            Station s = list.get(i);
            tv.setText((i + 1) + "");
            if (i == 0) {
                tv.setBackgroundResource(R.drawable.icon_focus_mark);
            } else {
                tv.setBackgroundResource(R.drawable.icon_mark);
            }
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
            LatLng latLng = new LatLng(s.getLat(), s.getLon());
            Bundle b = new Bundle();
            b.putParcelable("s", list.get(i));
            OverlayOptions oo = new MarkerOptions().position(latLng).icon(bitmap).title((i + 1) + "").extraInfo(b);
            if (i == 0) {
                lastMarker = (Marker) mBaiduMap.addOverlay(oo);
                mStation = s;
                showLayoutInfo((i + 1) + "", mStation);
            } else {
                mBaiduMap.addOverlay(oo);
            }
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                if (lastMarker != null) {
                    tv.setText(lastMarker.getTitle());
                    tv.setBackgroundResource(R.drawable.icon_mark);
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
                    lastMarker.setIcon(bitmap);
                }
                lastMarker = marker;
                String position = marker.getTitle();
                tv.setText(position);
                tv.setBackgroundResource(R.drawable.icon_focus_mark);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(tv);
                marker.setIcon(bitmap);
                mStation = marker.getExtraInfo().getParcelable("s");
                showLayoutInfo(position, mStation);
                return false;
            }
        });

    }
    /**
     *handler
     * */
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x01:
                    mList = (ArrayList<Station>) msg.obj;
                    setMarker(mList);
                    break;
                case 0x02:
                    showToast(String.valueOf(msg.obj));
                    break;
                default:
                    break;
            }

        }

    };
    /**
     *显示点击加油站信息
     * */
    public void showLayoutInfo(String position, Station s) {
        tv_name.setText(position + "." + s.getName());
        tv_distance.setText(s.getDistance() + "");
        List<Petrol> list = s.getGastPriceList();

        if (list != null && list.size() > 0) {
            tv_price_a.setText(list.get(0).getType() + " " + list.get(0).getPrice());
            if (list.size() > 1) {
                tv_price_b.setText(list.get(1).getType() + " " + list.get(1).getPrice());
            }
        }
        ll_summary.setVisibility(View.VISIBLE);
    }
    /**
     *搜索加油站
     * */
    public void searchStation(double lat, double lon, int distance) {
        mBaiduMap.clear();
        ll_summary.setVisibility(View.GONE);
        stationData.getStationData(lat, lon, distance, mContext);

    }
    /**
     *定位回掉函数
     * */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (location == null) {
                Toast.makeText(getActivity(),"定位失败",Toast.LENGTH_LONG).show();

                return;
            }
            loc = location;
            CoorType=location.getCoorType();
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            Log.i("success","ok");
            searchStation(location.getLatitude(), location.getLongitude(), mDistance);
        }

    }


    /**
     * 根据distance,获取当前位置附近的加油站
     * @param text
     * @param distance
     */
    public void distanceSearch(String text, int distance) {
        mDistance = distance;
        tv_title_right.setText(text);
        searchStation(loc.getLatitude(), loc.getLongitude(), distance);
        selectDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.jiaohuan:
                if(end.getText().toString().trim().equals("")&&!end.getHint().toString().trim().equals("我的位置"))
                    Toast.makeText(getActivity(),"地址输入完整后方可交换",Toast.LENGTH_LONG).show();
                else {
                    if(start.getText().toString().trim().equals("")){
                        start.setText(end.getText().toString().trim());
                        end.setHint("我的位置");
                        end.setText("");
                        PoiInfo t=startlo;
                        startlo=endlo;
                        endlo=t;
                    }else{
                        String te=start.getText().toString().trim();
                        start.setText(end.getText().toString().trim());
                        end.setText(te);
                        PoiInfo t=startlo;
                        startlo=endlo;
                        endlo=t;
                    }
                }
                    break;
            case R.id.iv_list:

                Intent listIntent = new Intent(mContext, StationListActivity.class);
                listIntent.putParcelableArrayListExtra("list", mList);
                listIntent.putExtra("locLat", loc.getLatitude());
                listIntent.putExtra("locLon", loc.getLongitude());
                startActivity(listIntent);
                break;
            case R.id.start:
                Intent intent=new Intent(getActivity(),Search_activity.class);
                intent.putExtra("flag",0);
                intent.putExtra("city",loc.getCity());
                startActivityForResult(intent, 1010);
                break;
            case R.id.end:
                Intent ntent=new Intent(getActivity(),Search_activity.class);
                ntent.putExtra("flag",1);
                ntent.putExtra("city",loc.getCity());
                startActivityForResult(ntent,1020);

            case R.id.iv_loc:
                int r = mLocationClient.requestLocation();
                switch (r) {
                    case 1:
                        showToast("服务没有启动。");
                        break;
                    case 2:
                        showToast("没有监听函数。");
                        break;
                    case 6:
                        showToast("请求间隔过短。");
                        break;

                    default:
                        break;
                }

                break;

            case R.id.ll_summary:
                Intent infoIntent = new Intent(mContext, StationInfoActivity.class);
                infoIntent.putExtra("s",  mStation);
                infoIntent.putExtra("locLat", loc.getLatitude());
                infoIntent.putExtra("locLon", loc.getLongitude());
                startActivity(infoIntent);
                break;
            case R.id.btn_search1:
                if(!start.getText().toString().trim().equals("")){
                    if(end.getText().toString().trim().equals(""))
                        Toast.makeText(getActivity(),"请输入目的地",Toast.LENGTH_LONG).show();
                    else if (BaiduNaviManager.isNaviInited()) {
                    routeplanToNavi(startlo,endlo);
                    Toast.makeText(getActivity(),"导航马上开始",Toast.LENGTH_LONG).show();
                }
                }else{
                    if(end.getText().toString().trim().equals(""))
                        Toast.makeText(getActivity(),"请输入目的地",Toast.LENGTH_LONG).show();
                    else if (BaiduNaviManager.isNaviInited()) {
                        PoiInfo t=new PoiInfo();
                        t.location=new LatLng(loc.getLatitude(),loc.getLongitude());
                        routeplanToNavi(t,endlo);
                        Toast.makeText(getActivity(),"导航马上开始",Toast.LENGTH_LONG).show();
                    }
                }
            default:
                break;
        }

    }


    /**
     * 显示通知
     *
     * @param msg
     */
    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mMapView.onResume();
        mLocationClient.start();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
        mLocationClient.stop();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
        mHandler = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1010)
        {
            PoiInfo  temp=data.getExtras().getParcelable("re");
            start.setText(temp.name);
            startlo=temp;
        }else if(requestCode==1020){
            PoiInfo  temp=data.getExtras().getParcelable("re");
            end.setText(temp.name);
            endlo=temp;
        }
    }
}
