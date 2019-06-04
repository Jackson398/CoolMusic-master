package com.cool.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cool.music.R;
import com.cool.music.activity.OnlineMusicActivity;
import com.cool.music.adapter.SheetAdapter;
import com.cool.music.application.AppCache;
import com.cool.music.constants.Extras;
import com.cool.music.constants.Keys;
import com.cool.music.model.SheetInfo;

import java.util.List;


public class SheetListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

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

        mSongLists = AppCache.getInstance().getShellList();
        if (mSongLists.isEmpty()) {
            String[] titles = getResources().getStringArray(R.array.online_music_list_title);
            String[] types = getResources().getStringArray(R.array.online_music_list_type);
            for (int i = 0; i < titles.length; i++) {
                SheetInfo info = new SheetInfo();
                info.setTitle(titles[i]);
                info.setType(types[i]);
                mSongLists.add(info);
            }
        }
        SheetAdapter adapter = new SheetAdapter(mSongLists);
        lvPlaylist.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SheetInfo sheetInfo = mSongLists.get(position);
        Intent intent = new Intent(getContext(), OnlineMusicActivity.class);
        intent.putExtra(Extras.MUSIC_LIST_TYPE, sheetInfo);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void setListener() {
        lvPlaylist.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        int position = lvPlaylist.getFirstVisiblePosition();
        int offset = (lvPlaylist.getChildAt(0) == null) ? 0 : lvPlaylist.getChildAt(0).getTop();
        outState.putInt(Keys.PLAYLIST_POSITION, position);
        outState.putInt(Keys.PLAYLIST_OFFSET, offset);
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        lvPlaylist.post(() -> {
            int position = savedInstanceState.getInt(Keys.PLAYLIST_POSITION);
            int offset = savedInstanceState.getInt(Keys.PLAYLIST_OFFSET);
            lvPlaylist.setSelectionFromTop(position, offset);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
