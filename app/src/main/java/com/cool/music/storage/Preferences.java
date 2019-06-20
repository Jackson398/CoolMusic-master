package com.cool.music.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.cool.music.R;

public class Preferences {

    private static Context sContext;
    private static final String PLAY_POSITION = "play_position";
    private static final String PLAY_MODE = "play_mode";
    private static final String TIMER_VALUE ="timer_value";
    private static final String NIGHT_MODE = "night_mode";
    private static final String ADCODE = "adcode"; //regional code
    private static final String CITYNAME = "city"; //city name

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static String getFilterSize() {
        return getString(sContext.getString(R.string.setting_key_filter_size), "0");
    }

    public static String getFilterTime() {
        return getString(sContext.getString(R.string.setting_key_filter_time), "0");
    }

    public static void saveNightMode(boolean on) {
        saveBoolean(NIGHT_MODE, on);
    }

    public static boolean isNightMode() {
        return getBoolean(NIGHT_MODE, false);
    }

    public static String getAdcode() {
        return getString(ADCODE, "");
    }

    public static void saveAdcode(String value) {
        saveString(ADCODE, value);
    }

    public static String getCityName() {
        return getString(CITYNAME, "");
    }

    public static void saveCityName(String value) {
        saveString(CITYNAME, value);
    }

    public static String getTimerValue() {
        return getString(TIMER_VALUE, "0");
    }

    public static void setTimerValue(String value) {
        saveString(TIMER_VALUE, value);
    }

    public static int getPlayPosition() {
        return getInt(PLAY_POSITION, 0);
    }

    private static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    private static String getString(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static boolean enableMobileNetworkDownload() {
        return getBoolean(sContext.getString(R.string.setting_key_mobile_network_download), false);
    }

    public static boolean enableMobileNetworkPlay() {
        return getBoolean(sContext.getString(R.string.setting_key_mobile_network_play), false);
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static void savePlayMode(int mode) {
        saveInt(PLAY_MODE, mode);
    }

    public static int getPlayMode() {
        return getInt(PLAY_MODE, 0);
    }

    public static void savePlayPosition(int position) {
        saveInt(PLAY_POSITION, position);
    }

    public static void saveMobileNetworkPlay(boolean enable) {
        saveBoolean(sContext.getString(R.string.setting_key_mobile_network_play), enable);
    }

    private static void saveBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    private static void saveInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    private static void saveString(String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }
}
