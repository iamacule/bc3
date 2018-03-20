package vn.mran.bc3.mvp.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    private boolean isEnablePlusMoney = false;
    private int currentMoney;

    private int[] moneyArrays = new int[6];
    private int tong;

    public PlayPresenter(PlayView view) {
        this.context = (Context) view;
        this.view = view;

        new PlayThread().start();
        new FireworkDelayThread().start();
    }

    public void setEnablePlusMoney(boolean enablePlusMoney) {
        isEnablePlusMoney = enablePlusMoney;
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

    public String updateMoneyValue(int value) {
        return value + "k";
    }

    public void onChoose(int[] valueArrays, int[] resultArrays) {
        for (int i = 0; i < valueArrays.length; i++) {
            moneyArrays[i] = (valueArrays[i] * currentMoney);
        }
        calculateResult(resultArrays);
    }

    public void resetMoneyArray() {
        moneyArrays = new int[6];
    }

    public void setCurrentMoney(int currentMoney) {
        Log.d(TAG, "setCurrentMoney : " + currentMoney);
        this.currentMoney = currentMoney;
    }

    public void calculateResult(int[] resultArrays) {
        tong = 0;
        if (currentMoney > 0) {
            int result[] = new int[6];
            for (int i = 0; i < moneyArrays.length; i++) {
                for (int j = 0; j < resultArrays.length; j++) {
                    if (i == resultArrays[j]) {
                        result[i] = result[i] + 1;
                    }
                }
            }

            for (int i = 0; i < result.length; i++) {
                if (result[i] > 0)
                    tong = tong + (moneyArrays[i] * result[i]);
                else
                    tong = tong - moneyArrays[i];
            }
        }
    }

    public void executeResult() {
        view.onResultExecute(tong);
        Log.d(TAG, "Tong = " + tong);
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
                Task.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        checkingTime();
                    }
                });
            }
        }

        private void checkingTime() {
            if (isEnablePlusMoney) {
                int time = Integer.parseInt(new SimpleDateFormat("ss").format(new Date()));
                view.onTimeChanged("+10k : " + (60 - time));
                if (time == 0) {
                    view.onAddMoney();
                }
            } else {
                view.onTimeChanged("");
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
