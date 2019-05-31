package com.cool.music.executor;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.model.Music;
import com.cool.music.service.AudioPlayer;
import com.cool.music.service.OnPlayerEventListener;
import com.cool.music.utils.CoverLoader;

public class ControlPanel implements View.OnClickListener, OnPlayerEventListener {
    private ImageView ivPlayBarCover;
    private TextView tvPlayBarTitle;
    private TextView tvPlayBarArtist;
    private ImageView ivPlayBarPlay;
    private ImageView ivPlayBarNext;
    private ImageView vPlayBarPlaylist;
    private ProgressBar mProgressBar;

    public ControlPanel(View view) {
        initView(view);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        vPlayBarPlaylist.setOnClickListener(this);
        onChange(AudioPlayer.getInstance().getPlayMusic()); //第一次点击进入app时，调用onChange使用当前播放音乐信息填充视图
    }

    private void initView(View view) {
        ivPlayBarCover = view.findViewById(R.id.iv_play_bar_cover);
        tvPlayBarTitle = view.findViewById(R.id.tv_play_bar_title);
        tvPlayBarArtist = view.findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlay = view.findViewById(R.id.iv_play_bar_play);
        ivPlayBarNext = view.findViewById(R.id.iv_play_bar_next);
        vPlayBarPlaylist = view.findViewById(R.id.v_play_bar_playlist);
        mProgressBar = view.findViewById(R.id.pb_play_bar);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onChange(Music music) {
        if (music == null) {
            return;
        }
        Bitmap cover = CoverLoader.getInstance().loadThumb(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.getInstance().isPlaying() || AudioPlayer.getInstance().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) AudioPlayer.getInstance().getAudioPosition());
    }

    @Override
    public void onPlayerStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }
}
