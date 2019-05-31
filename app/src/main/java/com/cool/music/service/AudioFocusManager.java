package com.cool.music.service;


import android.content.Context;
import android.media.AudioManager;

import static android.content.Context.AUDIO_SERVICE;

/**
 * This class is used to handle conflicts between third app play music background and its own play music background.
 * When the phone plays audio in the background, the other software also needs to play audio, at this time,
 * there will be two audio at the same time to play music which cause conflict.
 * After Android 2.2 provide AudioFocus for obtaining audioFocus. Normally music player will handle this focus during its
 * the audio is paused or stopped when it loses focus, and then resumed when it returns to focus again
 **/
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager audioManager;
    private boolean isPausedByFocusLossTransient = false;

    public AudioFocusManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            //重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    AudioPlayer.getInstance().startPlayer();
                }
                // 恢复音量
                AudioPlayer.getInstance().getMediaPlayer().setVolume(1f, 1f);
                isPausedByFocusLossTransient = false;
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                AudioPlayer.getInstance().pausePlayer();
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                AudioPlayer.getInstance().pausePlayer(false);
                isPausedByFocusLossTransient = true;
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                AudioPlayer.getInstance().getMediaPlayer().setVolume(0.5f, 0.5f);
                break;
        }
    }

    /**
     * This method is used to obtain audioFocus.<br>
     * @return {@link android.media.AudioManager#AUDIOFOCUS_REQUEST_GRANTED} obtain audio focus success.
     *         {@link android.media.AudioManager#AUDIOFOCUS_REQUEST_FAILED} obtain audio focus fail.
     */
    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * This method is used to lose audioFocus.<br>
     * It will inform you that app no longer need audio, and remove the related {@link AudioManager.OnAudioFocusChangeListener}
     * registration.If a short tonal focus is released, the interrupted audio continue to play.
     */
    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }
}
