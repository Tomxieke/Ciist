package com.ciist.entities;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class WeatherDayInfo {
    public String get_date() {
        return _date;
    }

    public String weather(){return _weather;}

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_month() {
        return _month;
    }

    public void set_month(String _month) {
        this._month = _month;
    }

    public String get_weatherImageUrl() {
        return _weatherImageUrl;
    }

    public void set_weatherImageUrl(String _weatherImageUrl) {
        this._weatherImageUrl = _weatherImageUrl;
    }

    public String get_weatherDescription() {
        return _weatherDescription;
    }

    public void set_weatherDescription(String _weatherDescription) {
        this._weatherDescription = _weatherDescription;
    }

    public String get_temperature() {
        return _temperature;
    }

    public void set_temperature(String _temperature) {
        this._temperature = _temperature;
    }

    public String get_wind() {
        return _wind;
    }

    public void set_wind(String _wind) {
        this._wind = _wind;
    }

    public String get_aqi() {
        return _aqi;
    }

    public void set_aqi(String _aqi) {
        this._aqi = _aqi;
    }

    public String get_relativeHumidity() {
        return _relativeHumidity;
    }

    public void set_relativeHumidity(String _relativeHumidity) {
        this._relativeHumidity = _relativeHumidity;
    }

    private String _date;
    private String _month;
    private String _weatherImageUrl;
    private String _weatherDescription;
    private String _temperature;
    private  String _wind;
    private  String _aqi;
    private String _relativeHumidity;
    private  String _weather;
public String get_weather(){return _weather;}

    public String setget_weather(){return _weather;}

    public String get_windp() {
        return _windp;
    }

    public void set_windp(String _windp) {
        this._windp = _windp;
    }

    private String _windp;

}
