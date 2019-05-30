package com.cool.music.utils;

import com.cool.music.application.WeChatOpenPlatform;
import com.cool.music.model.Music;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

public class WeChatUtils {

    public static void shareMusicToWeChatFriends(Music music) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "www.baidu.com";//分享url
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = music.getTitle();
        msg.description = music.getArtist() + "-" + music.getAlbum();
        msg.thumbData = CoverLoader.getInstance().getThumbData(music);//封面图片byte数组

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        WeChatOpenPlatform.getInstance().getWxAPI().sendReq(req);
    }
}
