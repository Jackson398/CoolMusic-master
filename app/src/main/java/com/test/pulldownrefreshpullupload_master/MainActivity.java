package com.test.pulldownrefreshpullupload_master;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.test.pulldownrefreshpullupload_master.activity.PullableListViewActivity;
import com.test.pulldownrefreshpullupload_master.adapter.MusicListAdapter;
import com.test.pulldownrefreshpullupload_master.ui.PullToRefreshLayout;
import com.test.pulldownrefreshpullupload_master.util.GenerateDataUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((PullToRefreshLayout) findViewById(R.id.pull_down_refresh_pull_up_load_layout)).setOnRefreshListener(new PullableListener());
        mList = (ListView) findViewById(R.id.content_view);
        initListView();
    }

    private void initListView() {
        List<String> items = new ArrayList<String>();
        items.add("ListView");
        items.add("GridView");
        items.add("ExpandableListView");
        items.add("ScrollView");
        items.add("WebView");
        items.add("ImageView");
        items.add("TextView");
        MusicListAdapter adapter = new MusicListAdapter(items, this);
        mList.setAdapter(adapter);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        MainActivity.this,
                        " LongClick on "
                                + parent.getAdapter().getItemId(position),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(MainActivity.this, PullableListViewActivity.class);
                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }
}
