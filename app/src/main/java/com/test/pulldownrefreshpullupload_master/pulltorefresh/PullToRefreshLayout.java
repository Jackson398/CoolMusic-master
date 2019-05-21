package com.test.pulldownrefreshpullupload_master.pulltorefresh;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A custom layout for managing three child widgets, one for pullableView contains content
 */
public class PullToRefreshLayout extends RelativeLayout {

    private static final String TAG = "PullToRefreshLayout";

    public static final int INIT = 0; // 初始状态
    public static final int RELEASE_TO_REFRESH = 1; // 释放刷新
    public static final int REFRESHING = 2; // 正在刷新
    public static final int RELEASE_TO_LOAD = 3; // 释放加载
    public static final int LOADING = 4; // 正在加载
    public static final int DONE = 5; // 操作完毕

    private int state = INIT; // 当前状态

    private OnRefreshListener mListener; // 刷新回调接口

    public static final int SUCCEED = 0; // 刷新成功
    public static final int FAILED = 1; // 刷新失败

    private float downY, lastY; // 按下Y坐标，上一个事件点Y坐标

    private float pullDownY = 0; // 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
    private float pullUpY = 0; // 上拉的距离

    private float refreshDist = 200; // 释放刷新的距离
    private float loadMoreDist = 200; // 释放加载的距离

    public float MOVE_SPEED = 8; // 回滚速度

    private MyTimer timer;

    private boolean isFirstLayout = false; // 第一次执行布局
    private boolean isFirstTouch = false; // 在刷新过程中滑动操作
    private float radio = 2; // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化

    private RotateAnimation rotateAnimation; // 下拉箭头的转180°动画
    private RotateAnimation refreshingAnimation; // 均匀旋转动画

    private View refreshView; // 下拉头
    private View pullView; // 下拉的箭头
    private View refreshingView; // 正在刷新的图标
    private View refreshStateImageView; // 刷新结果图标
    private View refreshStateTextView; // 刷新结果：成功或失败

    private View loadMoreView; // 上拉头
    private View pullUpView; // 上拉的箭头
    private View loadingView; // 正在加载的图标
    private View loadStateImageView; // 加载结果图标
    private TextView loadStateTextView; // 加载结果：成功或失败

    private View pullableView;

    private int mEvents; // 过滤多点触碰

    private boolean canPullDown = true; // 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
    private boolean canPullUp = true;

    private Context mContext;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }
    }

    class MyTask extends TimerTask {
        private Handler handler;

        public MyTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.obtainMessage().sendToTarget();
        }
    }

    /**
     * Refresh an load callback interface
     */
    public interface OnRefreshListener {
        /**
         * Invoked method when refresh something.
         * @param pullToRefreshLayout
         */
        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

        /**
         * Invoked method whe load something.
         * @param pullToRefreshLayout
         */
        void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
    }
}
