package com.test.pulldownrefreshpullupload_master.activity;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
    }

    private void bindService() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
