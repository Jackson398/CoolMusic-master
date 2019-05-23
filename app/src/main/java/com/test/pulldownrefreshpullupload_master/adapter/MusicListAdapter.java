package com.test.pulldownrefreshpullupload_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.pulldownrefreshpullupload_master.R;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    List<String> items;
    Context context;

    public MusicListAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(items.get(position));
        return view;
    }
}
