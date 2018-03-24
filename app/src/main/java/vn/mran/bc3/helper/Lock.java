package vn.mran.bc3.helper;

import android.os.Handler;

/**
 * Created by An Pham on 06-Dec-16.
 * This class use for Lock something
 */
public class Lock {
    private int timeDelay = 0;
    public boolean enable = true;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            enable = true;
        }
    };

    public Lock(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public void setTimeDelay(int time) {
        this.timeDelay = time;
    }

    public void lock() {
        enable = false;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, timeDelay);
    }
}