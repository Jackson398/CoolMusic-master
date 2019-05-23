package com.test.pulldownrefreshpullupload_master.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PullableListView extends ListView implements Pullable {

    public PullableListView(Context context) {
        this(context, null);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canPullDown() {
        if (getCount() == 0) {
            //没有item时也可以下拉刷新
            return true;
        } else if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0) {
            //滑到ListView顶部
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            //滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null &&
                    getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom()
                    <= getMeasuredHeight())
            return true;
        }
            return false;
    }
}
