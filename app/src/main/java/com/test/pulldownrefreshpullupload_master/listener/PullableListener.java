package com.test.pulldownrefreshpullupload_master.listener;

import android.os.Handler;
import android.os.Message;

import com.test.pulldownrefreshpullupload_master.ui.PullToRefreshLayout;

public class PullableListener implements PullToRefreshLayout.OnPullableListener {

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 5000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pullToRefreshLayout.loadMoreFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 5000);
    }
}
