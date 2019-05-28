package com.test.pulldownrefreshpullupload_master.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.test.pulldownrefreshpullupload_master.R;
import com.test.pulldownrefreshpullupload_master.constants.Extras;

public class ArtistInfoActivity extends BaseActivity {
    private ScrollView svArtistInfo;
    private LinearLayout llArtistInfoContainer;
    private LinearLayout llLoading;
    private LinearLayout llLoadFail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);
    }

    public static void start(Context context, String tingUid) {
        Intent intent = new Intent(context, ArtistInfoActivity.class);
        intent.putExtra(Extras.TING_UID, tingUid);
        context.startActivity(intent);
    }

}
