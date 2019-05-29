package com.cool.music.fragment;


import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.adapter.PlaylistAdapter;
import com.cool.music.application.AppCache;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.Music;
import com.cool.music.utils.MusicUtils;
import com.cool.music.utils.PermissionReq;
import com.cool.music.utils.ToastUtils;

import java.util.List;

public class LocalMusicFragment extends BaseFragment implements OnMoreClickListener, AdapterView.OnItemClickListener {
    private ListView lvLocalMusic;
    private TextView vSearching;

    private PlaylistAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        initViews(view);
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
            scanMusic(null);
        }
    }

    @Override
    public void onMoreClick(int position) {

    }

    public void scanMusic(Object object) {
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


}
