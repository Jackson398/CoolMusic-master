package com.cool.music.service;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.cool.music.application.Notifier;
import com.cool.music.enums.PlayModeEnum;
import com.cool.music.model.Music;
import com.cool.music.receiver.NoisyAudioStreamReceiver;
import com.cool.music.storage.DBManager;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioPlayer {
    private Context context;
    private volatile static AudioPlayer instance;

    private static final int STATE_IDLE = 0; //闲置
    private static final int STATE_PREPARING = 1; //准备就绪
    private static final int STATE_PLAYING = 2; //正在播放
    private static final int STATE_PAUSE = 3; //暂停
    private int state = STATE_IDLE;
    private static final long TIME_UPDATE = 300L;

    private List<Music> musicList;
    private MediaPlayer mediaPlayer;
    private AudioFocusManager audioFocusManager;
    private Handler handler;
    private NoisyAudioStreamReceiver noisyReceiver;
    private IntentFilter noisyFilter;
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE); //每隔0.3s更新一下播放进度
        }
    };

    public AudioPlayer() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        musicList = DBManager.getInstance().getMusicDao().queryBuilder().build().list();
        audioFocusManager = new AudioFocusManager(context);
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());
        noisyReceiver = new NoisyAudioStreamReceiver();
        noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mediaPlayer.setOnCompletionListener(mp -> next()); //网络流媒体播放结束时回调
        mediaPlayer.setOnPreparedListener(mp -> { //当装载媒流体完毕时回调
            if (isPreparing()) {
                startPlayer();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> { //网络流媒体的缓冲发生变化时发生回调
            for (OnPlayerEventListener listener : listeners) {
                listener.onBufferingUpdate(percent);
            }
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> { //发生错误时回调
           return false;
        });
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            synchronized (AudioPlayer.class) {
                if (instance == null) {
                    instance = new AudioPlayer();
                }
            }
        }
        return instance;
    }

    public void next() {
        if (musicList.isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                play(new Random().nextInt(musicList.size()));
                break;
            case SINGLE:
                play(getPlayPosition());
                break;
            case LOOP:
            default:
                play(getPlayPosition() + 1);
                break;
        }
    }

    public void prev() {
        if (musicList.isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                play(new Random().nextInt(musicList.size()));
                break;
            case SINGLE:
                play(getPlayPosition());
                break;
            case LOOP:
            default:
                play(getPlayPosition() - 1);
                break;
        }
    }

    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getPlayPosition());
        }
    }

    public void stopPlayer() {
        if (isIdle()) {
            return;
        }

        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    //获取音乐在播放列表中的位置
    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getPlayPosition() {
        int position = Preferences.getPlayPosition();
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            Preferences.savePlayPosition(position);
        }
        return position;
    }

    public void addAndPlay(Music music) { //将播放音乐添加到音乐播放列表缓存和数据库并播放
        int position = musicList.indexOf(music);
        if (position < 0) { //之前musicList不存在music
            musicList.add(music);
            DBManager.getInstance().getMusicDao().insert(music);
            position = musicList.size() - 1;
        }
        play(position);
}

    public void play(int position) {
        if (musicList.isEmpty()) {
            return;
        }

        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }

        setPlayPosition(position); //存储正在播放的音乐的在列表中位置到sharePreferences
        Music music = getPlayMusic();

        try {
            mediaPlayer.reset(); //重置MediaPlayer至未初始化状态
            mediaPlayer.setDataSource(music.getPath()); //通过一个具体的路径来设置MediaPlayer的数据源，path可以是本地的一个路径，也可以是一个网络路径
            mediaPlayer.prepareAsync(); //异步的方式装载流媒体文件，装载完毕后回调OnPreparedListener.onPrepared()方法
            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                listener.onChange(music);
            }
            Notifier.getInstance().showPlay(music); //改变remoteView音乐播放状态
            MediaSessionManager.getInstance().updateMetaData(music);
            MediaSessionManager.getInstance().updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show("当前歌曲无法播放");
        }
    }

    //添加播放事件监听器，外类使用该方法添加一个监听器，具体方法实现由外类实现
    public void addOnPlayEventListener(OnPlayerEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnPlayEventListener(OnPlayerEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            MediaSessionManager.getInstance().updatePlaybackState();
            for (OnPlayerEventListener listener : listeners) {
                listener.onPublish(msec);
            }
        }
    }

    public void delete(int position) {
        int playPosition = getPlayPosition();
        Music music = musicList.remove(position);
        DBManager.getInstance().getMusicDao().delete(music);
        if (playPosition > position) {
            setPlayPosition(playPosition - 1);
        } else if (playPosition == position) {
            if (isPlaying() || isPreparing()) {
                setPlayPosition(playPosition - 1);
                next();
            } else {
                stopPlayer();
                for (OnPlayerEventListener listener : listeners) {
                    listener.onChange(getPlayMusic());
                }
            }
        }
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public void  startPlayer() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (audioFocusManager.requestAudioFocus()) { //获取焦点
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable); //发送消息到主线程消息循环系统，更新播放进度
            Notifier.getInstance().showPlay(getPlayMusic()); //改变remoteView音乐播放状态
            MediaSessionManager.getInstance().updatePlaybackState();
            context.registerReceiver(noisyReceiver, noisyFilter);
            for (OnPlayerEventListener listener : listeners) {
                listener.onPlayerStart();
            }
        }
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public Music getPlayMusic() {
        if (musicList.isEmpty()) {
            return null;
        }
        return musicList.get(getPlayPosition());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void pausePlayer() {
        pausePlayer(true);
    }

    /**
     * Invoke the method to pause player.
     * @param abandonAudioFocus true: abandon audioFocus. false: no abandon audioFocus.
     */
    public void pausePlayer(boolean abandonAudioFocus) {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause(); //stop playing music
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable); //移除更新播放进度消息
        Notifier.getInstance().showPause(getPlayMusic());
        MediaSessionManager.getInstance().updatePlaybackState();
        context.unregisterReceiver(noisyReceiver);
        //TODO
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus(); //暂停音乐后失去焦点
        }

        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerPause();
        }
    }

    private void setPlayPosition(int position) {
        Preferences.savePlayPosition(position);
    }
}
