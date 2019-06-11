package com.cool.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cool.music.application.AppCache;
import com.cool.music.constants.Actions;
import com.cool.music.model.Music;
import com.cool.music.service.AudioPlayer;
import com.cool.music.service.PlayService;

/**
 * When you click the pause(play), next or close button of cool music network push notification.
 * A related broadcast will be received {@link com.cool.music.application.Notifier#getRemoteViews(Context, Music, boolean)}.
 * The broadcast intent action is {@link #ACTION_STATUS_BAR}.
 */
public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "cool.music.STATUS_BAR_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_NEXT = "next";
    public static final String EXTRA_PLAY_PAUSE = "play_pause";
    public static final String EXTRA_PLAY_CLOSE = "play_close";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        String extra = intent.getStringExtra(EXTRA);
        if (TextUtils.equals(extra, EXTRA_NEXT)) {
            AudioPlayer.getInstance().next();
        } else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            AudioPlayer.getInstance().playPause();
        } else if (TextUtils.equals(extra, EXTRA_PLAY_CLOSE)) {
            AppCache.getInstance().clearStack();
            PlayService.startCommand(context, Actions.ACTION_STOP);
        }
    }
}
