package com.cool.music.executor;

import android.view.MenuItem;
import android.view.View;

import com.cool.music.R;
import com.cool.music.activity.MusicActivity;
import com.cool.music.constants.Actions;
import com.cool.music.service.PlayService;
import com.cool.music.service.QuitTimer;
import com.cool.music.storage.Preferences;
import com.cool.music.utils.TimerDialog;

import java.sql.Time;

/**
 * Navigation menu executor
 */
public class NaviMenuExecutor {
    private MusicActivity activity;

    public NaviMenuExecutor(MusicActivity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                //todo
                return true;
            case R.id.action_theme:
                //todo
                return true;
            case R.id.action_timer:
                timerDialog();
                return true;
            case R.id.action_exit:
                activity.finish();
                PlayService.startCommand(activity, Actions.ACTION_STOP);
                return true;
            case R.id.action_about:
                //todo
                return true;

        }
        return false;
    }

    /**
     * The method will pop up a custom stop play music setting dialog {@link TimerDialog}.Setting time
     * will be saved into Preference.
     */
    private void timerDialog() {
        TimerDialog.Builder builder = new TimerDialog.Builder(activity);
                builder.setPositiveButton(activity.getResources().getString(android.R.string.ok), (View v) -> {
                    Preferences.setTimerValue(builder.getTimeValue());
                    startTimer(Integer.valueOf(builder.getTimeValue()));
                    builder.dialog.dismiss();
                })
                .setNegativeButton(activity.getResources().getString(android.R.string.cancel), (View v) -> {
                    builder.dialog.dismiss();
                })
                .setTimeValue(Preferences.getTimerValue()).create().show();
    }

    private void startTimer(int minute) {
        QuitTimer.getInstance().start(minute * 60 * 1000);
    }
}
