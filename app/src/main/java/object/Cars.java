package object;

import cn.bmob.v3.BmobObject;
/**
 * 车辆信息类
 * */
public class Cars extends BmobObject {
	String CarBrand;
	String CarClass;
	String CarId;
	String EngineId;
	String Km;
	String Flue;
	String BodyLevel;
	String Engine;
	String Transmission;
	String CarHeadLight;
	String UserId;

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String VIN) {
		this.VIN = VIN;
	}

	String VIN;

	public String getCarBrand() {
		return CarBrand;
	}

	public void setCarBrand(String carBrand) {
		CarBrand = carBrand;
	}

	public String getTransmission() {
		return Transmission;
	}

	public void setTransmission(String transmission) {
		Transmission = transmission;
	}

	public String getCarId() {
		return CarId;
	}

	public void setCarId(String carId) {
		CarId = carId;
	}

	public String getEngineId() {
		return EngineId;
	}

	public void setEngineId(String engineId) {
		EngineId = engineId;
	}

	public String getKm() {
		return Km;
	}

	public void setKm(String km) {
		Km = km;
	}

	public String getFlue() {
		return Flue;
	}

	public void setFlue(String flue) {
		Flue = flue;
	}

	public String getBodyLevel() {
		return BodyLevel;
	}

	public void setBodyLevel(String bodyLevel) {
		BodyLevel = bodyLevel;
	}

	public String getCarClass() {
		return CarClass;
	}

	public void setCarClass(String carClass) {
		CarClass = carClass;
	}

	public String getCarHeadLight() {
		return CarHeadLight;
	}

	public void setCarHeadLight(String carHeadLight) {
		CarHeadLight = carHeadLight;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getEngine() {
		return Engine;
	}

	public void setEngine(String engine) {
		Engine = engine;
	}
}