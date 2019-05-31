package com.cool.music.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cool.music.R;
import com.cool.music.adapter.OnlineMusicAdapter;
import com.cool.music.constants.Extras;
import com.cool.music.enums.LoadStateEnum;
import com.cool.music.executor.DownloadOnlineMusic;
import com.cool.music.executor.PlayOnlineMusic;
import com.cool.music.executor.ShareOnlineMusic;
import com.cool.music.http.HttpCallback;
import com.cool.music.http.HttpClient;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.listener.PullableListener;
import com.cool.music.model.Music;
import com.cool.music.model.OnlineMusic;
import com.cool.music.model.OnlineMusicList;
import com.cool.music.model.SheetInfo;
import com.cool.music.service.AudioPlayer;
import com.cool.music.widget.PullableListView;
import com.cool.music.ui.PullToRefreshLayout;
import com.cool.music.utils.FileUtils;
import com.cool.music.utils.ImageUtils;
import com.cool.music.utils.PermissionReq;
import com.cool.music.utils.ToastUtils;
import com.cool.music.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OnlineMusicActivity extends BaseActivity implements PullableListView.OnLoadListener, OnMoreClickListener, AdapterView.OnItemClickListener {

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
        ptrl.setOnPullableListener(new PullableListener() {
            @Override
            protected void loadPullableList() {
                getMusic(mOffset);
            }

            @Override
            protected void refreshPullableList() {
                mMusicList.clear();
                getMusic(mOffset);
            }
        });
        mList.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);
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



    @Override
    public void onMoreClick(int position) {
        final OnlineMusic onlineMusic = mMusicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(mMusicList.get(position).getTitle());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(onlineMusic.getArtist_name(), onlineMusic.getTitle());
        File file = new File(path);
        int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    share(onlineMusic);
                    break;
                case 1:// 查看歌手信息
                    artistInfo(onlineMusic);
                    break;
                case 2:// 下载
                    downloadOnlineMusic(onlineMusic);
                    break;
            }
        });

//        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:// 分享
//                        share(onlineMusic);
//                        break;
//                    case 1:// 查看歌手信息
//                        artistInfo(onlineMusic);
//                        break;
//                    case 2:// 下载
//                        download(onlineMusic);
//                        break;
//                }
//            }
//        });
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        play((OnlineMusic) parent.getAdapter().getItem(position));
    }

    private void play(OnlineMusic onlineMusic) {
        new PlayOnlineMusic(this, onlineMusic) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Music music) {
                cancelProgress();
                AudioPlayer.getInstance().addAndPlay(music);
                ToastUtils.show("已添加到播放列表");
            }

            @Override
            public void onExecuteFail(Exception e) {

            }
        }.execute();
    }

    //静态申请的权限可能无效，需要动态申请
    private void downloadOnlineMusic(OnlineMusic onlineMusic) {
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        download(onlineMusic);
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_download);
                    }
                })
                .request();

    }

    private void share(final OnlineMusic onlineMusic) {
        new ShareOnlineMusic(this, onlineMusic.getTitle(), onlineMusic.getSong_id()) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
            }
        }.execute();
    }

    private void artistInfo(OnlineMusic onlineMusic) {
        ArtistInfoActivity.start(this, onlineMusic.getTing_uid());
    }

    private void download(final OnlineMusic onlineMusic) {
        new DownloadOnlineMusic(this, onlineMusic) {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
                ToastUtils.show(getString(R.string.now_download, onlineMusic.getTitle()));
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(R.string.unable_to_download);
            }
        }.execute();
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
