package com.cool.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.music.R;
import com.cool.music.listener.OnMoreClickListener;
import com.cool.music.model.SearchMusic;

import java.util.List;

public class SearchMusicAdapter extends BaseAdapter {
    private List<SearchMusic.Song> mData;
    private OnMoreClickListener mListener;

    public SearchMusicAdapter(List<SearchMusic.Song> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
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
        holder.tvTitle.setText(mData.get(position).getSongname());
        holder.tvArtist.setText(mData.get(position).getArtistname());
        holder.ivMore.setOnClickListener(v -> mListener.onMoreClick(position));
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    public void setOnMoreClickListener(OnMoreClickListener mListener) {
        this.mListener = mListener;
    }

    private static class ViewHolder {
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvArtist;
        private ImageView ivMore;
        private View vDivider;

        public ViewHolder(View view) {
            ivCover = view.findViewById(R.id.iv_cover);
            tvTitle = view.findViewById(R.id.tv_title);
            tvArtist = view.findViewById(R.id.tv_artist);
            ivMore = view.findViewById(R.id.iv_more);
            vDivider = view.findViewById(R.id.v_divider);
            ivCover.setVisibility(View.GONE);
        }
    }
}
