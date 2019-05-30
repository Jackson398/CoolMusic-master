package com.cool.music.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.adapter.SearchMusicAdapter;
import com.cool.music.enums.LoadStateEnum;
import com.cool.music.http.HttpCallback;
import com.cool.music.http.HttpClient;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.SearchMusic;
import com.cool.music.utils.FileUtils;
import com.cool.music.utils.ViewUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SearchMusicActivity extends BaseActivity implements SearchView.OnQueryTextListener,
        AdapterView.OnItemClickListener, OnMoreClickListener {
    protected Handler handler;
    private ListView lvSearchMusic;
    private LinearLayout llLoading;
    private LinearLayout llLoadFail;
    List<SearchMusic.Song> searchMusicList = new ArrayList<>();
    private SearchMusicAdapter mAdapter = new SearchMusicAdapter(searchMusicList);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        handler = new Handler(Looper.getMainLooper());
    }

    private void initView() {
        lvSearchMusic = findViewById(R.id.lv_search_music_list);
        llLoading = findViewById(R.id.ll_loading);
        llLoadFail = findViewById(R.id.ll_load_fail);
        TextView tvLoadFail = llLoadFail.findViewById(R.id.tv_load_fail_text);
        tvLoadFail.setText(R.string.search_empty);
    }


    @Override
    protected void onServiceBound() {
        initView();
        lvSearchMusic.setAdapter(mAdapter);
        lvSearchMusic.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_music, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded(); //初始是否已经是展开的状态
        searchView.setQueryHint(getString(R.string.search_tips));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true); //设置true后，右边会出现一个箭头按钮。如果用户没有输入，就不会触发提交（submit）事件
        try {
            //下面的方法是通过反射修改android.support.v7.widget.SearchView类的mGoButton的图标（默认是箭头）
            Field field = searchView.getClass().getDeclaredField("mGoButton");
            field.setAccessible(true); //mGoButton是private，需要允许访问
            ImageView mGoButton = (ImageView) field.get(searchView);
            mGoButton.setImageResource(R.mipmap.ic_menu_search);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    //输入完成后，提交时触发的方法，一般情况是点击输入法中的搜索按钮才会触发。
    //return true 表示已经处理了提交请求
    //return false 表示让SearchView处理通过启动任何相关意图提交
    @Override
    public boolean onQueryTextSubmit(String query) {
        ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        searchMusic(query);
        return false;
    }

    //在输入时触发的方法，当字符真正显示到searchView中才触发
    //return false SearchView执行默认操作
    //return true 由监听器处理
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchMusic(String keyword) {
        HttpClient.searchMusic(keyword, new HttpCallback<SearchMusic>() {
            @Override
            public void onSuccess(SearchMusic response) {
                if (response == null || response.getSong() == null) {
                    ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FALIED);
                    return;
                }
                ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESSED);
                searchMusicList.clear();
                searchMusicList.addAll(response.getSong());
                mAdapter.notifyDataSetChanged();
                lvSearchMusic.requestFocus();
                //将第position项item显示在ListView的最上上面。假设一个ListView控件一次只能显示10个item，有20个数据
                //position为1显示的是第二项到第十一项数据，大于10，显示的都是第十一项到最后一项。
                handler.post(() -> lvSearchMusic.setSelection(0));
            }

            @Override
            public void onFail(Exception e) {
                ViewUtils.changeViewState(lvSearchMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FALIED);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onMoreClick(int position) {
        final SearchMusic.Song song = searchMusicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(song.getSongname());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(song.getArtistname(), song.getSongname());
        File file = new File(path);
        int itemsId = file.exists() ? R.array.search_music_dialog_no_download : R.array.search_music_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    share(song);
                    break;
                case 1:// 下载
                    download(song);
                    break;
            }
        });
        dialog.show();
    }

    private void share(SearchMusic.Song song) {
        //使用微信开放平台支持分享到微信朋友圈
    }

    private void download(final SearchMusic.Song song) {

    }
}
