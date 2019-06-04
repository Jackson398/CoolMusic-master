package com.cool.music.application;

import android.app.Application;

import com.cool.music.storage.DBManager;

public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCache.getInstance().init(this);
        DBManager.getInstance().init(this);
        WeChatOpenPlatform.getInstance().init(this);
    }
}


