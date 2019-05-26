package com.test.pulldownrefreshpullupload_master.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.adapter.OnlineMusicAdapter;
import com.test.pulldownrefreshpullupload_master.constants.Extras;
import com.test.pulldownrefreshpullupload_master.enums.LoadStateEnum;
import com.test.pulldownrefreshpullupload_master.http.HttpCallback;
import com.test.pulldownrefreshpullupload_master.http.HttpClient;
import com.test.pulldownrefreshpullupload_master.listener.PullableListener;
import com.test.pulldownrefreshpullupload_master.model.OnlineMusic;
import com.test.pulldownrefreshpullupload_master.model.OnlineMusicList;
import com.test.pulldownrefreshpullupload_master.model.SheetInfo;
import com.test.pulldownrefreshpullupload_master.pulltorefresh.PullableListView;
import com.test.pulldownrefreshpullupload_master.ui.PullToRefreshLayout;
import com.test.pulldownrefreshpullupload_master.utils.ImageUtils;
import com.test.pulldownrefreshpullupload_master.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class OnlineMusicActivity extends BaseActivity implements PullableListView.OnLoadListener {

    private static final int MUSIC_LIST_SIZE = 20;
    private PullableListView mList;
    private PullToRefreshLayout ptrl;
    private SheetInfo mListInfo;
    private LinearLayout llLoading;
    private LinearLayout llLoadFail;
    private LinearLayout llOnLineMusic;
    private OnlineMusicList mOnlineMusicList;
    private View vHeader;
    private List<OnlineMusic> mMusicList = new ArrayList<>();
    private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);
    private int mOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);
        initListView();
    }

    @Override
    protected void onServiceBound() {
        mListInfo = (SheetInfo) getIntent().getSerializableExtra(Extras.MUSIC_LIST_TYPE);
        setTitle(mListInfo.getTitle());
        onLoad(); //第一次进入调用该方法加载
    }

    private void initListView() {
        ptrl = ((PullToRefreshLayout) findViewById(R.id.pull_down_refresh_pull_up_load_layout));
        mList = (PullableListView) findViewById(R.id.content_view);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llLoadFail = (LinearLayout) findViewById(R.id.ll_load_fail);
        llOnLineMusic = (LinearLayout) findViewById(R.id.ll_online_music);
        vHeader = LayoutInflater.from(this).inflate(R.layout.activity_online_music_list_header, llOnLineMusic);
        ViewUtils.changeViewState(llOnLineMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        ptrl.setOnPullableListener(new PullableListener());
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onLoad() {
        getMusic(mOffset);
    }

    private void initHeader() {
        final ImageView ivHeaderBg = vHeader.findViewById(R.id.iv_header_bg);
        final ImageView ivCover = vHeader.findViewById(R.id.iv_cover);
        TextView tvTitle = vHeader.findViewById(R.id.tv_title);
        TextView tvUpdateDate = vHeader.findViewById(R.id.tv_update_date);
        TextView tvComment = vHeader.findViewById(R.id.tv_comment);
        tvTitle.setText(mOnlineMusicList.getBillboard().getName());
        tvUpdateDate.setText(getString(R.string.recent_update, mOnlineMusicList.getBillboard().getUpdate_date()));
        tvComment.setText(mOnlineMusicList.getBillboard().getComment());
        Glide.with(this)
                .load(mOnlineMusicList.getBillboard().getPic_s640())
                .asBitmap()
                .placeholder(R.mipmap.default_cover)
                .error(R.mipmap.default_cover)
                .override(200, 200)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivCover.setImageBitmap(resource);
                        ivHeaderBg.setImageBitmap(ImageUtils.blur(resource));
                    }
                });
    }

    private void getMusic(final int offset) {
        HttpClient.getSongListInfo(mListInfo.getType(), MUSIC_LIST_SIZE, offset, new HttpCallback<OnlineMusicList>() {
            @Override
            public void onSuccess(OnlineMusicList response) {
                mList.onLoadComplete();
                mOnlineMusicList = response; //从服务器上获取的音乐信息
                if (offset == 0 && response == null) {
                    ViewUtils.changeViewState(llOnLineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FALIED);
                    return;
                } else if (offset == 0) {
                    initHeader(); //初始化头部信息
                    ViewUtils.changeViewState(llOnLineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESSED);
                }
                if (response == null || response.getSong_list() == null || response.getSong_list().size() == 0) {
                    llOnLineMusic.setEnabled(false);
                    return;
                }
                mOffset += MUSIC_LIST_SIZE;
                mMusicList.addAll(response.getSong_list());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                mList.onLoadComplete();
                if (e instanceof RuntimeException) {
                    //歌曲全部加载完成
                    mList.setEnabled(false);
                    return;
                }
                if (offset == 0) {
                    ViewUtils.changeViewState(llOnLineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FALIED);
                } else {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}
