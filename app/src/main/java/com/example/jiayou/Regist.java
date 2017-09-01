package com.example.jiayou;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import object.Users;
import utils.ActivityManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author c_875
 *
 */

public class Regist extends ActivityManager implements OnClickListener{
	private EditText name;
	private EditText pas1;
	private EditText pas2;
	private EditText yanzhenid;
	private Button submit;
	private Button qinqiu;
	private EditText email;
	private int index=60;
	private String phone=null;
	private String password1=null;
	private String password2=null;
	private String em=null;
	private String yanzhen=null;
	Timer tim;
	Users u=null;
	private  Handler uihandler=new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch(msg.arg1)
			{
			case 0001:
				Toast.makeText(Regist.this, "验证错误:"+msg.obj.toString(), Toast.LENGTH_SHORT).show();
				yanzhenid.setText("");
				qinqiu.setText("获取验证码");
				index=60;
				break;
			case 0002:
				Toast.makeText(Regist.this, "验证成功",  Toast.LENGTH_SHORT).show();
				break;
			case 0003:	
				Toast.makeText(Regist.this, "登陆异常"+msg.obj.toString(),  Toast.LENGTH_SHORT).show();
				index=60;
				qinqiu.setText("获取验证码");
				break;
			case 0004:
				qinqiu.setText("("+index+")后可再次获取");
				if(index==0)
				{

					tim.cancel();
					Message m=new Message();
					m.arg1=0006;
					qinqiu.setClickable(true);
					uihandler.sendMessage(m);

				}
				break;
			case 0005:
				Toast.makeText(Regist.this, msg.obj.toString(),  Toast.LENGTH_SHORT).show();break;

			default:break;
			}

		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initview();
	}
	/**
	 * 初始化控件
	 */
	private void initview() {
		// TODO Auto-generated method stub
		name=(EditText) findViewById(R.id.username);
		pas1=(EditText) findViewById(R.id.password);
		pas2=(EditText) findViewById(R.id.password2);
		yanzhenid=(EditText) findViewById(R.id.yanzhenid);
		submit=(Button) findViewById(R.id.submit);
		qinqiu=(Button) findViewById(R.id.qinqiuyanzhen);
		email=(EditText) findViewById(R.id.email);
		qinqiu.setText("获取验证");
		qinqiu.setOnClickListener(this);
		submit.setOnClickListener(this);

	}
	/**
	 * 发送短信
	 */
	private void getsms()
	{
		tim=new Timer();
		tim.schedule(new TimerTask() {
			@Override
			public void run() {
				Message m = new Message();
				m.arg1 = 0004;

				qinqiu.setClickable(false);
				index--;
				uihandler.sendMessage(m);
			}
		}, 0, 1000);


                BmobSMS.requestSMSCode(Regist.this, phone, "叼炸天模板", new RequestSMSCodeListener() {

					@Override
					public void done(Integer arg0, BmobException arg1) {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.arg1 = 0005;
						//Log.i("sgvs", arg1.toString());
						String a = arg1 != null ? (arg1.toString()) : ("发送成功");
						msg.obj = a;
						uihandler.sendMessage(msg);
					}
				});
				



	}
	/**
	 *
	 * 修改手机号验证信息

	 * */
	private void UpPhone(String phone){
		Users user =new Users();
		user.setMobilePhoneNumber(phone);
		user.setMobilePhoneNumberVerified(true);
		Users cur = Users.getCurrentUser(Regist.this,Users.class);
		user.update(getApplicationContext(), cur.getObjectId(),new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i("smile","手机号码绑定成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("smile","手机号码绑定失败："+arg0+"-"+arg1);
			}
		});
	}
	/**
	 * 登陆
	 */
	private void regist()

	{ 
		u=new  Users();
		u.setUsername(phone);
		u.setPassword(password1);
		u.setEmail(em);

		u.signUp(Regist.this, new SaveListener() {

			@Override
			public void onSuccess() {

				Message m=new Message();
				m.arg1=0002;
				
				uihandler.sendMessage(m);
				UpPhone(phone);
				finish();
				//Intent intent=new Intent();
				//intent.putExtra("ObjectId", u.getObjectId());
				//intent.setClass(Regist.this, Main_activity.class);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				Message m=new Message();
				m.arg1=0003;
				m.obj=arg1;
				uihandler.sendMessage(m);

			}
		});

	}
	/**
	 * 验证短信验证码
	 * @param phone
	 * @param smscode
	 */
	private void versms(String phone,String smscode)
	{
		
		BmobSMS.verifySmsCode(Regist.this, phone, smscode, new VerifySMSCodeListener() {

			@Override
			public void done(BmobException arg0) {
				if(arg0==null)
				{
					regist();
				}
				else {
					Message msg=new Message();
					msg.arg1=0001;
					msg.obj=arg0;
					uihandler.sendMessage(msg);
				}

			}
		});
	}
	/**
	 *
处理点击事件
	 * */
	@Override
	public void onClick(View v) {
		phone=name.getText().toString().trim();
		password1=pas1.getText().toString().trim();
		password2=pas2.getText().toString().trim();
		yanzhen=yanzhenid.getText().toString().trim();
		em=email.getText().toString().trim();		
		switch(v.getId())
		{
		case R.id.submit:
			if(yanzhen.equals(""))
				Toast.makeText(Regist.this, "请输入验证码", Toast.LENGTH_SHORT).show();
					else if(phone.equals("")||password1.equals("")||password2.equals("")||em.equals(""))
				Toast.makeText(Regist.this, "请填完整信息", Toast.LENGTH_SHORT).show();
			else if(!password1.equals(password2))
			{
				Toast.makeText(Regist.this, "密码输入不一致", Toast.LENGTH_SHORT).show();
				pas1.setText("");
				pas2.setText("");
				pas1.requestFocus();
			}else
				versms(phone, yanzhen);
			break;
		case R.id.qinqiuyanzhen:

			getsms();
			break;
		default:break;

		}
	};
}
