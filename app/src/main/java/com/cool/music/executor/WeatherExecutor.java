package com.cool.music.executor;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.cool.music.R;
import com.cool.music.activity.MusicActivity;
import com.cool.music.application.AppCache;
import com.cool.music.utils.LoggerUtils;
import com.cool.music.utils.StringUtils;

import java.util.Calendar;

public class WeatherExecutor implements IExecutor, AMapLocalWeatherListener {
    private Context mContext;
    private LinearLayout llWeather;
    private ImageView ivIcon;
    private TextView tvTemp;
    private TextView tvCity;
    private TextView tvWind;

    public WeatherExecutor(Context context, View navigationHeader) {
        mContext = context.getApplicationContext();
        initView(navigationHeader);
    }

    private void initView(View view) {
        llWeather = view.findViewById(R.id.ll_weather);
        ivIcon = view.findViewById(R.id.iv_weather_icon);
        tvTemp = view.findViewById(R.id.tv_weather_temp);
        tvCity = view.findViewById(R.id.tv_weather_city);
        tvWind = view.findViewById(R.id.tv_weather_wind);
    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        if (aMapLocalWeatherLive != null && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            AppCache.getInstance().setAMapLocalWeatherLive(aMapLocalWeatherLive);
            updateView(aMapLocalWeatherLive);
        } else {
            LoggerUtils.debug(getClass(), "获取天气预报失败");
        }

        release();
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {

    }

    /**
     * This method is used to obtain local weather information.You need permission to get the current
     * location before calling this method. see {@link MusicActivity#updateWeather()}<br>
     *
     */
    @Override
    public void execute() {
        AMapLocalWeatherLive aMapLocalWeatherLive = AppCache.getInstance().getAMapLocalWeatherLive();
        if (aMapLocalWeatherLive != null) {
            updateView(aMapLocalWeatherLive);
            release();
        } else {
            LocationManagerProxy mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
            mLocationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
        }
    }

    private void updateView(AMapLocalWeatherLive aMapLocalWeatherLive) {
        llWeather.setVisibility(View.VISIBLE);
        ivIcon.setImageResource(getWeatherIcon(aMapLocalWeatherLive.getWeather()));
        tvTemp.setText(mContext.getString(R.string.weather_temp, aMapLocalWeatherLive.getTemperature()));
        tvCity.setText(aMapLocalWeatherLive.getCity());
        tvWind.setText(mContext.getString(R.string.weather_wind, aMapLocalWeatherLive.getWindDir(),
                aMapLocalWeatherLive.getWindPower(), aMapLocalWeatherLive.getHumidity()));
    }

    private int getWeatherIcon(String weather) {
        if (StringUtils.isBlank(weather)) {
            return R.mipmap.ic_weather_sunny;
        }

        if (weather.contains("-")) {
            weather = weather.substring(0, weather.indexOf("-"));
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int resId;
        if (weather.contains("晴")) {
            if (hour >= 7 && hour < 19) {
                resId = R.mipmap.ic_weather_sunny;
            } else {
                resId = R.mipmap.ic_weather_sunny_night;
            }
        } else if (weather.contains("多云")) {
            if (hour >= 7 && hour < 19) {
                resId = R.mipmap.ic_weather_cloudy;
            } else {
                resId = R.mipmap.ic_weather_cloudy_night;
            }
        } else if (weather.contains("阴")) {
            resId = R.mipmap.ic_weather_overcast;
        } else if (weather.contains("雷阵雨")) {
            resId = R.mipmap.ic_weather_thunderstorm;
        } else if (weather.contains("雨夹雪")) {
            resId = R.mipmap.ic_weather_sleet;
        } else if (weather.contains("雨")) {
            resId = R.mipmap.ic_weather_rain;
        } else if (weather.contains("雪")) {
            resId = R.mipmap.ic_weather_snow;
        } else if (weather.contains("雾") || weather.contains("霾")) {
            resId = R.mipmap.ic_weather_foggy;
        } else if (weather.contains("风") || weather.contains("飑")) {
            resId = R.mipmap.ic_weather_typhoon;
        } else if (weather.contains("沙") || weather.contains("尘")) {
            resId = R.mipmap.ic_weather_sandstorm;
        } else {
            resId = R.mipmap.ic_weather_cloudy;
        }
        return resId;
    }

    @Override
    public void onPrepare() {
        new LocationExecutor(mContext).execute();
    }

    @Override
    public void onExecuteSuccess(Object o) {

    }

    @Override
    public void onExecuteFail(Exception e) {

    }

    private void release() {
        mContext = null;
        llWeather = null;
        ivIcon = null;
        tvTemp = null;
        tvCity = null;
        tvWind = null;
    }
}
