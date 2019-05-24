package com.test.pulldownrefreshpullupload_master.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.test.pulldownrefreshpullupload_master.R;

public class LocalMusicFragment extends BaseFragment {
    private ListView lvLocalMusic;
    private TextView vSearching;

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
    }

    @Override
    protected void setListener() {
        super.setListener();
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
