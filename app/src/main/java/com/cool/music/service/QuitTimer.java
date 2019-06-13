package com.cool.music.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;

import com.cool.music.application.AppCache;
import com.cool.music.constants.Actions;
import com.cool.music.storage.Preferences;

public class QuitTimer {
    private Context context;
    private OnTimerListener listener;
    private Handler handler;
    private long timerRemain;
    private volatile static QuitTimer instance;

    public interface OnTimerListener {
        void onTimer(long remain);
    }

    public static QuitTimer getInstance() {
        if (instance == null) {
            synchronized (QuitTimer.class) {
                if (instance == null) {
                    instance = new QuitTimer();
                }
            }
        }
        return instance;
    }

    public QuitTimer() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void setOnTimerListener(OnTimerListener listener) {
        this.listener = listener;
    }

    public void start(long millisecond) {
        stop();
        if (millisecond > 0) {
            timerRemain = millisecond + DateUtils.SECOND_IN_MILLIS;
            handler.post(mQuitRunnable);
        } else {
            timerRemain = 0;
            if (listener != null) {
                listener.onTimer(timerRemain);
            }
        }
    }

    public void stop() {
        handler.removeCallbacks(mQuitRunnable);
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            timerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (timerRemain > 0) {
                if (listener != null) {
                    listener.onTimer(timerRemain);
                }
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
            } else {
                AppCache.getInstance().clearStack();
                PlayService.startCommand(context, Actions.ACTION_STOP);
            }
        }
    };
}
