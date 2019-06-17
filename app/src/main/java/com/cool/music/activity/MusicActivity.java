package com.cool.music.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.adapter.FragmentAdapter;
import com.cool.music.constants.Extras;
import com.cool.music.constants.Keys;
import com.cool.music.executor.ControlPanel;
import com.cool.music.executor.NaviMenuExecutor;
import com.cool.music.executor.WeatherExecutor;
import com.cool.music.fragment.LocalMusicFragment;
import com.cool.music.fragment.PlayFragment;
import com.cool.music.fragment.SheetListFragment;
import com.cool.music.service.AudioPlayer;
import com.cool.music.service.QuitTimer;
import com.cool.music.utils.PermissionReq;
import com.cool.music.utils.ToastUtils;


public class MusicActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
         ViewPager.OnPageChangeListener, QuitTimer.OnTimerListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivMenu;
    private ImageView ivSearch;
    private TextView tvLocalMusic;
    private TextView tvOnlineMusic;
    private ViewPager mViewPager;
    private ControlPanel controlPanel;
    private FrameLayout flPlayBar;
    private LocalMusicFragment mLocalMusicFragment;
    private SheetListFragment mSheetListFragment;
    private PlayFragment mPlayFragment;
    private boolean isPlayFragmentShow;
    private NaviMenuExecutor naviMenuExecutor;
    private View vNavigationHeader;

    /**
     * The bundle parameter in the onCreate() method, which can be used to restore the data is different
     * from onRestoreInstanceSate() method. The onCreate() method can be used to restore the activity, because
     * the savedInstanceSate parameter in onCreate() method is the data stored after the execution of
     * savedInstanceState. However, the onCreate() method is executed before savedInstanceState() method,
     * what's more, the savedInstanceState() method may not be executed, so it's necessary to judge null when
     * using onCreate() method to restore. However, if onRestoreInstanceState() method is invoked, the Bundle
     * parameter must not be null, and the parameter can be used directly.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
    }

    private void initViews() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.addHeaderView(vNavigationHeader);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        tvLocalMusic = (TextView) findViewById(R.id.tv_local_music);
        tvOnlineMusic = (TextView) findViewById(R.id.tv_online_music);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        flPlayBar = (FrameLayout) findViewById(R.id.fl_play_bar);

        mLocalMusicFragment = new LocalMusicFragment();
        mSheetListFragment = new SheetListFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(mSheetListFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);

        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
        flPlayBar.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onServiceBound() {
        initViews();
        updateWeather();
        controlPanel = new ControlPanel(flPlayBar);
        naviMenuExecutor = new NaviMenuExecutor(this);
        AudioPlayer.getInstance().addOnPlayEventListener(controlPanel);
        QuitTimer.getInstance().setOnTimerListener(this);
        parseIntent();
    }

    private void updateWeather() {
        PermissionReq.with(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        new WeatherExecutor(MusicActivity.this, vNavigationHeader).execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_location);
                    }
                })
                .request();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        handler.postDelayed(() -> item.setChecked(false), 500); //restored unselected state
        return naviMenuExecutor.onNavigationItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_search:
                startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_online_music:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    @Override
    public void onTimer(long remain) {
        //todo
    }

    /**
     * onRestoreInstanceState(Bundle savedInstanceState) will be invoked when the activity is actually
     * recycled by the system and recreated. At this time, the parameter savedInstanceState must not be null,
     * it is executed after the onStart() method. And the value is not null when read the savedInstanceSate in
     * onRestoreInstanceState() method, but it may be null when the information stored in the Bundle then read
     * read in onCreate().
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mViewPager.post(() -> {
            mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
            mLocalMusicFragment.onRestoreInstanceState(savedInstanceState);
            mSheetListFragment.onRestoreInstanceState(savedInstanceState);
        });
    }

    /**
     * Activity will be destroyed under certain special circumstances, such as out of memory, or not invoke
     * Process.killProcess on the main page, system.exit() and so on. Then system will create activity.
     * onSaveInstanceState(Bundle outState) and onRestoreInstanceState(Bundle savedInstanceState) are
     * used to save and restore data. When system resumes the destroyed activity, it will read the UI
     * state information stored by onSaveInstanceState() method and restore the original UI.<br>
     * The onSaveInstanceState method executes before the activity might be destroyed, saving UI state information
     * that can be used for recovery after destroyed due to the exception. It will be invoked before putting the interface
     * into the background or possibly being destroyed (between onPause and onStop), such as:
     * 1. The screen is turned off. execution sequence: onPause() -> onSavedInstanceState() -> onStop()
     * 2. Start a new activity from the current activity. execution sequence: onPause() -> onSavedInstanceState() -> onStop()
     * 3. Switch screen direction. execution sequence: onPause() -> onSavedInstanceState() -> onStop() -> onDestroy() ->
     *    onCreate() -> onStart() -> onRestoreInstanceState() -> onResume()
     * notice: screen orientation toggle. when configChanges are set in AndroidMainfest.xml, it is sure that it can't be destroyed,
     * so onSavedInstance(Bundle outState) will not be invoked.
     * 4. User pressed the HOME button. execution sequence: onPause() -> onSavedInstanceState() -> onStop()
     * However, in some cases it might not be invoked when it is sure that the activity would be destroyed, such as:
     * 1. User pressed the return key.
     * 2. Invoke finish() method to destroyed activity.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
        mLocalMusicFragment.onSaveInstanceState(outState);
        mSheetListFragment.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        AudioPlayer.getInstance().removeOnPlayEventListener(controlPanel);
        QuitTimer.getInstance().setOnTimerListener(null);
        super.onDestroy();
    }
}
