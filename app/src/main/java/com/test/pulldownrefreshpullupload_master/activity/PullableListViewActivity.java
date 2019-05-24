package com.test.pulldownrefreshpullupload_master.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.test.pulldownrefreshpullupload_master.adapter.OnlineMusicAdapter;
import com.test.pulldownrefreshpullupload_master.constants.Extras;
import com.test.pulldownrefreshpullupload_master.enums.LoadStateEnum;
import com.test.pulldownrefreshpullupload_master.http.HttpCallback;
import com.test.pulldownrefreshpullupload_master.http.HttpClient;
import com.test.pulldownrefreshpullupload_master.listener.PullableListener;
import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.model.OnlineMusic;
import com.test.pulldownrefreshpullupload_master.model.OnlineMusicList;
import com.test.pulldownrefreshpullupload_master.model.SheetInfo;
import com.test.pulldownrefreshpullupload_master.pulltorefresh.PullableListView;
import com.test.pulldownrefreshpullupload_master.ui.PullToRefreshLayout;
import com.test.pulldownrefreshpullupload_master.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class PullableListViewActivity extends BaseActivity implements PullableListView.OnLoadListener{

    private static final int MUSIC_LIST_SIZE = 20;
    private PullableListView mList;
    private PullToRefreshLayout ptrl;
    private boolean isFirstIn = true; //第一次进入时需要自动刷新
    private LinearLayout llLoading;
    private LinearLayout llLoadFail;
    private SheetInfo mListInfo;
    private OnlineMusicList mOnlineMusicList;
    private List<OnlineMusic> mMusicList = new ArrayList<>();
    private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);
    private int mOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ptrl = ((PullToRefreshLayout) findViewById(R.id.pull_down_refresh_pull_up_load_layout));
        ptrl.setOnRefreshListener(new PullableListener());
        mList = (PullableListView) findViewById(R.id.content_view);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llLoadFail = (LinearLayout) findViewById(R.id.ll_load_fail);
        initListView();
    }

    private void initListView() {
        mList.setAdapter(mAdapter);
        ViewUtils.changeViewState(mList, llLoading, llLoadFail, LoadStateEnum.LOADING);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onLoad() {
        getMusic(mOffset);
    }

    private void getMusic(final int offset) {
        HttpClient.getSongListInfo(mListInfo.getType(), MUSIC_LIST_SIZE, offset, new HttpCallback<OnlineMusicList>() {
            @Override
            public void onSuccess(OnlineMusicList response) {
                mList.onLoadComplete();
                mOnlineMusicList = response; //从服务器上获取的音乐信息
                if (offset == 0 && response == null) {

                } else if (offset == 0) {

                }
                if (response == null || response.getSong_list() == null || response.getSong_list().size() == 0) {

                }
                mOffset += MUSIC_LIST_SIZE;
                mMusicList.addAll(response.getSong_list());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                mList.onLoadComplete();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}
