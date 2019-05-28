package com.cool.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.cool.music.R;
import com.cool.music.model.Music;

import java.util.HashMap;
import java.util.Map;


/**
 * 专辑封面加载器
 */
public class CoverLoader {
    public static final int THUMBNAIL_MAX_LENGTH = 500; //缩略图最大宽度
    private static final String KEY_NULL = "null";

    private Context context;
    private Map<Type, LruCache<String, Bitmap>> cacheMap;
    private int roundLength = ScreenUtils.getScreenWidth() / 2;
    private static CoverLoader instance;

    private enum Type {
        THUMB,
        ROUND,
        BLUR
    }

    public CoverLoader() {
    }

    public static CoverLoader getInstance() {
        if (instance == null) {
            synchronized (CoverLoader.class) {
                if (instance == null) {
                    instance = new CoverLoader();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();

        //获取当前进程的可用内存（单位KB）
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //缓存大小为当前进程可用内存的1/8
        int cacheSize = maxMemory / 8;
        /**
         * LruCache缓存原理，LRU，即Least Recently Used.最近最少使用。当缓存空间满了，将最近最少使用的数据从
         * 缓存空间中删除以增加可用的缓存空间用于缓存新数据。
         * 该算法内部有一个缓存列表，当一个缓存数据被访问时，这个数据就会被提到列表尾部。如此以来，列表头部数据就
         * 是最近最不常使用的，当缓存空间不足时，就会删除列表头部的缓存数据。
         */
        LruCache<String, Bitmap> thumbCache = new LruCache<String, Bitmap>(cacheSize) {
            //重写该方法，用于测量Bitmap的大小，单位（KB）
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return bitmap.getAllocationByteCount() / 1024;
                } else {
                    return bitmap.getByteCount() / 1024;
                }
            }
        };
        LruCache<String, Bitmap> roundCache = new LruCache<String, Bitmap>(10);
        LruCache<String, Bitmap> blurCache = new LruCache<String, Bitmap>(10);

        cacheMap = new HashMap<>(3);
        cacheMap.put(Type.THUMB, thumbCache);
        cacheMap.put(Type.ROUND, roundCache);
        cacheMap.put(Type.BLUR, blurCache);
    }

    public Bitmap loadThumb(Music music) {
        return loadCover(music, Type.THUMB);
    }

    private Bitmap loadCover(Music music, Type type) {
        Bitmap bitmap;
        String key = getKey(music);
        LruCache<String, Bitmap> cache = cacheMap.get(type);
        if (TextUtils.isEmpty(key)) {
            bitmap = cache.get(KEY_NULL);
            if (bitmap != null) {
                return bitmap;
            }

            bitmap = getDefaultCover(type);
            cache.put(KEY_NULL, bitmap);
            return bitmap;
        }
        return loadCover(null, type);
    }

    private Bitmap getDefaultCover(Type type) {
        return null;
    }

    private String getKey(Music music) {
        if (music == null) {
            return null;
        }

        if (music.getType() == Music.Type.LOCAL && music.getAlbumId() > 0) {
            return String.valueOf(music.getAlbumId());
        } else if (music.getType() == Music.Type.ONLINE && !TextUtils.isEmpty(music.getCoverPath())) {
            return music.getCoverPath();
        } else {
            return null;
        }
    }
}
