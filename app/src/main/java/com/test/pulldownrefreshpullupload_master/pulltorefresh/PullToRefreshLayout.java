package com.test.pulldownrefreshpullupload_master.pulltorefresh;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.pulldownrefreshpullupload_master.R;

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
    private TextView refreshStateTextView; // 刷新结果：成功或失败

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

    //执行自动回滚的handler
    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

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
        timer = new MyTimer(updateHandler);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.reverse_anim);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotating);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        refreshingAnimation.setInterpolator(new LinearInterpolator());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isFirstLayout) {
            refreshingView = getChildAt(0);
            pullableView = getChildAt(1);
            loadMoreView = getChildAt(2);
            isFirstLayout = true;
            initView();
            refreshDist = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
            loadMoreDist = ((ViewGroup) loadMoreView).getChildAt(0).getMeasuredHeight();
        }

        refreshView.layout(0, (int)(pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
                refreshView.getMeasuredWidth(), (int)(pullDownY + pullUpY));
        pullableView.layout(0, (int)(pullDownY + pullUpY), pullableView.getMeasuredWidth(),
                (int)(pullDownY + pullUpY));
        loadMoreView.layout(0, (int)(pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
                loadMoreView.getMeasuredWidth(), (int)(pullDownY + pullUpY)
                        + pullableView.getMeasuredHeight() + loadMoreView.getMeasuredHeight());
    }

    private void initView() {

        pullView = refreshView.findViewById(R.id.pull_icon);
        refreshStateTextView = refreshView.findViewById(R.id.state_tv);
        refreshingView = refreshView.findViewById(R.id.refreshing_icon);
        refreshStateImageView = refreshView.findViewById(R.id.state_iv);

        pullUpView = loadMoreView.findViewById(R.id.pull_up_icon);
        loadStateTextView = loadMoreView.findViewById(R.id.load_state_tv);
        loadingView = loadMoreView.findViewById(R.id.loading_icon);
        loadStateImageView = loadMoreView.findViewById(R.id.load_state_iv);
    }

    public void refreshFinish(int result) {
        refreshView.clearAnimation();
        refreshView.setVisibility(View.GONE);
        switch (result) {
            case SUCCEED:
                refreshStateImageView.setVisibility(View.VISIBLE);
                refreshStateTextView.setText(R.string.refresh_succeed);
                refreshStateImageView.setBackgroundResource(R.drawable.refresh_succeed);
                break;
            case FAILED:
                refreshStateImageView.setVisibility(View.VISIBLE);
                refreshStateTextView.setText(R.string.refresh_failed);
                refreshStateImageView.setBackgroundResource(R.drawable.refresh_failed);
                break;
            default:
        }

        if (pullDownY > 0) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    changeState(DONE);
                    hide();
                }
            }.sendEmptyMessageDelayed(0, 1000);
        } else {
            changeState(DONE);
            hide();
        }
    }

    private void hide() {
        timer.schedule(5);
    }

    private void changeState(int to) {
        state = to;
        switch (state) {
            case INIT:
                refreshStateImageView.setVisibility(View.GONE);
                refreshStateTextView.setText(R.string.pull_to_refresh);
                pullView.clearAnimation();
                pullView.setVisibility(View.VISIBLE);

                loadStateImageView.setVisibility(View.GONE);
                loadStateTextView.setText(R.string.pull_up_to_load);
                pullUpView.clearAnimation();
                pullUpView.setVisibility(View.VISIBLE);
                break;
            case REFRESHING:
                pullView.clearAnimation();
                refreshingView.setVisibility(View.VISIBLE);
                pullView.setVisibility(View.INVISIBLE);
                refreshingView.startAnimation(refreshingAnimation);
                refreshStateTextView.setText(R.string.refreshing);
                break;
            case RELEASE_TO_LOAD:
                refreshStateTextView.setText(R.string.release_to_refresh);
                pullView.startAnimation(rotateAnimation);
                break;
            case LOADING:
                pullUpView.clearAnimation();
                loadingView.setVisibility(View.VISIBLE);
                pullUpView.setVisibility(View.INVISIBLE);
                loadingView.startAnimation(refreshingAnimation);
                loadStateTextView.setText(R.string.loading);
                break;
            case DONE:
                break;
        }
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
