package object;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
/**
 * 用户信息类
* */
public class Users extends BmobUser{

private List<String> car_id=new ArrayList<String>();
public Users() {
	
	this.setTableName("_User");
}
/**
 * @return the car_id
 */
public List<String> getCar_id() {
	return car_id;
}
/**
 * @param car_id the car_id to set
 */
public void setCar_id(List<String> car_id) {
	this.car_id = car_id;
}

}
