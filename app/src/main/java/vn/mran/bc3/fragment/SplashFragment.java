package vn.mran.bc3.fragment;

import android.os.Handler;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import vn.mran.bc3.R;
import vn.mran.bc3.base.BaseFragment;
import vn.mran.bc3.constant.PrefValue;
import vn.mran.bc3.instance.Media;
import vn.mran.bc3.util.MyAnimation;
import vn.mran.bc3.widget.CustomTextView;

/**
 * Created by Mr An on 23/03/2018.
 */

public class SplashFragment extends BaseFragment {

    private final String TAG = getClass().getSimpleName();
    private final Handler handler = new Handler();
    private CustomTextView txtTitle;
    private LinearLayout lnMain;

    @Override
    public void initLayout() {
        txtTitle = v.findViewById(R.id.txtTitle);
        lnMain = (LinearLayout) v.findViewById(R.id.frMain);
    }

    @Override
    public void initValue() {
    }

    @Override
    public void initAction() {
        txtTitle.startAnimation(MyAnimation.fadeIn(getActivity()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = MyAnimation.fadeOut(getActivity());
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (preferences.getBooleanValue(PrefValue.SETTING_SOUND, true)) {
                            Media.playBackgroundMusic(getContext());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lnMain.clearAnimation();
                        lnMain.removeAllViews();
                        goTo(new ChooserFragment());
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
        return R.layout.fragment_main;
    }
}
