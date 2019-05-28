package com.cool.music.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.Music;
import com.cool.music.utils.CoverLoader;
import com.cool.music.utils.FileUtils;

import java.util.List;

/**
 * 本地音乐列表适配器
 */
public class PlaylistAdapter extends BaseAdapter {

    private List<Music> musicList;
    private OnMoreClickListener listener;
    private boolean isPlaylist;

    public PlaylistAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public void setIsPlaylist(boolean playlist) {
        isPlaylist = playlist;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //todo
        Music music = musicList.get(position);
//        Bitmap cover = CoverLoader.getInstance().loadThumb(music);
        holder.ivCover.setImageBitmap(null);
        holder.tvTitle.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    //最后一行不显示分割符
    private boolean isShowDivider(int position) {
        return position != musicList.size() - 1;
    }

    private static class ViewHolder {
        private View vPlaying;
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvArtist;
        private ImageView ivMore;
        private View vDivider;

        public ViewHolder(View view) {
            this.vPlaying = (View) view.findViewById(R.id.v_playing);
            this.ivCover = (ImageView) view.findViewById(R.id.iv_cover);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.tvArtist = (TextView) view.findViewById(R.id.tv_artist);
            this.ivMore = (ImageView) view.findViewById(R.id.iv_more);
            this.vDivider = (View) view.findViewById(R.id.v_divider);
        }
    }
}
