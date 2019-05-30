package com.cool.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.constants.Extras;
import com.cool.music.model.Music;

public class MusicInfoActivity extends BaseActivity implements View.OnClickListener {
    private EditText etTitle;
    private EditText etArtist;
    private EditText etAlbum;
    private TextView tvFileSize;
    private TextView tvDuration;
    private Button btMusicLrc;
    private TextView tvFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);
    }

    @Override
    protected void onServiceBound() {
        super.onServiceBound();
        initView();
    }

    public static void start(Context context, Music music) {
        Intent intent = new Intent(context, MusicInfoActivity.class);
        intent.putExtra(Extras.MUSIC, music);
        context.startActivity(intent);
    }

    private void initView() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
