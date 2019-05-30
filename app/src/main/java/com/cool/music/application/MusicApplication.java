package com.cool.music.application;

import android.app.Application;

public class MusicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCache.getInstance().init(this);
        WeChatOpenPlatform.getInstance().init(this);
    }
}
