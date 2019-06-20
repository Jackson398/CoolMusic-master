package com.cool.music.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.http.HttpCallback;
import com.cool.music.http.HttpClient;
import com.cool.music.model.Casts;
import com.cool.music.model.DisCity;
import com.cool.music.model.Districts;
import com.cool.music.model.DistrictsRoot;
import com.cool.music.model.Forecasts;
import com.cool.music.model.LiveWeather;
import com.cool.music.model.Lives;
import com.cool.music.model.TimeWeather;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.LoggerUtils;
import com.cool.music.utils.ToastUtils;
import com.cool.music.utils.WeatherUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String cityName;
    private String adcode;
    private String selectName;
    private String selectCode;
    private LinearLayout llWeather;
    private LinearLayout llWeatherInfo;
    private TextView tvHeader;
    private TextView tvTemperature;
    private TextView tvPosttime;
    private TextView tvHumidity;
    private TextView tvWindDirection;
    private TextView tvWeather;
    private ImageView ivWeather;
    private ImageView ivSearch;
    private Button btnSwitchLocation;
    private List<Integer> cities;
    private List<String> cityKeyVal;
    private List<String> cityVal;
    private Gson jsonConverter = new Gson();
    AutoCompleteTextView autoCompleteTextView;
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
        btnSwitchLocation = findViewById(R.id.change_location_btn);
        btnSwitchLocation.setOnClickListener(this);
        ivSearch = findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(this);
        autoCompleteTextView = findViewById(R.id.tv_select_city);
        autoCompleteTextView.setOnItemClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        cityName = intent.getStringExtra(SplashWeatherActivity.CITYNAME);
        adcode = intent.getStringExtra(SplashWeatherActivity.ADCODE);
        tvHeader.setText(cityName);

        cities = WeatherUtils.getCities();
        
        cityKeyVal = new ArrayList<>();
        cityVal = new ArrayList<>();
        findCity();
        ArrayAdapter<String> autoTextString = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,cityKeyVal);
        autoCompleteTextView.setAdapter(autoTextString);

        updateView();
    }

    private void updateView() {
        LoggerUtils.fmtDebug(getClass(), "Get weather info, adcode:%s", adcode);
        //todo
        HttpClient.getLiveWeather(adcode, HttpClient.WEATHER_TYPE_BASE, new HttpCallback<LiveWeather>() {
            @Override
            public void onSuccess(LiveWeather liveWeather) {
                if (GET_WEATHER_SUCCESS_STATUS.equals(liveWeather.getInfo()) && GET_WEATHER_MIN_COUNT.equals(liveWeather.getCount())) {
                    runOnUiThread(()->{
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
                    });
                } else {
                    runOnUiThread(() -> {
                        tvTemperature.setText(R.string.service_unavailable);
                        ToastUtils.show(R.string.service_unavailable);
                    });
                }
            }

            @Override
            public void onFail(Exception e) {
                runOnUiThread(() ->{
                    tvTemperature.setText(R.string.get_weather_info_fail);
                    ToastUtils.show(R.string.get_weather_info_fail);
                });
            }
        });

        HttpClient.getTimeWeather(adcode, HttpClient.WEATHER_TYPE_ALL, new HttpCallback<TimeWeather>() {
            @Override
            public void onSuccess(TimeWeather timeWeather) {
                if (GET_WEATHER_SUCCESS_STATUS.equals(timeWeather.getInfo()) && GET_WEATHER_MIN_COUNT.equals(timeWeather.getCount())) {
                    runOnUiThread(()->{
                        for (Forecasts forecasts : timeWeather.getForecasts()) {
                            for (Casts casts : forecasts.getCasts()) {
                                View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.weather_item, llWeatherInfo,false);
                                TextView date = view.findViewById(R.id.item_date);
                                TextView maxTemperature = view.findViewById(R.id.item_max_temperature);
                                TextView minTemperature = view.findViewById(R.id.item_min_temperature);
                                ImageView ivIcon = view.findViewById(R.id.item_weather_icon);
                                TextView currentWeather = view.findViewById(R.id.item_weather);
                                TextView week = view.findViewById(R.id.item_week);

                                date.setText(getDay(casts.getDate()));
                                maxTemperature.setText(casts.getDaytemp() + "°");
                                minTemperature.setText(casts.getNighttemp() + "°");
                                ivIcon.setImageResource(WeatherUtils.getWeatherIcon(casts.getDayweather()));
                                currentWeather.setText(casts.getDayweather());
                                week.setText(getWeek(casts.getWeek()));

                                llWeatherInfo.addView(view);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFail(Exception e) {
                //todo
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object obj = adapterView.getItemAtPosition(position);
        int index = cityKeyVal.indexOf(obj);
        selectCode = cityVal.get(index);
        selectName = obj.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_location_btn:
                ivSearch.setVisibility(View.VISIBLE);
                autoCompleteTextView.setVisibility(View.VISIBLE);
                tvHeader.setVisibility(View.GONE);
                btnSwitchLocation.setVisibility(View.GONE);
                break;
            case R.id.iv_search:
                if (!TextUtils.isEmpty(selectCode) && !TextUtils.isEmpty(selectName)){
                    Preferences.saveAdcode(selectCode);
                    Preferences.saveCityName(selectName);
                    adcode = selectCode;
                    cityName = selectName;
                    tvHeader.setText(cityName);
                    llWeatherInfo.removeViews(2, 4);
                    updateView();
                }else {
                    //todo
                }
                ivSearch.setVisibility(View.GONE);
                autoCompleteTextView.setVisibility(View.GONE);
                tvHeader.setVisibility(View.VISIBLE);
                btnSwitchLocation.setVisibility(View.VISIBLE);
                break;
        }
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

    private String getDay(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date toDate = dateFormat.parse(date);

            return new SimpleDateFormat("dd").format(toDate);
        } catch (ParseException e) {
            return "N/A";
        }
    }

    private String getWeek(String week){
        switch (week)
        {
            case "1":
                return "星期一";
            case "2":
                return "星期二";
            case "3":
                return "星期三";
            case "4":
                return "星期四";
            case "5":
                return "星期五";
            case "6":
                return "星期六";
            default:
                return "星期日";
        }
    }

    private void findCity(){
        for (int i : cities){
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = getResources().openRawResource(i);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line = "";
                while ((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }

                DistrictsRoot dis = jsonConverter.fromJson(stringBuilder.toString(),DistrictsRoot.class);

                if (dis.getDistricts().size() > 0){
                    List<Districts> _dis = dis.getDistricts();
                    if (_dis.size() > 0){
                        Districts currentDis = _dis.get(0);

                        DisCity disCity = new DisCity();
                        disCity.setAdcode(currentDis.getAdcode());
                        disCity.setName(currentDis.getName());
                        disCity.setDistricts(currentDis.getDistricts());

                        whileCity(currentDis.getDistricts(),disCity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void whileCity(List<DisCity> districtses, DisCity parenCity){
        for (DisCity c : districtses){
            if (c.getDistricts().size() > 0){
                whileCity(c.getDistricts(),c);
            }else {
                cityKeyVal.add(parenCity.getName() + " " + c.getName());
                cityVal.add(c.getAdcode());
            }
        }
    }



}
