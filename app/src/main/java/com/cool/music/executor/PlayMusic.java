package com.cool.music.executor;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.cool.music.R;
import com.cool.music.model.Music;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.NetworkUtils;

public abstract class PlayMusic implements IExecutor<Music> {
    private Activity mActivity;
    private int mTotalStep;
    protected int mCounter = 0;
    protected Music music; //巧妙的设计，当变量的初始化来自于其他类，将其设置的protected,由子类完成初始化

    public PlayMusic(Activity mActivity, int mTotalStep) {
        this.mActivity = mActivity;
        this.mTotalStep = mTotalStep;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkPlay = Preferences.enableMobileNetworkPlay(); //是否允许移动数据播放音乐
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkPlay) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.play_tips);
            builder.setPositiveButton(R.string.play_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences.saveMobileNetworkPlay(true);
                    getPlayInfoWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            getPlayInfoWrapper();
        }
    }

    private void getPlayInfoWrapper() {
        onPrepare();
        getPlayInfo();
    }

    protected abstract void getPlayInfo();

    protected void checkCounter() {
        mCounter++;
        if (mCounter == mTotalStep) { //准备就绪，执行方法播放音乐
            onExecuteSuccess(music);
        }
    }
}
