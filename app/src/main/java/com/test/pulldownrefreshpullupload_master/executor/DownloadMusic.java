package com.test.pulldownrefreshpullupload_master.executor;

import android.app.Activity;
import android.app.Dialog;

import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.utils.NetworkUtils;
import com.test.pulldownrefreshpullupload_master.utils.PreferencesUtils;

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
        boolean mobileNetworkDownload = PreferencesUtils.enableMobileNetworkDownload();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkDownload) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
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
