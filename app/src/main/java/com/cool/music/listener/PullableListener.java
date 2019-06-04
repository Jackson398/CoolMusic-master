package com.cool.music.listener;

import android.os.Handler;
import android.os.Message;

import com.cool.music.ui.PullToRefreshLayout;

//设计有问题，需要改进
public class PullableListener implements PullToRefreshLayout.OnPullableListener {

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                refreshPullableList();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                loadPullableList();
                pullToRefreshLayout.loadMoreFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 3000);
    }

    protected void loadPullableList() {
    }
    protected void refreshPullableList(){
    }
}
