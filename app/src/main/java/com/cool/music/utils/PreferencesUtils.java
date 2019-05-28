package com.cool.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cool.music.R;

public class PreferencesUtils {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static boolean enableMobileNetworkDownload() {
        return getBoolean(sContext.getString(R.string.setting_key_mobile_network_download), false);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
