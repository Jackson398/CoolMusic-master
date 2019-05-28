package com.cool.music.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.cool.music.R;

public class Preferences {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static String getFilterSize() {
        return getString(sContext.getString(R.string.setting_key_filter_size), "0");
    }

    public static String getFilterTime() {
        return getString(sContext.getString(R.string.setting_key_filter_time), "0");
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
