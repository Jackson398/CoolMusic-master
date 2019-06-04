package com.cool.music.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cool.music.R;
import com.cool.music.adapter.PlaylistAdapter;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.Music;
import com.cool.music.service.AudioPlayer;
import com.cool.music.service.OnPlayerEventListener;

public class PlaylistActivity extends BaseActivity implements OnPlayerEventListener, AdapterView.OnItemClickListener, OnMoreClickListener {
    private ListView lvPlaylist;
    private PlaylistAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
    }

    private void initView() {
        lvPlaylist = findViewById(R.id.lv_playlist);
    }

    @Override
    protected void onServiceBound() {
        initView();
        adapter = new PlaylistAdapter(AudioPlayer.getInstance().getMusicList());
        adapter.setIsPlaylist(true);
        adapter.setOnMoreClickListener(this);
        lvPlaylist.setAdapter(adapter);
        lvPlaylist.setOnItemClickListener(this);
        AudioPlayer.getInstance().addOnPlayEventListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.getInstance().play(position);
    }

    @Override
    public void onMoreClick(int position) {
        String[] items = new String[]{"移除"};
        Music music = AudioPlayer.getInstance().getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(music.getTitle());
        dialog.setItems(items, (dialog1, which) -> {
            AudioPlayer.getInstance().delete(position);
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    @Override
    public void onChange(Music music) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerStart() {

    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPublish(int progress) {

    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    protected void onDestroy() {
        AudioPlayer.getInstance().removeOnPlayEventListener(this);
        super.onDestroy();
    }
}
