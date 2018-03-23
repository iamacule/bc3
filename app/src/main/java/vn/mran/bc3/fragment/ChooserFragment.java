package vn.mran.bc3.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import vn.mran.bc3.R;
import vn.mran.bc3.base.BaseFragment;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.Task;
import vn.mran.bc3.util.TouchEffect;

/**
 * Created by Mr An on 23/03/2018.
 */

public class ChooserFragment extends BaseFragment implements View.OnClickListener{
    private ImageView imgPlay;
    private ImageView imgExit;

    private ParticleSystem ps1;
    private ParticleSystem ps2;
    private ParticleSystem ps3;
    private ParticleSystem ps4;

    @Override
    public void initLayout() {
        imgPlay = (ImageView) v.findViewById(R.id.imgPlay);
        imgExit = (ImageView) v.findViewById(R.id.imgExit);
        imgPlay.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.button_background), screenWidth / 3));
        imgExit.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.button_background), screenWidth / 3));
        startAnimation();
    }

    @Override
    public void initValue() {
        TouchEffect.addAlpha(imgPlay);
        TouchEffect.addAlpha(imgExit);

        setVersion();
    }

    private void setVersion() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ((TextView) v.findViewById(R.id.txtVersion)).setText("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initAction() {
        imgPlay.setOnClickListener(this);
        imgExit.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void startAnimation() {
        Task.startNewBackGroundThread(new Thread(new Runnable() {
            @Override
            public void run() {
                Task.sleep(500);
                Task.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        ps1 = new ParticleSystem(getActivity(), 50, R.drawable.ball_1, 1000);
                        ps1.setSpeedRange(0.1f, 0.25f)
                                .emit(v.findViewById(R.id.lnAnimation), 100);
                        ps2 = new ParticleSystem(getActivity(), 50, R.drawable.ball_2, 1000);
                        ps2.setSpeedRange(0.1f, 0.25f)
                                .emit(v.findViewById(R.id.lnAnimation), 100);
                        ps3 = new ParticleSystem(getActivity(), 50, R.drawable.ball_3, 1000);
                        ps3.setSpeedRange(0.1f, 0.25f)
                                .emit(v.findViewById(R.id.lnAnimation2), 100);
                        ps4 = new ParticleSystem(getActivity(), 50, R.drawable.ball_4, 1000);
                        ps4.setSpeedRange(0.1f, 0.25f)
                                .emit(v.findViewById(R.id.lnAnimation2), 100);
                    }
                });
            }
        }));
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_chooser;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPlay:
                ps1.cancel();
                ps2.cancel();
                ps3.cancel();
                ps4.cancel();
                goTo(new PlayFragment());
                break;
            case R.id.imgExit:
                finish();
                break;
        }
    }

}
