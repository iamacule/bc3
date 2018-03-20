package vn.mran.bc3.activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import vn.mran.bc3.R;
import vn.mran.bc3.base.BaseActivity;
import vn.mran.bc3.helper.Log;
import vn.mran.bc3.instance.Media;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.ScreenUtil;
import vn.mran.bc3.util.Task;
import vn.mran.bc3.util.TouchEffect;

/**
 * Created by MrAn PC on 13-Feb-16.
 */
public class ChooserActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgPlay;
    private ImageView imgExit;

    private boolean run = true;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void initLayout() {
        hideStatusBar();
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgExit = (ImageView) findViewById(R.id.imgExit);
        imgPlay.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.button_background), screenWidth / 3));
        imgExit.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.button_background), screenWidth / 3));
    }

    @Override
    public void initValue() {
        TouchEffect.addAlpha(imgPlay);
        TouchEffect.addAlpha(imgExit);

        setVersion();
    }

    private void setVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView) findViewById(R.id.txtVersion)).setText("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initAction() {
        imgPlay.setOnClickListener(this);
        imgExit.setOnClickListener(this);
        startAnimation();
    }

    private void startAnimation() {
       Task.startNewBackGroundThread(new Thread(new Runnable() {
           @Override
           public void run() {
               Task.sleep(500);
               Task.runOnUIThread(new Runnable() {
                   @Override
                   public void run() {
                       new ParticleSystem(ChooserActivity.this, 50, R.drawable.ball_1, 1000)
                               .setSpeedRange(0.1f, 0.25f)
                               .emit(findViewById(R.id.lnAnimation), 100);
                       new ParticleSystem(ChooserActivity.this, 50, R.drawable.ball_2, 1000)
                               .setSpeedRange(0.1f, 0.25f)
                               .emit(findViewById(R.id.lnAnimation), 100);
                       new ParticleSystem(ChooserActivity.this, 50, R.drawable.ball_3, 1000)
                               .setSpeedRange(0.1f, 0.25f)
                               .emit(findViewById(R.id.lnAnimation2), 100);
                       new ParticleSystem(ChooserActivity.this, 50, R.drawable.ball_4, 1000)
                               .setSpeedRange(0.1f, 0.25f)
                               .emit(findViewById(R.id.lnAnimation2), 100);
                   }
               });
           }
       }));
    }

    @Override
    public int setLayout() {
        return R.layout.activity_chooser;
    }

    @Override
    public void onBackPressed() {
        finish();
        Media.stopBackgroundMusic();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPlay:
                startActivity(PlayActivity.class);
                break;
            case R.id.imgExit:
                finish();
                Media.stopBackgroundMusic();
                break;
        }
    }

}
