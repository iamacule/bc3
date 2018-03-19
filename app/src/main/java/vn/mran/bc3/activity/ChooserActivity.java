package vn.mran.bc3.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import vn.mran.bc3.R;
import vn.mran.bc3.base.BaseActivity;
import vn.mran.bc3.instance.Media;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.Task;
import vn.mran.bc3.util.TouchEffect;

/**
 * Created by MrAn PC on 13-Feb-16.
 */
public class ChooserActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgPlay;
    private ImageView imgExit;

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

    @Override
    public int setLayout() {
        return R.layout.activity_chooser;
    }

    private void startAnimation() {

        Task.postDelay(new Runnable() {
            @Override
            public void run() {
                ParticleSystem ps = new ParticleSystem(ChooserActivity.this, 100, R.drawable.fire_slot, 1000);
                ps.setScaleRange(0.7f, 1.3f);
                ps.setSpeedModuleAndAngleRange(0.07f, 0.16f, 0, 180);
                ps.setRotationSpeedRange(90, 180);
                ps.setAcceleration(0.00013f, 90);
                ps.setFadeOut(200, new AccelerateInterpolator());
                ps.emit(findViewById(R.id.lnFire1), 100, 360000);


                ParticleSystem ps2 = new ParticleSystem(ChooserActivity.this, 100, R.drawable.fire_slot, 1000);
                ps2.setScaleRange(0.7f, 1.3f);
                ps2.setSpeedModuleAndAngleRange(0.07f, 0.16f, 0, 180);
                ps2.setRotationSpeedRange(90, 180);
                ps2.setAcceleration(0.00013f, 90);
                ps2.setFadeOut(200, new AccelerateInterpolator());
                ps2.emit(findViewById(R.id.lnFire2), 100, 360000);
            }
        }, 1000);
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
