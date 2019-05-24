package com.test.pulldownrefreshpullupload_master.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.model.SheetInfo;

import java.util.List;

public class SheetListFragment extends BaseFragment {

    private ListView lvPlaylist;
    private List<SheetInfo> mSongLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheet_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        lvPlaylist = (ListView) view.findViewById(R.id.lv_sheet);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
