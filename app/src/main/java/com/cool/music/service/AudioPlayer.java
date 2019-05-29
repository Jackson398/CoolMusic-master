package com.cool.music.service;

import android.content.Context;

import com.cool.music.application.AppCache;
import com.cool.music.model.Music;
import com.cool.music.storage.Preferences;

import java.util.List;

public class AudioPlayer {
    private Context context;
    private static AudioPlayer instance;

    private List<Music> musicList;

    public AudioPlayer() {
    }

    public void init(Context context) {

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
}
