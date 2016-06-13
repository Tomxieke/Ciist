package com.ciist.bean;



public class Pm25Beans {
	public String key;

	public int show_desc;

	public Pm25_2Beans pm25;

	public String dateTime;

	public String cityName;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getShow_desc() {
		return show_desc;
	}

	public void setShow_desc(int show_desc) {
		this.show_desc = show_desc;
	}

	public Pm25_2Beans getPm25() {
		return pm25;
	}

	public void setPm25(Pm25_2Beans pm25) {
		this.pm25 = pm25;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
