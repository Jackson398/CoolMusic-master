package com.cool.music.service;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.cool.music.application.AppCache;
import com.cool.music.model.Music;
import com.cool.music.storage.DBManager;
import com.cool.music.storage.Preferences;

import java.util.List;

public class AudioPlayer {
    private Context context;
    private static AudioPlayer instance;

    private List<Music> musicList;

    public AudioPlayer() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        musicList = DBManager.getInstance().getMusicDao().queryBuilder().build().list();
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            synchronized (AppCache.class) {
                if (instance == null) {
                    instance = new AudioPlayer();
                }
            }
        }
        return instance;
    }

    public int getPlayPosition() {
        int position = Preferences.getPlayPosition();
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            Preferences.savePlayPosition(position);
        }
        return position;
    }

    public void addAndPlay(Music music) { //将播放音乐添加到数据库并播放
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            DBManager.getInstance().getMusicDao().insert(music);
            position = musicList.size() - 1;
        }
        //TODO
    }
}
