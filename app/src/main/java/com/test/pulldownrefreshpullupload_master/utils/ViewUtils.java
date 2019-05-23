package com.test.pulldownrefreshpullupload_master.utils;

import android.view.View;

import com.test.pulldownrefreshpullupload_master.enums.LoadStateEnum;

public class ViewUtils {

    public static void changeViewState(View success, View loading, View failed, LoadStateEnum state) {
        switch (state) {
            case LOADING:
                success.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                failed.setVisibility(View.GONE);
                break;
            case LOAD_SUCCESSED:
                success.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                failed.setVisibility(View.GONE);
                break;
            case LOAD_FALIED:
                success.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                failed.setVisibility(View.VISIBLE);
                break;
        }
    }
}
