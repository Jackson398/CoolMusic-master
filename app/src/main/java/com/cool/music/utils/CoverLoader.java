package com.cool.music.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.cool.music.R;
import com.cool.music.model.Music;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.cool.music.application.WeChatOpenPlatform.IMAGE_SIZE;


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

    public byte[] getThumbData(Music music) {
        //todo
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), 1, options);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        int quality = 100;
        while (output.toByteArray().length > IMAGE_SIZE && quality != 10) {
            output.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);
            quality -= 10;
        }
        bitmap.recycle();
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bitmap loadThumb(Music music) {
        return loadCover(music, Type.THUMB);
    }

    private Bitmap loadCover(Music music, Type type) {
        Bitmap bitmap;
        String key = getKey(music);
        LruCache<String, Bitmap> cache = cacheMap.get(type);
        if (TextUtils.isEmpty(key)) { //键为null，表示使用默认的封面
            bitmap = cache.get(KEY_NULL);//先从缓存中获取，获取到直接返回
            if (bitmap != null) {
                return bitmap;
            }

            bitmap = getDefaultCover(type);//缓存中不存在，而获取默认封面
            cache.put(KEY_NULL, bitmap); //缓存
            return bitmap;
        }

        bitmap = cache.get(key); //获取缓存中的封面
        if (bitmap != null) {
            return bitmap;
        }

        bitmap = loadCoverByType(music, type); //获取本地或者网络下载的封面
        if (bitmap != null) {
            cache.put(key, bitmap);
            return bitmap;
        }

        return loadCover(null, type);
    }


    private Bitmap loadCoverByType(Music music, Type type) {
        Bitmap bitmap;
        if (music.getType() == Music.Type.LOCAL) {
            bitmap = loadCoverFromMediaStore(music.getAlbumId());
        } else {
            bitmap = loadCoverFromFile(music.getCoverPath());
        }

        switch (type) {
            case ROUND:
                bitmap = ImageUtils.resizeImage(bitmap, roundLength, roundLength);
                return ImageUtils.createCircleImage(bitmap);
            case BLUR:
                return ImageUtils.blur(bitmap);
            default:
                return bitmap;
        }
    }

    /**
     * 从下载的图片中加载封面<br>
     * 网络音乐
     */
    private Bitmap loadCoverFromFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 从媒体库中加载封面<br>
     * 本地音乐
     */
    private Bitmap loadCoverFromMediaStore(long albumId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MusicUtils.getMediaStoreAlbumCoverUri(albumId);
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (FileNotFoundException ignored) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options(); //该类用于对图片进行解码时使用的配置参数类
        options.inPreferredConfig = Bitmap.Config.RGB_565; //设置图片解码时使用的颜色模式
        return BitmapFactory.decodeStream(is, null, options);
    }

    private Bitmap getDefaultCover(Type type) {
        switch (type) {
            case ROUND:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.play_page_default_cover);
                bitmap = ImageUtils.resizeImage(bitmap, roundLength, roundLength);
                return bitmap;
            case BLUR:
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.play_page_default_bg);
            default:
                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_cover);
        }
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
