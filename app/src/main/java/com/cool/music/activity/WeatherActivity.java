package com.cool.music.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.LinkAddress;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocalWeatherLive;
import com.cool.music.R;
import com.cool.music.application.AppCache;
import com.cool.music.http.HttpCallback;
import com.cool.music.http.HttpClient;
import com.cool.music.model.Casts;
import com.cool.music.model.Forecasts;
import com.cool.music.model.LiveWeather;
import com.cool.music.model.Lives;
import com.cool.music.model.TimeWeather;
import com.cool.music.utils.ToastUtils;
import com.cool.music.utils.WeatherUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity {
    private String cityName;
    private String adcode;
    private LinearLayout llWeather;
    private LinearLayout llWeatherInfo;
    private TextView tvHeader;
    private TextView tvTemperature;
    private TextView tvPosttime;
    private TextView tvHumidity;
    private TextView tvWindDirection;
    private TextView tvWeather;
    private ImageView ivWeather;
    private static final String GET_WEATHER_SUCCESS_STATUS = "OK";
    private static final String GET_WEATHER_MIN_COUNT = "1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        llWeather = findViewById(R.id.ll_weather_view);
        llWeatherInfo = findViewById(R.id.ll_weather_main_info_view);
        tvHeader = findViewById(R.id.tv_weather_main_header_label);
        ivWeather = findViewById(R.id.iv_weather_main);
        tvWeather = findViewById(R.id.tv_weather_main_info);
        tvWindDirection = findViewById(R.id.tv_weather_wind_direction);
        tvHumidity = findViewById(R.id.tv_weather_humidity);
        tvPosttime = findViewById(R.id.tv_weather_main_posttime);
        tvTemperature = findViewById(R.id.tv_weather_main_temperature);

        //todo
    }

    private void initData() {
        AMapLocalWeatherLive aMapLocalWeatherLive = AppCache.getInstance().getAMapLocalWeatherLive();
        if (aMapLocalWeatherLive != null) {
            updateView(aMapLocalWeatherLive);
        } else {
            //todo
        }
    }

    private void updateView(AMapLocalWeatherLive aMapLocalWeatherLive) {
        cityName = aMapLocalWeatherLive.getCity();
        adcode = aMapLocalWeatherLive.getCityCode();
        tvHeader.setText(cityName);

        HttpClient.getLiveWeather(adcode, HttpClient.WEATHER_TYPE_BASE, new HttpCallback<LiveWeather>() {
            @Override
            public void onSuccess(LiveWeather liveWeather) {
                if (GET_WEATHER_SUCCESS_STATUS.equals(liveWeather.getInfo()) && GET_WEATHER_MIN_COUNT.equals(liveWeather.getCount())) {
                    final Lives info = liveWeather.getLives().get(0);
                    tvTemperature.setText(info.getTemperature());
                    tvPosttime.setText(format(info.getReporttime()) + "更新");
                    tvWindDirection.setText(info.getWinddirection());
                    tvHumidity.setText("湿度" + info.getHumidity() + "%");

                    if (WeatherUtils.WeatherKV.containsKey(info.getWeather())) {
                        tvWeather.setText(info.getWeather());
                        ivWeather.setImageResource(WeatherUtils.WeatherKV.get(info.getWeather()));
                    } else {
                        tvTemperature.setText("N/A");
                    }
                } else {
                    tvTemperature.setText(R.string.service_unavailable);
                    ToastUtils.show(R.string.service_unavailable);
                }
            }

            @Override
            public void onFail(Exception e) {
                tvTemperature.setText(R.string.get_weather_info_fail);
                ToastUtils.show(R.string.get_weather_info_fail);
            }
        });

        HttpClient.getTimeWeather(adcode, HttpClient.WEATHER_TYPE_ALL, new HttpCallback<TimeWeather>() {
            @Override
            public void onSuccess(TimeWeather timeWeather) {
                if (GET_WEATHER_SUCCESS_STATUS.equals(timeWeather.getInfo()) && GET_WEATHER_MIN_COUNT.equals(timeWeather.getCount())) {
                    for (Forecasts forecasts : timeWeather.getForecasts()) {
                        for (Casts casts : forecasts.getCasts()) {
                            //todo
                        }
                    }
                }
            }

            @Override
            public void onFail(Exception e) {
                //todo
            }
        });
    }

    private String format(String posttime){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(posttime);

            return new SimpleDateFormat("HH:MM").format(date);
        } catch (ParseException e) {
            return "刚刚更新";
        }
    }
}
