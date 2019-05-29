package com.cool.music.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.rxbus.RxBus;
import com.cool.music.R;
import com.cool.music.adapter.PlaylistAdapter;
import com.cool.music.application.AppCache;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.Music;
import com.cool.music.utils.FileUtils;
import com.cool.music.utils.MusicUtils;
import com.cool.music.utils.PermissionReq;
import com.cool.music.utils.ToastUtils;

import java.io.File;
import java.util.List;

public class LocalMusicFragment extends BaseFragment implements OnMoreClickListener, AdapterView.OnItemClickListener {
    private ListView lvLocalMusic;
    private TextView vSearching;

    private PlaylistAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        initViews(view);
        RxBus.getDefault().subscribe(this, new RxBus.Callback<String>() {
            @Override
            public void onEvent(String s) {
                scanMusic();
            }
        });
        return view;
    }

    private void initViews(View view) {
        lvLocalMusic = (ListView) view.findViewById(R.id.lv_local_music);
        vSearching = (TextView) view.findViewById(R.id.v_searching);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PlaylistAdapter(AppCache.getInstance().getLoclMusicList());
        adapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(adapter);
        if (AppCache.getInstance().getLoclMusicList().isEmpty()) {
            scanMusic();
        }
    }

    @Override
    public void onMoreClick(final int position) {
        Music music = AppCache.getInstance().getLoclMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(music.getTitle());
        dialog.setItems(R.array.local_music_dialog, (dialog1, which) -> {
            switch (which) {
                case 0: //分享
                    shareMusic(music);
                    break;
                case 1: //设为铃声
                    requestSetRingtone(music);
                    break;
                case 2: //删除
                    deleteMusic(music);
                    break;
                case 3: //歌曲信息
                    break;
            }
        });
        dialog.show();
    }

    private void deleteMusic(final Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, (dialog1, which) -> {
            String musicFilePath = music.getPath();
            File musicFile = new File(musicFilePath);
            File lrcFile = new File(FileUtils.getLrcDir()+ File.separator + FileUtils.getLrcFileName(musicFilePath));
            File albumFile = new File(FileUtils.getAlbumDir() + File.separator + FileUtils.getAlbumFileName(musicFilePath));
            if(musicFile.delete() && lrcFile.delete() && albumFile.delete()) {
                AppCache.getInstance().getLoclMusicList().remove(music); //删除本地歌曲后将歌曲信息从缓存中删除
                adapter.notifyDataSetChanged();
                //刷新媒体库
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                getContext().sendBroadcast(intent);
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }

    private void requestSetRingtone(final Music music) {
    }

    private void shareMusic(Music music) {
        //todo
        //使用微信开发平台开发支持微信分享
    }

    public void scanMusic() {
        lvLocalMusic.setVisibility(View.GONE);
        vSearching.setVisibility(View.VISIBLE);
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        new AsyncTask<Void, Void, List<Music>>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }

                            @Override
                            protected void onPostExecute(List<Music> musicList) {
                                AppCache.getInstance().getLoclMusicList().clear();
                                AppCache.getInstance().getLoclMusicList().addAll(musicList);
                                lvLocalMusic.setVisibility(View.VISIBLE);
                                vSearching.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            protected List<Music> doInBackground(Void... voids) {
                                return MusicUtils.scanMusic(getContext());
                            }
                        }.execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                        lvLocalMusic.setVisibility(View.VISIBLE);
                        vSearching.setVisibility(View.GONE);
                    }
                })
                .request();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(this);
    }
}
