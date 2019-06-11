package com.cool.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cool.music.application.Notifier;
import com.cool.music.constants.Actions;

/**
 * The service runs in the main thread of its managed progress, it neither creates its own thread nor
 * runs in a separate progress(unless otherwise specified). This means that if the service will perform
 * any cpu-intensive or blocking operation. (such as mp3 player or networking), a new thread should be
 * created within the service to do the job. By using separate threads, the risk of a "ANR" exception is
 * reduced, while the main thread of tje application continues to focus on running user-activity
 * interactions.
 */
public class PlayService extends Service {
    private static final String TAG = "Service";

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    /**
     * The method only called once.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        AudioPlayer.getInstance().init(this);
        MediaSessionManager.getInstance().init(this);
        Notifier.getInstance().init(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    /**
     * The method use Intent to start service, that {@link #onStartCommand(Intent, int, int)} will be
     * called. the method called by {@link com.cool.music.receiver.StatusBarReceiver#onReceive(Context, Intent)}
     * to stop service while you click close icon in network push notification.
     * @param context
     * @param action
     */
    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    /**
     * The method will be called many times, When another component, such as an activity, request to
     * start a service by calling startService(), the system calls this method, Once this method is
     * executed, the service starts and can run indefinitely in the background. If you implement this
     * method, you need to stop the service by calling stopSelf() or stopService() when the service
     * work is done.<br>
     * The method returns int type, which describes how the system should continue to run the service
     * even if the service terminates.The return must be one of the following constants:
     * {@link #START_REDELIVER_INTENT} if the system terminates the service after onStartCommand() returns,
     * the service is rebuilt and called onStartCommand() with the last Intent passed to the service.
     * Any pending intents are passed in turn. This applies to services that actively perform jobs(such
     * as downloading files) that should be resumed immediately.
     * {@link #START_NOT_STICKY} if the system terminates the service after onStartCommand() returns, the
     * service will not be rebuilt unless an Intent is suspended to be delivered. This is the safest option
     * to avoid running the service unnecessarily and when the application can easily restart all outstanding jobs.
     * {@link #START_STICKY} if the system terminates the service after onStartCommand() returns, the
     * service is rebuilt and onStartCommand() is invoked, but the last Intent is not re-passed. Conversely,
     * unless an Intent is suspended to start the service(in which case, the intent is passed), onStartCommand()
     * is called with an empty Intent. This applies to media players(or similar services) that do not execute
     * commands but run indefinitely and wait for jobs.
     * {@link #START_STICKY_COMPATIBILITY} as a compatible version of {@link #START_STICKY}, there is no
     * guarantee that the onStartCommand() callback will be executed again after the service is killed.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.ACTION_STOP:
                    stop();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void stop() {
        AudioPlayer.getInstance().stopPlayer();
        //todo
        Notifier.getInstance().cancelAll();
    }
}
