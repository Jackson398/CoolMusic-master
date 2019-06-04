package com.cool.music.service;

import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.cool.music.application.AppCache;
import com.cool.music.model.Music;
import com.cool.music.utils.CoverLoader;

/**
 * @see {@link android.media.session.MediaSessionManager}
 */
public class MediaSessionManager {
    private static final String TAG = "MediaSessionManager";
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private PlayService playService;
    private volatile static MediaSessionManager instance;
    private MediaSessionCompat mediaSession;

    private MediaSessionManager() {
    }

    public static MediaSessionManager getInstance() {
        if (instance == null) {
            synchronized (MediaSessionManager.class) {
                if (instance == null) {
                    instance = new MediaSessionManager();
                }
            }
        }
        return instance;
    }

    public void init(PlayService playService) {
        this.playService = playService;
        setupMediaSession();
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(playService, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setCallback(callback);
        mediaSession.setActive(true);
    }

    public void updateMetaData(Music music) {
        if (music == null) {
            mediaSession.setMetadata(null);
            return;
        }

        MediaMetadataCompat.Builder metaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, CoverLoader.getInstance().loadThumb(music));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppCache.getInstance().getLocalMusicList().size());
        }

        mediaSession.setMetadata(metaData.build());
    }

    public void updatePlaybackState() {
        int state = (AudioPlayer.getInstance().isPlaying() || AudioPlayer.getInstance().isPreparing()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, AudioPlayer.getInstance().getAudioPosition(), 1)
                        .build());
    }

    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
//        @Override
//        public void onPlay() {
//            AudioPlayer.getInstance().playPause();
//        }
//
//        @Override
//        public void onPause() {
//            AudioPlayer.getInstance().playPause();
//        }
//
//        @Override
//        public void onSkipToNext() {
//            AudioPlayer.getInstance().next();
//        }
//
//        @Override
//        public void onSkipToPrevious() {
//            AudioPlayer.getInstance().prev();
//        }
//
//        @Override
//        public void onStop() {
//            AudioPlayer.getInstance().stopPlayer();
//        }
//
//        @Override
//        public void onSeekTo(long pos) {
//            AudioPlayer.getInstance().seekTo((int) pos);
//        }
    };
}
