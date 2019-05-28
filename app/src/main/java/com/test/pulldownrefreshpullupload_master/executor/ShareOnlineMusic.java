package com.test.pulldownrefreshpullupload_master.executor;

import android.content.Context;
import android.content.Intent;

import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.http.HttpCallback;
import com.test.pulldownrefreshpullupload_master.http.HttpClient;
import com.test.pulldownrefreshpullupload_master.model.DownloadInfo;
import com.test.pulldownrefreshpullupload_master.utils.ToastUtils;

public abstract class ShareOnlineMusic implements IExecutor<Void> {
    private Context mContext;
    private String mTitle;
    private String mSongId;

    public ShareOnlineMusic(Context mContext, String mTitle, String mSongId) {
        this.mContext = mContext;
        this.mTitle = mTitle;
        this.mSongId = mSongId;
    }

    @Override
    public void execute() {
        onPrepare();
        share();
    }

    private void share() {
        HttpClient.getMusicDownloadInfo(mSongId, new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) {
                if (response == null) {
                    onFail(null);
                    return;
                }
                onExecuteSuccess(null);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_music, mContext.getString(R.string.app_name),
                        mTitle, response.getBitrate().getFile_link()));
                mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share)));
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
                ToastUtils.show(R.string.unable_to_share);
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
