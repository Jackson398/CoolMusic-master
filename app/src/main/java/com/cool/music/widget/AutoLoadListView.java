package com.cool.music.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.cool.music.R;
import com.cool.music.utils.LoggerUtils;

public class AutoLoadListView extends ListView implements AbsListView.OnScrollListener {
    private View vFooter;
    private OnLoadListener mListener;
    private int mFirstVisibleItem = 0;
    private boolean mEnableLoad = true;
    private boolean mIsLoading = false;

    public AutoLoadListView(Context context) {
        super(context);
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        vFooter = LayoutInflater.from(getContext()).inflate(R.layout.auto_load_list_view_footer, null);
        addFooterView(vFooter, null, false);
        setOnScrollListener(this);
        onLoadComplete();
    }

    public void onLoadComplete() {
        LoggerUtils.fmtDebug(getClass(), "load completed");
        mIsLoading = false;
        removeFooterView(vFooter);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mListener = listener;
    }

    public void setEnable(boolean enable) {
        mEnableLoad = enable;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean isPullDown = firstVisibleItem > mFirstVisibleItem;
        if (mEnableLoad && !mIsLoading && isPullDown) {
            int lastVisibleItem = firstVisibleItem + visibleItemCount;
            if (lastVisibleItem >= totalItemCount - 1) {
                onLoad();
            }
        }
    }

    private void onLoad() {
        LoggerUtils.fmtDebug(getClass(), "on load.");
        mIsLoading = true;
        addFooterView(vFooter, null, false);
        if (mListener != null) {
            mListener.onLoad();
        }
    }

    public interface OnLoadListener {
        void onLoad();
    }
}
