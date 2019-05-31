package com.cool.music.application;

import android.app.NotificationManager;
import android.content.Context;
import com.cool.music.model.Music;
import com.cool.music.service.PlayService;

public class Notifier {
    private static final int NOTIFICATION_ID = 0x12345;
    /**
     * volatile used to guarantee visibility, that instance is immediately visible to others threads
     * after instantiation, whereas synchronized guarantees both atomicity and visibility. Only one
     * thread is flushed into main memory before the lock is released, ensuring that other thread
     * enters, it read the latest variable contents in main memory.<br>
     * Assuming no keyword volatile situation, two threads A and B are both the first time to invoke this
     * single method. Thread A instantiate the instance object firstly, the constructor is an atomic
     * operation, it will generated many bytecode instruction after complied, for the Java reordering
     * of instructions. Then instantiate the instance object may be done at first(The actual operation
     * is only malloc memory area to storage object before return the memory reference), after instantiate
     * the instance object, instance object is not null, but the actual initialization operation did not
     * perform, if thread B enters at this time, it will see a not null but incomplete initialization
     * instance object, so you need to join the volatile keyword to prevent reordering optimization,
     * so as to implement the singleton safely.
     * @see <a href="https://blog.csdn.net/xiakepan/article/details/52444565">
     *     Why do singleton double-checked locks need the volatile keyword</a>
     */
    private static volatile Notifier instance;
    private PlayService playService;
    private NotificationManager notificationManager;

    public Notifier() {
    }

    public static Notifier getInstance() {
        if (instance == null) {
            synchronized (Notifier.class) {
                if (instance == null) {
                    instance = new Notifier();
                }
            }
        }
        return instance;
    }

    public void init(PlayService playService) {
        this.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(Music music) {
      //TODO
    }
}
