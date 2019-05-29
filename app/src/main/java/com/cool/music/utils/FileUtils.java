package com.cool.music.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.cool.music.R;
import com.cool.music.application.AppCache;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    private static final String MP3 = ".mp3";
    private static final String LRC = ".lrc";

    public static String getMp3FileName(String artist, String title) {
        return getFileName(artist, title) + MP3;
    }

    public static String getLrcFileName(String artist, String title) {
        return getFileName(artist, title) + LRC;
    }

    public static String getAlbumFileName(String artist, String title) {
        return getFileName(artist, title);
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        String regEx = "[\\/:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String getFileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = AppCache.getInstance().getContext().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = AppCache.getInstance().getContext().getString(R.string.unknown);
        }
        return artist + " - " + title;
    }

    public static String getLrcFileName(String musicFilePath) {
        return musicFilePath.substring(musicFilePath.lastIndexOf("/") + 1, musicFilePath.lastIndexOf("."))
                .concat(".lrc");
    }

    public static String getAlbumFileName(String musicFilePath) {
        return musicFilePath.substring(musicFilePath.lastIndexOf("/") + 1, musicFilePath.lastIndexOf("."));
    }

    private static String getAppDir() {
        //file path:/mnt/sdcard/CoolMusic
        return Environment.getExternalStorageDirectory() + "/CoolMusic";
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "/Music/";
        return mkdirs(dir);
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "/Lyric/";
        return mkdirs(dir);
    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
                file.mkdirs();
        }
        return dir;
    }

    public static String getAlbumDir() {
        String dir = getAppDir() + "/Album/";
        return mkdirs(dir);
    }

    public static String getRelativeMusicDir() {
        String dir = "CoolMusic/Music/";
        return mkdirs(dir);
    }

    public static String getArtistAndAlbum(String artist, String album) {
        if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return "";
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return artist;
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            return album;
        } else {
            return artist + " - " + album;
        }
    }
}
