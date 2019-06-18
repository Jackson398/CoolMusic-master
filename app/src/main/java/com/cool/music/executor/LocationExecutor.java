package com.cool.music.executor;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.StringUtils;

public class LocationExecutor implements IExecutor, AMapLocationListener {
    private Context mContext;
    private AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    public LocationExecutor(Context context) {
        this.mContext = context;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //Positioning success
                Preferences.saveAdcode(aMapLocation.getAdCode());
                Preferences.saveCityName(aMapLocation.getCity() + " " + aMapLocation.getDistrict());
                //todo
                //The local location service is not destroyed when the location is stopped
                mLocationClient.stopLocation();
                //Destroy the location client and destroy the local location service.
                mLocationClient.onDestroy();
                //todo
            } else {
                //When the location failed, the reason for the failure can be determined by the ErrCode
                //information (errInfo is the error information, see error code table for details).
                //todo
            }
        } else {
            //todo
        }
    }

    @Override
    public void execute() {
        final String adcode = Preferences.getAdcode();
        final String cityName = Preferences.getCityName();
        if (!StringUtils.isBlank(adcode)) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                //todo
            }, 1000);
            return;
        }

        //Initial positioning
        mLocationClient = new AMapLocationClient(mContext);
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
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onExecuteSuccess(Object o) {

    }

    @Override
    public void onExecuteFail(Exception e) {

    }
}
