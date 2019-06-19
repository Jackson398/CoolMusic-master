package com.cool.music.http;

import android.support.annotation.NonNull;

import com.cool.music.model.DownloadInfo;
import com.cool.music.model.LiveWeather;
import com.cool.music.model.Lrc;
import com.cool.music.model.OnlineMusicList;
import com.cool.music.model.SearchMusic;
import com.cool.music.model.TimeWeather;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class HttpClient {
    private static final String SPLASH_URL = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
    private static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    private static final String METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList";
    private static final String METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play";
    private static final String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
    private static final String METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug";
    private static final String METHOD_LRC = "baidu.ting.song.lry";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_SONG_ID = "songid";
    private static final String PARAM_TING_UID = "tinguid";
    private static final String PARAM_QUERY = "query";

    public static final String WEATHER_TYPE_BASE = "base";
    public static final String WEATHER_TYPE_ALL = "all";
    private static final String WEATHER_URL = "http://restapi.amap.com/v3/weather/weatherInfo";
    private static final String KEY = "key";
    private static final String CITY = "city";
    private static final String EXTENSIONS = "extensions";
    private static final String OUTPUT = "output";
    private static final String OUTPUT_FORMAT = "JSON";
    private static final String KEY_VALUE = "7c3cc2df97d7fe15f13db8444888949c";

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static void downloadFile(String url, String destFileDir, String destFileName, final HttpCallback<File> callback) {
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(destFileDir, destFileName) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        if (callback != null) {
                            callback.onFinish();
                        }
                    }

                });
    }

    public static void getMusicDownloadInfo(String songId, final HttpCallback<DownloadInfo> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_DOWNLOAD_MUSIC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<DownloadInfo>(DownloadInfo.class) {
                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(DownloadInfo response, int id) {
                        callback.onSuccess(response);
                    }
                });
    }

    public static void getSongListInfo(String type, int size, int offset, @NonNull final HttpCallback<OnlineMusicList> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
                .addParams(PARAM_TYPE, type)
                .addParams(PARAM_SIZE, String.valueOf(size))
                .addParams(PARAM_OFFSET, String.valueOf(offset))
                .build()
                .execute(new JsonCallback<OnlineMusicList>(OnlineMusicList.class) {
                    @Override
                    public void onResponse(OnlineMusicList response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void searchMusic(String keyword, @NonNull final HttpCallback<SearchMusic> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .addParams(PARAM_QUERY, keyword)
                .build()
                .execute(new JsonCallback<SearchMusic>(SearchMusic.class) {
                    @Override
                    public void onResponse(SearchMusic response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getLrc(String songId, @NonNull final HttpCallback<Lrc> callback) {
        OkHttpUtils.get().url(BASE_URL)
                .addParams(PARAM_METHOD, METHOD_LRC)
                .addParams(PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<Lrc>(Lrc.class) {
                    @Override
                    public void onResponse(Lrc response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }


    public static void getLiveWeather(String adcode, String type, final HttpCallback<LiveWeather> callback) {
        OkHttpUtils.get().url(WEATHER_URL)
                .addParams(KEY, KEY_VALUE)
                .addParams(CITY, adcode)
                .addParams(EXTENSIONS, type)
                .addParams(OUTPUT, OUTPUT_FORMAT)
                .build()
                .execute(new JsonCallback<LiveWeather>(LiveWeather.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(LiveWeather response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getTimeWeather(String adcode, String type, final HttpCallback<TimeWeather> callback) {
        OkHttpUtils.get().url(WEATHER_URL)
                .addParams(KEY, KEY_VALUE)
                .addParams(CITY, adcode)
                .addParams(EXTENSIONS, type)
                .addParams(OUTPUT, OUTPUT_FORMAT)
                .build()
                .execute(new JsonCallback<TimeWeather>(TimeWeather.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(TimeWeather response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
}
