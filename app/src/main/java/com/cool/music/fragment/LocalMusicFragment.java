package com.cool.music.fragment;


import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.rxbus.RxBus;
import com.cool.music.R;
import com.cool.music.activity.MusicInfoActivity;
import com.cool.music.adapter.PlaylistAdapter;
import com.cool.music.application.AppCache;
import com.cool.music.application.WeChatOpenPlatform;
import com.cool.music.constants.Keys;
import com.cool.music.constants.RequestCode;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.Music;
import com.cool.music.service.AudioPlayer;
import com.cool.music.utils.CoverLoader;
import com.cool.music.utils.FileUtils;
import com.cool.music.utils.MusicUtils;
import com.cool.music.utils.PermissionReq;
import com.cool.music.utils.ToastUtils;
import com.cool.music.utils.WeChatUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.File;
import java.util.List;

public class LocalMusicFragment extends BaseFragment implements OnMoreClickListener, AdapterView.OnItemClickListener {
    private ListView lvLocalMusic;
    private TextView vSearching;

    private PlaylistAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        initViews(view);
        RxBus.getDefault().subscribe(this, new RxBus.Callback<String>() {
            @Override
            public void onEvent(String s) {
                scanMusic();
            }
        });
        return view;
    }

    private void initViews(View view) {
        lvLocalMusic = (ListView) view.findViewById(R.id.lv_local_music);
        vSearching = (TextView) view.findViewById(R.id.v_searching);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PlaylistAdapter(AppCache.getInstance().getLocalMusicList());
        adapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(adapter);
        if (AppCache.getInstance().getLocalMusicList().isEmpty()) {
            scanMusic();
        }
    }

    @Override
    public void onMoreClick(final int position) {
        Music music = AppCache.getInstance().getLocalMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(music.getTitle());
        dialog.setItems(R.array.local_music_dialog, (dialog1, which) -> {
            switch (which) {
                case 0: //分享
                    shareMusic(music);
                    break;
                case 1: //设为铃声
                    requestSetAlarm(music);
                    break;
                case 2: //删除
                    deleteMusic(music);
                    break;
                case 3: //歌曲信息
//                    MusicInfoActivity.start(getContext(), music);
                    break;
            }
        });
        dialog.show();
    }

    private void deleteMusic(final Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, (dialog1, which) -> {
            String musicFilePath = music.getPath();
            File musicFile = new File(musicFilePath);
            File lrcFile = new File(FileUtils.getLrcFilePath(musicFile.getName()));
            File albumFile = new File(FileUtils.getAlbumFilePath(musicFile.getName()));
            if(musicFile.delete() && lrcFile.delete() && albumFile.delete()) {
                AppCache.getInstance().getLocalMusicList().remove(music); //删除本地歌曲后将歌曲信息从缓存中删除
                adapter.notifyDataSetChanged();
                //刷新媒体库
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                getContext().sendBroadcast(intent);
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }

    private void requestSetAlarm(final Music music) {
        //Android M把权限做了加强管理，在mainfest申明后，在使用到相关功能时还需要重新授权方可使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
            ToastUtils.show(R.string.no_permission_setting);
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
            startActivityForResult(intent, RequestCode.REQUEST_WRITE_SETTINGS);
        } else {
            setAlarm(music);
        }
    }

    //设置闹钟
    private void setAlarm(Music music) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getPath());
        //查询音乐文件在媒体库是否存在
        Cursor cursor = getContext().getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{music.getPath()}, null);
        if (cursor == null) { return; }
        //查询得到的cursor指向第一条记录之前，使用moveToFirst和moveToNext都可以将cursor移动到第一条记录上
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, false); //来电铃声
            values.put(MediaStore.Audio.Media.IS_ALARM, true); //闹铃铃声
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false); //通知铃声
            values.put(MediaStore.Audio.Media.IS_PODCAST, false);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");

            getContext().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[]{music.getPath()});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM, newUri);//设置闹铃铃声
            ToastUtils.show(R.string.setting_ringtone_success);
        }
        cursor.close();
    }

    private void shareMusic(Music music) {
        //todo
        WeChatUtils.shareMusicToWeChatFriends(music);
    }

    public void scanMusic() {
        lvLocalMusic.setVisibility(View.GONE);
        vSearching.setVisibility(View.VISIBLE);
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        new AsyncTask<Void, Void, List<Music>>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }

                            @Override
                            protected void onPostExecute(List<Music> musicList) {
                                AppCache.getInstance().getLocalMusicList().clear();
                                AppCache.getInstance().getLocalMusicList().addAll(musicList);
                                lvLocalMusic.setVisibility(View.VISIBLE);
                                vSearching.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            protected List<Music> doInBackground(Void... voids) {
                                return MusicUtils.scanMusic(getContext());
                            }
                        }.execute();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(R.string.no_permission_storage);
                        lvLocalMusic.setVisibility(View.VISIBLE);
                        vSearching.setVisibility(View.GONE);
                    }
                })
                .request();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Music music = AppCache.getInstance().getLocalMusicList().get(position);
        AudioPlayer.getInstance().addAndPlay(music); //将音乐添加到播放列表并播放音乐
        ToastUtils.show("已添加到播放列表");
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(getContext())) {
                ToastUtils.show(R.string.grant_permission_setting);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        int position = lvLocalMusic.getFirstVisiblePosition();
        int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
        outState.putInt(Keys.LOCAL_MUSIC_POSITION, position);
        outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        lvLocalMusic.post(() -> {
            int position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION);
            int offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET);
            lvLocalMusic.setSelectionFromTop(position, offset);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(this);
    }
}
