package com.cool.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cool.music.R;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.LoggerUtils;
import com.cool.music.utils.StringUtils;

public class SplashWeatherActivity extends BaseActivity implements AMapLocationListener{
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    public static final String ADCODE = "adcode"; //regional code
    public static final String CITYNAME = "city"; //city name
    private String adcode;
    private String cityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_weather);
        adcode = Preferences.getAdcode();
        cityName = Preferences.getCityName();
        if (!StringUtils.isBlank(adcode)) {
            LoggerUtils.fmtDebug(getClass(), "Preferences adcode:%s,cityName:%s.", adcode, cityName);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashWeatherActivity.this, WeatherActivity.class);
                intent.putExtra(ADCODE, adcode);
                intent.putExtra(CITYNAME, cityName);
                startActivity(intent);
                finish();
            }, 1000);
            return;
        }

        //Initial positioning
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //Get the result of a positioning: this method default return false.
        mLocationOption.setOnceLocation(true);

        //Obtain the most accurate positioning result in the last 3s:
        //Set the setOnceLocationLatest(Boolean b) interface to true and the SDK will return the
        //most accurate location in the last 3s when starting the location.The setOnceLocation
        //(Boolean b) interface is set to true if set to true, and false if not.
        mLocationOption.setOnceLocationLatest(true);

        //Set whether to return address information (default return address information)
        mLocationOption.setNeedAddress(true);
        //Set whether to force WIFI refresh to improve precision, default is true, and force refresh
        mLocationOption.setWifiActiveScan(false);
        //Set if simulated location is allowed. The default is false. Simulated location is not allowed
        mLocationOption.setMockEnable(false);
        //The units are milliseconds, the default is 30000 milliseconds, and the timeout is
        //recommended to be no less than 8000 milliseconds
        mLocationOption.setHttpTimeOut(20000);
        //Turn off caching
        mLocationOption.setLocationCacheEnable(true);
        //Sets the positioning parameters for the positioning client object
        mLocationClient.setLocationOption(mLocationOption);
        //Start position
        mLocationClient.startLocation();
        mLocationClient.setLocationListener(this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                adcode = aMapLocation.getAdCode();
                cityName = aMapLocation.getCity() + " " + aMapLocation.getDistrict();
                //Positioning success
                Preferences.saveAdcode(adcode);
                Preferences.saveCityName(cityName);

                Intent intent = new Intent(SplashWeatherActivity.this, WeatherActivity.class);
                intent.putExtra(ADCODE, adcode);
                intent.putExtra(CITYNAME, cityName);

                //The local location service is not destroyed when the location is stopped
                mLocationClient.stopLocation();
                //Destroy the location client and destroy the local location service.
                mLocationClient.onDestroy();

                startActivity(intent);
                finish();
                LoggerUtils.fmtDebug(getClass(), "locationChanged, adcode:%s,cityName:%s.", adcode, cityName);
            } else {
                //When the location failed, the reason for the failure can be determined by the ErrCode
                //information (errInfo is the error information, see error code table for details).
                //todo
                LoggerUtils.fmtError(getClass(), "location Error, ErrCode:%s, errInfo:%s", aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
                finish();
            }
        } else {
            //todo
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
