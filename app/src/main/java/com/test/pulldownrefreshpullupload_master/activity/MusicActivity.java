package com.test.pulldownrefreshpullupload_master.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.adapter.FragmentAdapter;
import com.test.pulldownrefreshpullupload_master.fragment.LocalMusicFragment;
import com.test.pulldownrefreshpullupload_master.fragment.SheetListFragment;


public class MusicActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
         ViewPager.OnPageChangeListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivMenu;
    private ImageView ivSearch;
    private TextView tvLocalMusic;
    private TextView tvOnlineMusic;
    private ViewPager mViewPager;
    private LocalMusicFragment mLocalMusicFragment;
    private SheetListFragment mSheetListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initViews();
    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        tvLocalMusic = (TextView) findViewById(R.id.tv_local_music);
        tvOnlineMusic = (TextView) findViewById(R.id.tv_online_music);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

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
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_search:
                break;
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_online_music:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
