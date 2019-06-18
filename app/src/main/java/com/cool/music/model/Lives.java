package com.cool.music.model;

import com.cool.music.utils.WeatherUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Live weather data information
 */
public class Lives {
    @SerializedName("province")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("adcode")
    private String adcode;
    @SerializedName("weather")
    private String weather;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("winddirection")
    private String winddirection;
    @SerializedName("windpower")
    private String windpower;
    @SerializedName("humidity")
    private String humidity;
    @SerializedName("reporttime")
    private String reporttime;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return this.temperature + "°";
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWinddirection() {
        return this.winddirection + "风";
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public String getWindpower() {
        if (WeatherUtils.weatherWind.containsKey(windpower)){
            return WeatherUtils.weatherWind.get(windpower);
        }else {
            return "N/A";
        }
    }

    public void setWindpower(String windpower) {
        this.windpower = windpower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }
}
