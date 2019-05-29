package com.cool.music.ui;

import android.content.Context;
import android.os.AsyncTask;
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

import com.cool.music.R;
import com.cool.music.widget.Pullable;

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

    private OnPullableListener mListener; // 刷新回调接口

    public static final int SUCCEED = 0; // 刷新成功
    public static final int FAILED = 1; // 刷新失败

    private float downY, lastY; // 按下Y坐标，上一个事件点Y坐标

    private float pullDownY = 0; // 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
    private float pullUpY = 0; // 上拉的距离

    private float refreshDist = 200; // 释放刷新的距离
    private float loadMoreDist = 200; // 释放加载的距离

    public float MOVE_SPEED = 8; // 回滚速度

    private MyTimer timer;

    private boolean isLayout = false; // 第一次执行布局
    private boolean isTouch = false; // 在刷新过程中滑动操作
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
            //回弹距离随下拉moveDeltaY增大而增大
            MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() *
                    (pullDownY + Math.abs(pullUpY))));
            if (!isTouch) {
                //正在刷新，没有往上推则悬停，显示“正在刷新”
                if (state == REFRESHING && pullDownY <= refreshDist) {
                    pullDownY = refreshDist;
                    timer.cancel();
                } else if (state == LOADING && -pullUpY <= loadMoreDist) {
                    pullUpY = -loadMoreDist;
                    timer.cancel();
                }
            }
            if (pullDownY > 0) {
                pullDownY -= MOVE_SPEED;
            } else if (pullUpY < 0) {
                pullUpY += MOVE_SPEED;
            }
            if (pullDownY < 0) {
                //已经完成回弹
                pullDownY = 0;
                pullView.clearAnimation();
                //隐藏下拉头时可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != REFRESHING && state != LOADING) {
                    changeState(INIT);
                }
                timer.cancel();
                requestLayout();
            }
            if (pullUpY > 0) {
                //已经完成回弹
                pullUpY = 0;
                pullUpView.clearAnimation();
                //隐藏上拉头饰可能还在刷新，只有当前状态不是正在刷新时才该改变状态
                if (state != REFRESHING && state != LOADING) {
                    changeState(INIT);
                }
                timer.cancel();
                requestLayout();
            }
            //刷新布局，会自动调用onLayout()
            requestLayout();
            if (pullDownY + Math.abs(pullUpY) == 0) {
                timer.cancel();
            }
        }
    };

    public PullToRefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
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

    public void autoRefresh() {
        AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
        task.execute(20);
    }

    private class AutoRefreshAndLoadTask extends AsyncTask<Integer, Float, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            changeState(REFRESHING);
            if (mListener != null) {
                mListener.onRefresh(PullToRefreshLayout.this);
            }
            hide();
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            if (pullDownY > refreshDist) {
                changeState(RELEASE_TO_REFRESH);
            }
            requestLayout();
        }

        @Override
        protected String doInBackground(Integer... params) {
            while (pullDownY < 4 / 3 * refreshDist) {
                pullDownY += MOVE_SPEED;
                publishProgress(pullDownY);
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                lastY = downY;
                timer.cancel();
                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1; // 过滤多点触碰
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    if (pullDownY > 0 || (((Pullable) pullableView).canPullDown() && canPullDown && state != LOADING)) {
                        //可以下拉，正在加载时不能下拉，对实际滑动距离做缩小，造成用力拉的感觉
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY < 0) {
                            pullDownY = 0;
                            canPullDown = false;
                            canPullUp = true;
                        }
                        if (pullDownY > getMeasuredHeight()) {
                            pullDownY = getMeasuredHeight();
                        }
                        if (state == REFRESHING) {
                            //正在刷新时触摸移动
                            isTouch = true;
                        }
                    } else if (pullUpY < 0 || (((Pullable)pullableView).canPullUp() && canPullUp && state != REFRESHING)) {
                        //可以上拉，正在刷新时不能上拉
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY > 0) {
                            pullUpY = 0;
                            canPullDown = true;
                            canPullUp = false;
                        }
                        if (pullUpY < -getMeasuredHeight()) {
                            pullUpY = -getMeasuredHeight();
                        }
                        if (state == LOADING) {
                            //正在加载时触摸移动
                            isTouch = true;
                        }
                    } else {
                        releasePull();
                    }
                } else {
                    mEvents = 0;
                }
                    lastY = ev.getY();
                    //根据下拉距离改变比例
                    radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                             * (pullDownY + Math.abs(pullUpY))));
                    if (pullDownY > 0 || pullUpY < 0) {
                        requestLayout();
                    }
                    if (pullDownY > 0) {
                        if (pullDownY <= refreshDist && (state == RELEASE_TO_REFRESH || state == DONE)) {
                            //若下拉距离没有达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                            changeState(INIT);
                        }
                        if (pullDownY >= refreshDist && state == INIT) {
                            //若下拉距离达到刷新距离且当前状态是初始状态刷新，改变状态为释放刷新
                            changeState(RELEASE_TO_REFRESH);
                        }
                    } else if (pullUpY < 0) {
                        if (-pullUpY <= loadMoreDist && (state == RELEASE_TO_LOAD || state == DONE)) {
                            changeState(INIT);
                        }
                        //上拉操作
                        if (-pullUpY >= loadMoreDist && state == INIT) {
                            changeState(RELEASE_TO_LOAD);
                        }
                    }
                    //刷新和加载不能同时进行，所以pullDownY和pullUpY不能同时为0，这里用(pullDownY +
                    //Math.abs(pullUpY))就可以不对当前状态做区分了
                    if ((pullDownY + Math.abs(pullUpY)) > 8) {
                        //防止下拉过程中误触发长按事件和点击事件
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                break;
            case MotionEvent.ACTION_UP:
                if (pullDownY > refreshDist || -pullUpY > loadMoreDist) {
                    //正在刷新时往下拉(正在加载时往拉),释放后下拉箭头(上拉箭头)不隐藏
                    isTouch = false;
                }
                if (state == RELEASE_TO_REFRESH) {
                    changeState(REFRESHING);
                    //刷新
                    if (mListener != null) {
                        mListener.onRefresh(this);
                    }
                } else if (state == RELEASE_TO_LOAD) {
                    changeState(LOADING);
                    //加载
                    if (mListener != null) {
                        mListener.onLoadMore(this);
                    }
                }
                hide();
                break;
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void releasePull() {
        canPullDown = true;
        canPullUp = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isLayout) {
            refreshView = getChildAt(0);
            pullableView = getChildAt(1);
            loadMoreView = getChildAt(2);
            isLayout = true;
            initView();
            refreshDist = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
            loadMoreDist = ((ViewGroup) loadMoreView).getChildAt(0).getMeasuredHeight();
        }

        refreshView.layout(0, (int)(pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
                refreshView.getMeasuredWidth(), (int)(pullDownY + pullUpY));
        pullableView.layout(0, (int)(pullDownY + pullUpY), pullableView.getMeasuredWidth(),
                (int)(pullDownY + pullUpY) + pullableView.getMeasuredHeight());
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

    //加载完毕，显示加载结果
    public void loadMoreFinish(int result) {
        loadingView.clearAnimation();
        loadMoreView.setVisibility(View.GONE);
        switch (result) {
            case SUCCEED:
                loadStateImageView.setVisibility(View.VISIBLE);
                loadStateTextView.setText(R.string.load_succeed);
                loadStateImageView.setBackgroundResource(R.drawable.load_succeed);
                break;
            case FAILED:
                loadStateImageView.setVisibility(View.VISIBLE);
                loadStateTextView.setText(R.string.load_failed);
                loadStateImageView.setBackgroundResource(R.drawable.load_failed);
                break;
                default:
                    break;
        }
        if (pullUpY < 0) {
            //刷新结果停留1s
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

    public void refreshFinish(int result) {
        refreshingView.clearAnimation();
        refreshingView.setVisibility(View.GONE);
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
            case RELEASE_TO_REFRESH:
                //释放刷新状态
                refreshStateTextView.setText(R.string.release_to_refresh);
                pullView.startAnimation(rotateAnimation);
                break;
            case REFRESHING:
                pullView.clearAnimation();
                refreshingView.setVisibility(View.VISIBLE);
                pullView.setVisibility(View.INVISIBLE);
                refreshingView.startAnimation(refreshingAnimation);
                refreshStateTextView.setText(R.string.refreshing);
                break;
            case RELEASE_TO_LOAD:
                loadStateTextView.setText(R.string.release_to_refresh);
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

    public void setOnPullableListener(OnPullableListener listener) {
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
    public interface OnPullableListener {
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
