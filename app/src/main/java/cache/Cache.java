package cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.jiayou.Login_activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import Mods.Staticobject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import object.Cars;
import object.Users;

public class Cache {
private  List<String> list;

private  SharedPreferences user;
 public  void Writecache(Context context,String ObjectId,String username,List<String> list)
 {
	 user=context.getSharedPreferences("user",Context.MODE_PRIVATE);
	 Editor editor=user.edit();
	 editor.putString("username", username);
	 editor.putString("ObjectId", ObjectId);
	 editor.putBoolean("current", true);
	 Set<String>  lists=new HashSet<String>(list);
	 editor.putStringSet("car_id",lists);
	 editor.commit();
	 Log.i("fsdfsf", user.getString("ObjectId", "-1"));
 }
@SuppressWarnings("null")
public void SetcacheCars(Context context,List<String> list){
	user=context.getSharedPreferences("user", Context.MODE_PRIVATE);
	Editor editor=user.edit();
	editor.putStringSet("car_id", new HashSet<String>(list));
	editor.commit();
}
public  List<String> Getcachecars(Context context)
 {
	List<String> list = new ArrayList<String>();
	List<String>  temp=new ArrayList<String>();
	temp.add("dfisfak");
	Set<String> t=new HashSet<String>(temp);
	 //if(flag)
	 //{
	user=context.getSharedPreferences("user", Context.MODE_PRIVATE);
	list.addAll(user.getStringSet("car_id", t));
	//Toast.makeText(Cache.this, user.getString("ObjectId", "-1"), 1).show();
	
		
	return list;//}
	// else{
	//	 return null;
	// }
 }
	public String GetName(Context context)
	{
		user=context.getSharedPreferences("user",Context.MODE_PRIVATE);
		return user.getString("username","NoName");
	}
public  String GetObjectId(Context context)
{
	
	 //if(flag)
	 //{
	user=context.getSharedPreferences("user", Context.MODE_PRIVATE);
	return user.getString("ObjectId", "-1");
	
	//}
	// else{
	//	 return "-1";
	// }
}
	public void UpCars(Context context){
		user=context.getSharedPreferences("user", Context.MODE_PRIVATE);
		final Editor editor=user.edit();
		BmobQuery<Users> query=new BmobQuery<Users>();
		query.getObject(context, GetObjectId(context), new GetListener<Users>() {

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Users arg0) {
				// TODO Auto-generated method stub
				list=arg0.getCar_id();
				Set<String>  lists=new HashSet<String>(list);
				editor.putStringSet("car_id",lists);
				editor.commit();
			}
		});
	}
}
