package com.test.pulldownrefreshpullupload_master.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.LongSparseArray;

import com.amap.api.location.AMapLocalWeatherLive;
import com.test.pulldownrefreshpullupload_master.executor.DownloadMusicInfo;
import com.test.pulldownrefreshpullupload_master.model.Music;
import com.test.pulldownrefreshpullupload_master.model.SheetInfo;

import java.util.ArrayList;
import java.util.List;

//缓存类
public class AppCache {
    private Context mContext;
    private final List<Music> mLoclMusicList = new ArrayList<Music>();
    private final List<SheetInfo> mShellList = new ArrayList<SheetInfo>();
    private final List<Activity> mActivityStack = new ArrayList<Activity>();
    private final LongSparseArray<DownloadMusicInfo> mDownloadList = new LongSparseArray<DownloadMusicInfo>();
    private AMapLocalWeatherLive mAMapLocalWeatherLive;
    private static AppCache instance;

    public AppCache() {

    }

    public static AppCache getInstance() {
        if (instance == null) {
            instance = new AppCache();
        }
        return instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    public void clearStack() {
        List<Activity> activityStack = mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

    public Context getContext() {
        return mContext;
    }

    public List<Music> getLoclMusicList() {
        return mLoclMusicList;
    }

    public List<SheetInfo> getShellList() {
        return mShellList;
    }

    public List<Activity> getActivityStack() {
        return mActivityStack;
    }

    public LongSparseArray<DownloadMusicInfo> getDownloadList() {
        return mDownloadList;
    }

    public AMapLocalWeatherLive getAMapLocalWeatherLive() {
        return mAMapLocalWeatherLive;
    }

    public void setAMapLocalWeatherLive(AMapLocalWeatherLive aMapLocalWeatherLive) {
        mAMapLocalWeatherLive = aMapLocalWeatherLive;
    }

    /**
     * 接口回调简化监测Activity的生命周期，在一个类中作统一处理.
     * @since 1.4
     */
    public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mActivityStack.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityStack.remove(activity);
        }
    }
}
