package object;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * 订单类
 * Created by Administrator on 2016/5/10 0010.
 */
public class Orders extends BmobObject {
    private String UserName;
    private String UserObjectId;
private String GasClass;

    public String getGasClass() {
        return GasClass;
    }

    public void setGasClass(String gasClass) {
        GasClass = gasClass;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private boolean flag;
    private String GasNum;
    private String GasStationId;
    public String getGasStationId() {
        return GasStationId;
    }

    public void setGasStationId(String gasStationId) {
        GasStationId = gasStationId;
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserObjectId() {
        return UserObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        UserObjectId = userObjectId;
    }


    public String getGasNum() {
        return GasNum;
    }

    public void setGasNum(String gasNum) {
        GasNum = gasNum;
    }
}
