package vn.mran.bc3;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import vn.mran.bc3.activity.ChooserActivity;
import vn.mran.bc3.base.BaseActivity;
import vn.mran.bc3.constant.PrefValue;
import vn.mran.bc3.instance.Media;
import vn.mran.bc3.instance.Rule;
import vn.mran.bc3.util.MyAnimation;
import vn.mran.bc3.widget.CustomTextView;

public class SplashActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private final Handler handler = new Handler();
    private CustomTextView txtTitle;
    private LinearLayout lnMain;

    @Override
    public void initLayout() {
        txtTitle = findViewById(R.id.txtTitle);
        lnMain = (LinearLayout) findViewById(R.id.frMain);
    }

    @Override
    public void initValue() {
        Rule.init(getApplicationContext());
    }

    @Override
    public void initAction() {
        txtTitle.startAnimation(MyAnimation.fadeIn(this));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = MyAnimation.fadeOut(SplashActivity.this);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (preferences.getBooleanValue(PrefValue.SETTING_SOUND, true)) {
                            Media.playBackgroundMusic(getApplicationContext());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lnMain.clearAnimation();
                        lnMain.removeAllViews();
                        Intent in = new Intent(SplashActivity.this, ChooserActivity.class);
                        startActivity(in);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                lnMain.startAnimation(animation);
            }
        }, 1500);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
