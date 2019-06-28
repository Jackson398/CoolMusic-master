package com.cool.music.application;

import android.app.Application;
import android.content.Context;

import com.cool.music.storage.DBManager;
import com.cool.music.utils.LoggerUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MusicApplication extends Application {
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = setupLeakCanary();
        AppCache.getInstance().init(this);
        DBManager.getInstance().init(this);
        WeChatOpenPlatform.getInstance().init(this);
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            LoggerUtils.fmtDebug(getClass(), "The process of LeakCanary for heap analysis is dedicated to the process your app run.");
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MusicApplication application = (MusicApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}


