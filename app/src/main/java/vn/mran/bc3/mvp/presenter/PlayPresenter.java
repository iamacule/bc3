package vn.mran.bc3.mvp.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vn.mran.bc3.helper.Log;
import vn.mran.bc3.mvp.view.PlayView;
import vn.mran.bc3.util.ScreenUtil;
import vn.mran.bc3.util.Task;

/**
 * Created by Mr An on 29/11/2017.
 */

public class PlayPresenter {
    private final String TAG = getClass().getSimpleName();

    private Context context;

    private PlayView view;

    private boolean run = true;

    public PlayPresenter(PlayView view,Context context) {
        this.context = context;
        this.view = view;

        new PlayThread().start();
        new FireworkDelayThread().start();
    }

    public void stopAllThread() {
        Log.d(TAG, "Stop checking network");
        run = false;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class PlayThread extends Thread {
        @Override
        public void run() {
            while (run) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkingNetwork();
            }
        }

        private void checkingNetwork() {
            view.onNetworkChanged(isOnline());
        }
    }

    private class FireworkDelayThread extends Thread {
        @Override
        public void run() {
            while (run) {
                try {
                    Thread.sleep(ScreenUtil.getRandomNumber(3000,8000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Task.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        view.startFireworks();
                    }
                });
            }
        }
    }

    public String updateText(String oldText) {
        if (oldText.length() * 2 < 90) {
            StringBuilder stringBuilder = new StringBuilder(oldText);
            for (int i = stringBuilder.length() * 2; i <= 90; i++) {

                stringBuilder.append(" ");
            }

            stringBuilder.append(oldText);
            return stringBuilder.toString();
        }
        return oldText;
    }
}
