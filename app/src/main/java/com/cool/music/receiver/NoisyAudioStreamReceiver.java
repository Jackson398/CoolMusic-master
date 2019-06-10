package com.cool.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.cool.music.service.AudioPlayer;

/**
 *  The broadcast receiver used to pause the music play when incoming telegram and
 *  earphone is pulled out.<br>
 *
 *  The receiver was register in {@link AudioPlayer#startPlayer()} and unregister in
 *  {@link AudioPlayer#pausePlayer()}. And the broadcast intent is
 *  {@link AudioManager#ACTION_AUDIO_BECOMING_NOISY} , which is used to prompt application
 *  audio signals to become "noisy" due to changes in audio output. Unplug a wired earphone
 *  or disconnect an audio receiver that support A2DP, and the intent is sent, the system automatically
 *  switches the audio wire to the speaker. After receiving the intent, the application controlling the
 *  audio stream considers pausing, reducing the volume, or other measures to avoid surprising the user
 *  with the speaker sound.
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            AudioPlayer.getInstance().pausePlayer();
        }
    }
}


















