package fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jiayou.AboutUs_activity;
import com.example.jiayou.Carslist;
import com.example.jiayou.ContactUs_activity;
import com.example.jiayou.MyAccount_activity;
import com.example.jiayou.R;

import cache.Cache;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MineFragment extends Fragment  implements OnClickListener{
LinearLayout carinfo,myAccount,recommend,contactUs,about;
	TextView username;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.mine_layout,
				container, false);

		initview(settingLayout);
		return settingLayout;
	}
	private void initview(View v) {
		contactUs= (LinearLayout) v.findViewById(R.id.contactUs);
		about= (LinearLayout) v.findViewById(R.id.about);
		contactUs.setOnClickListener(this);
		about.setOnClickListener(this);
       recommend= (LinearLayout) v.findViewById(R.id.recommend);
		recommend.setOnClickListener(this);
		carinfo=(LinearLayout) v.findViewById(R.id.carinfo);
		carinfo.setOnClickListener(this);
		myAccount= (LinearLayout) v.findViewById(R.id.myAccount);
		myAccount.setOnClickListener(this);
		username=(TextView)v.findViewById(R.id.username);
		username.setText(new Cache().GetName(getActivity()));

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.myAccount:
                           Intent intent1=new Intent(getActivity(), MyAccount_activity.class);
				          getActivity().startActivity(intent1);
				break;
			case R.id.carinfo:
				Intent intent = new Intent(getActivity(), Carslist.class);
				getActivity().startActivity(intent);
				break;
			case R.id.recommend:
				showShare();
				break;
			case R.id.contactUs:
				Intent intent11 = new Intent(getActivity(), ContactUs_activity.class);
				getActivity().startActivity(intent11);
				break;
			case R.id.about:
				Intent intent111 = new Intent(getActivity(), AboutUs_activity.class);
				getActivity().startActivity(intent111);
			default:break;
			}

	}
	/**
	 *分享功能
	 * */
	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		//oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("分享至");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
		oks.show(getActivity());
	}
}
