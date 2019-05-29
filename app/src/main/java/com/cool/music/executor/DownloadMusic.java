package com.cool.music.executor;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;

import com.cool.music.R;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.NetworkUtils;

public abstract class DownloadMusic implements IExecutor<Void> {
    private Activity mActivity;

    public DownloadMusic(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        //将是否允许移动网络下载音乐存储到sharePreferences中
        boolean mobileNetworkDownload = Preferences.enableMobileNetworkDownload();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.download_tips);
            builder.setPositiveButton(R.string.download_tips_sure, (dialog, which) -> downloadWrapper());
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            downloadWrapper();
        }
    }

    private void downloadWrapper() {
        onPrepare();
        download();
    }

    protected abstract void download();
}
