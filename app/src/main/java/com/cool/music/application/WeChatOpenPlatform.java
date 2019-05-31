package com.cool.music.application;

import android.app.Application;
import android.content.Context;

import com.cool.music.constants.WeChatKey;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WeChatOpenPlatform {
    private Context mContext;
    private IWXAPI wxAPI;
    public static final int IMAGE_SIZE=32768;//微信分享图片大小限制
    private static volatile WeChatOpenPlatform instance;

    public WeChatOpenPlatform() {
    }

    public static WeChatOpenPlatform getInstance() {
        if (instance == null) {
            synchronized (WeChatOpenPlatform.class) {
                if (instance == null) {
                    instance = new WeChatOpenPlatform();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
//        EventBus.getDefault().register(mContext);//注册
        wxAPI = WXAPIFactory.createWXAPI(mContext, WeChatKey.WECHAT_APPID,true);
        wxAPI.registerApp(WeChatKey.WECHAT_APPID);
    }

    public IWXAPI getWxAPI() {
        return wxAPI;
    }
}
