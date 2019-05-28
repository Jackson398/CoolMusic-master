package com.cool.music.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.cool.music.model.Music;
import com.cool.music.storage.Preferences;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {

    public static List<Music> scanMusic(Context context) {
        List<Music> musicList = new ArrayList<>();

        long filterSize = Long.parseLong(Preferences.getFilterSize()) * 1024;
        long filterTime = Long.parseLong(Preferences.getFilterTime()) * 1000;

        //扫描手机上外存储文件
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION
                },
                MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " + MediaStore.Audio.AudioColumns.DURATION + " >= ?"
                ,
                new String[]{
                        String.valueOf(filterSize),
                        String.valueOf(filterTime)
                },
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return musicList;
        }

        int index = 0;
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
            if ((!SystemUtils.isFlyme() && isMusic == 0) || "raw".equals(album)) { //不是魅族手机并且为0
                continue;
            }

            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

            Music music = new Music();
            music.setSongId(id);
            music.setType(Music.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
            if (++index <= 20) {
                // 只加载前20首的缩略图
                CoverLoader.getInstance().loadThumb(music);
            }
            musicList.add(music);
        }
        cursor.close();

        return musicList;
    }
}
