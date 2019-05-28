package com.test.pulldownrefreshpullupload_master.executor;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.test.pulldownrefreshpullupload_master.application.AppCache;
import com.test.pulldownrefreshpullupload_master.http.HttpCallback;
import com.test.pulldownrefreshpullupload_master.http.HttpClient;
import com.test.pulldownrefreshpullupload_master.model.DownloadInfo;
import com.test.pulldownrefreshpullupload_master.model.OnlineMusic;
import com.test.pulldownrefreshpullupload_master.utils.FileUtils;
import com.test.pulldownrefreshpullupload_master.utils.ToastUtils;

import java.io.File;

public abstract class DownloadOnlineMusic extends DownloadMusic {
    private OnlineMusic mOnlineMusic;

    public DownloadOnlineMusic(Activity mActivity, OnlineMusic onlineMusic) {
        super(mActivity);
        mOnlineMusic = onlineMusic;
    }

    protected void downloadMusic(String url, String artist, String title, String coverPath) {
        try {
            String fileName = FileUtils.getMp3FileName(artist, title);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(FileUtils.getFileName(artist, title));
            request.setDescription("正在下载…");
            request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), fileName);
            request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false); // 不允许漫游
            DownloadManager downloadManager = (DownloadManager) AppCache.getInstance().getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            long id = downloadManager.enqueue(request);
            String musicAbsPath = FileUtils.getMusicDir().concat(fileName);
            DownloadMusicInfo downloadMusicInfo = new DownloadMusicInfo(title, musicAbsPath, coverPath);
            AppCache.getInstance().getDownloadList().put(id, downloadMusicInfo);
        } catch (Throwable th) {
            th.printStackTrace();
            ToastUtils.show("下载失败");
        }
    }

    @Override
    protected void download() {
        final String artist = mOnlineMusic.getArtist_name();
        final String title = mOnlineMusic.getTitle();

        //下载歌词
        String lrcFileName = FileUtils.getLrcFileName(artist, title);
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(mOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            HttpClient.downloadFile(mOnlineMusic.getLrclink(), FileUtils.getLrcDir(), lrcFileName, null);
        }

        //下载封面
        String albumFileName = FileUtils.getAlbumFileName(artist, title);
        final File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
        String picUrl = mOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            picUrl = mOnlineMusic.getPic_small();
        }
        if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
            HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), albumFileName, null);
        }

        //获取歌曲下载链接
        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) {
                if (response == null || response.getBitrate() == null) {
                    onFail(null);
                    return;
                }
                //下载歌曲
                downloadMusic(response.getBitrate().getFile_link(), artist, title, albumFile.getPath());
                onExecuteSuccess(null);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }
}
