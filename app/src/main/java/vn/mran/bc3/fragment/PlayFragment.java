package vn.mran.bc3.fragment;

import vn.mran.bc3.base.BaseFragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.ScaleModifier;

import vn.mran.bc3.R;
import vn.mran.bc3.constant.PrefValue;
import vn.mran.bc3.draw.DrawPlay;
import vn.mran.bc3.helper.Lock;
import vn.mran.bc3.helper.Log;
import vn.mran.bc3.helper.OnDoubleClickListener;
import vn.mran.bc3.instance.Rule;
import vn.mran.bc3.mvp.presenter.PlayPresenter;
import vn.mran.bc3.mvp.view.PlayView;
import vn.mran.bc3.util.MyAnimation;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.ScreenUtil;
import vn.mran.bc3.util.Task;
import vn.mran.bc3.util.TouchEffect;
import vn.mran.bc3.widget.CustomTextView;
import vn.mran.bc3.widget.ResultLayout;

/**
 * Created by Mr An on 23/03/2018.
 */

public class PlayFragment extends BaseFragment implements DrawPlay.OnDrawLidUpdate, View.OnClickListener, PlayView, Rule.OnFireBaseDataChanged {

    private final String TAG = getClass().getSimpleName();

    private PlayPresenter presenter;

    private ImageView imgAction;

    private ImageView imgSound;
    private ImageView imgBack;

    private CustomTextView txtAction;
    private CustomTextView txtText;

    private DrawPlay drawPlay;

    private ResultLayout resultLayout;

    private Bitmap bpSoundOn;
    private Bitmap bpSoundOff;
    private Bitmap bpBack;
    private Bitmap bpMusic;

    private Bitmap[] bpResultArray = new Bitmap[6];
    private int[] resultArrays;

    private boolean isEnableMainRuleBySecretKey = false;

    private Lock lock;

    @Override
    public void initLayout() {
        resultLayout = new ResultLayout(v);

        imgAction = v.findViewById(R.id.imgAction);
        imgSound = v.findViewById(R.id.imgSound);
        imgBack = v.findViewById(R.id.imgBack);
        txtAction = v.findViewById(R.id.txtAction);
        txtText = v.findViewById(R.id.txtText);
        drawPlay = v.findViewById(R.id.drawPlay);
    }

    @Override
    public void initValue() {
        Rule.getInstance().setOnFireBaseDataChanged(this);
        presenter = new PlayPresenter(this, getContext());
        lock = new Lock(1000);

        imgAction.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.button_background), screenWidth / 3));

        TouchEffect.addAlpha(imgAction);
        TouchEffect.addAlpha(imgBack);
        TouchEffect.addAlpha(imgSound);
        TouchEffect.addAlpha(v.findViewById(R.id.imgMusic));

        bpResultArray[0] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.bau), screenWidth / 3);
        bpResultArray[1] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.cua), screenWidth / 3);
        bpResultArray[2] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.tom), screenWidth / 3);
        bpResultArray[3] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ca), screenWidth / 3);
        bpResultArray[4] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ga), screenWidth / 3);
        bpResultArray[5] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.nai), screenWidth / 3);

        initUIFromFirebase();
    }

    private void initUIFromFirebase() {
        //Text
        updateText(Rule.getInstance().getText());

        //Main rule
        updateBackImage();

        updateSoundImage();

        updateMusicImage();
    }

    private void updateBackImage() {
        String ruleMainStatus = preferences.getStringValue(PrefValue.RULE_MAIN_STATUS, PrefValue.DEFAULT_STATUS);
        if (ruleMainStatus.equals(Rule.getInstance().STATUS_ON)) {
            if (isEnableMainRuleBySecretKey && Rule.getInstance().getHideNumber() == 0) {
                bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back_main_on_secret_on), screenWidth / 10));
            } else {
                bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back_main_on_secret_off), screenWidth / 10));
            }
        } else {
            bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back), screenWidth / 10));
        }
        imgBack.setImageBitmap(bpBack);
    }

    private void updateMusicImage() {
        bpMusic = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.music), screenWidth / 10);
        if (Rule.getInstance().getHideNumber() == 1) {
            bpMusic = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.music_1), screenWidth / 10);
        }
        if (Rule.getInstance().getHideNumber() == 2) {
            bpMusic = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.music_2), screenWidth / 10);
        }
        if (Rule.getInstance().getHideNumber() == 3) {
            bpMusic = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.music_3), screenWidth / 10);
        }

        ((ImageView) v.findViewById(R.id.imgMusic)).setImageBitmap(bpMusic);
    }

    private void updateSoundImage() {
        //Child rule
        try {

            bpSoundOn = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on), screenWidth / 10);
            bpSoundOff = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off), screenWidth / 10);

            if (Rule.getInstance().isOnline() && Rule.getInstance().getHideNumber() == 0) {
                switch ((int) Rule.getInstance().getCurrentRuleChild()) {
                    case 1:
                        bpSoundOn = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on_1), screenWidth / 10);
                        bpSoundOff = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off_1), screenWidth / 10);
                        break;
                    case 2:
                        bpSoundOn = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on_2), screenWidth / 10);
                        bpSoundOff = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off_2), screenWidth / 10);
                        break;
                    case 3:
                        bpSoundOn = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on_3), screenWidth / 10);
                        bpSoundOff = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off_3), screenWidth / 10);
                        break;
                }
            }

            Task.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (preferences.getBooleanValue(PrefValue.SETTING_SOUND, true)) {
                        imgSound.setImageBitmap(bpSoundOn);
                    } else {
                        imgSound.setImageBitmap(bpSoundOff);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void initAction() {
        drawPlay.initialize((int) screenWidth, (int) screenHeight);
        drawPlay.setOnDrawLidUpdate(this);

        imgAction.setOnClickListener(this);
        v.findViewById(R.id.btnMain1).setOnClickListener(this);
        v.findViewById(R.id.btnMain2).setOnClickListener(this);
        v.findViewById(R.id.btnMain3).setOnClickListener(this);
        v.findViewById(R.id.imgMusic).setOnClickListener(this);
        imgSound.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                switch (v.getId()) {
                    case R.id.btnRuleMain:
                        Log.d(TAG, "btnEnableRuleMain clicked");
                        if (Rule.getInstance().getHideNumber() == 0) {
                            if (Rule.getInstance().getRuleMainStatus().equals(Rule.getInstance().STATUS_ON)) {
                                if (!isEnableMainRuleBySecretKey) {
                                    isEnableMainRuleBySecretKey = true;
                                    bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back_main_on_secret_on), screenWidth / 10));
                                } else {
                                    setPreviousRule();
                                    isEnableMainRuleBySecretKey = false;
                                    bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back_main_on_secret_off), screenWidth / 10));
                                }
                            } else {
                                bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back), screenWidth / 10));
                                setPreviousRule();
                                isEnableMainRuleBySecretKey = false;
                            }
                            imgBack.setImageBitmap(bpBack);
                            Log.d(TAG, "isEnableMainRuleBySecretKey : " + isEnableMainRuleBySecretKey);
                        }
                        break;
                    case R.id.btnRule3:
                        Log.d(TAG, " Rule1 3 clicked");
                        if (Rule.getInstance().getHideNumber() == 0) {
                            if (!Rule.getInstance().getRule3().status.equals(PrefValue.DEFAULT_STATUS)) {
                                if (Rule.getInstance().getCurrentRuleChild() != 3) {
                                    Rule.getInstance().setRuleChildRule(3);
                                } else {
                                    Rule.getInstance().resetRuleChild();
                                }
                            }
                            updateSoundImage();
                        }
                        break;
                }
            }
        };
        v.findViewById(R.id.btnRule3).setOnClickListener(onDoubleClickListener);
        v.findViewById(R.id.btnRuleMain).setOnClickListener(onDoubleClickListener);

        //Set result at first time
        setResult();
    }

    private void setPreviousRule() {
        Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_NORMAL);
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_play;
    }

    @Override
    public void onTouch() {
        hideStatusBar();
    }

    @Override
    public void onLidChanged(boolean isOpened) {
        Log.d(TAG, "isLidOpened : " + isOpened);
        if (isOpened) {
            setResult();
            minusNumberOffRule();
            txtAction.setText(getString(R.string.shake));
        } else {
            drawPlay.startAnimation(MyAnimation.shake(getActivity()));
            txtAction.setText(getString(R.string.open));
        }
    }

    @Override
    public void onSoundEffect(int id) {
        getMedia().playShortSound(id);
    }

    private void minusNumberOffRule() {
        Rule.getInstance().minusRuleNumber(Rule.getInstance().RULE_NORMAL);
        if (isEnableMainRuleBySecretKey)
            Rule.getInstance().minusRuleNumber(Rule.getInstance().RULE_MAIN);
    }

    /**
     * Set result from rule
     */
    private void setResult() {
        resultArrays = Rule.getInstance().getResult();
        drawPlay.setResultArrays(resultArrays);
        resultLayout.updateResult(bpResultArray[resultArrays[0]], bpResultArray[resultArrays[1]], bpResultArray[resultArrays[2]]);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgSound:
                Log.d(TAG, "btnSound clicked");
                switchSound();
                break;

            case R.id.imgMusic:
                if (lock.enable) {
                    lock.lock();
                    Log.d(TAG, "imgMusic clicked");
                    getMedia().changeBackgroundMusic();
                }
                break;

            case R.id.imgBack:
                Log.d(TAG, "btnBack clicked");
                onBackPressed();
                break;

            case R.id.imgAction:
                Task.startAliveBackGroundThread(new Runnable() {
                    @Override
                    public void run() {
                        setPreviousRule();
                        drawPlay.action();
                    }
                });
                break;

            case R.id.btnMain1:
                Log.d(TAG, "btnMain1 clicked");
                Task.startAliveBackGroundThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnableMainRuleBySecretKey && Rule.getInstance().getHideNumber() == 0) {
                            Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_1);
                            Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_MAIN);
                        } else {
                            Log.d(TAG, "Rule1 Main disabled");
                        }
                        drawPlay.action();
                    }
                });
                break;

            case R.id.btnMain2:
                Log.d(TAG, "btnMain2 clicked");
                Task.startAliveBackGroundThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnableMainRuleBySecretKey && Rule.getInstance().getHideNumber() == 0) {
                            Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_2);
                            Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_MAIN);
                        } else {
                            Log.d(TAG, "Rule1 Main disabled");
                        }
                        drawPlay.action();
                    }
                });
                break;
            case R.id.btnMain3:
                Log.d(TAG, "btnMain3 clicked");
                Task.startAliveBackGroundThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnableMainRuleBySecretKey && Rule.getInstance().getHideNumber() == 0) {
                            Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_3);
                            Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_MAIN);
                        } else {
                            Log.d(TAG, "Rule1 Main disabled");
                        }
                        drawPlay.action();
                    }
                });
                break;
        }
    }

    private void switchSound() {
        Log.d(TAG, "switchSound");
        final boolean isPlaySound = preferences.getBooleanValue(PrefValue.SETTING_SOUND, true);
        Runnable switchSoundRunnable = new Runnable() {
            @Override
            public void run() {
                Task.startNewBackGroundThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPlaySound) {
                            getMedia().playBackgroundMusic();
                        } else {
                            getMedia().stopBackgroundMusic();
                        }
                    }
                }));
            }
        };

        if (!isPlaySound) {
            imgSound.setImageBitmap(bpSoundOn);
            preferences.storeValue(PrefValue.SETTING_SOUND, true);
        } else {
            imgSound.setImageBitmap(bpSoundOff);
            preferences.storeValue(PrefValue.SETTING_SOUND, false);
        }
        Task.removeCallBack(switchSoundRunnable);
        Task.postDelay(switchSoundRunnable, 500);
    }

    @Override
    public void onStop() {
        presenter.stopAllThread();
        super.onStop();
    }

    @Override
    public void onNetworkChanged(boolean isEnable) {
        Log.d(TAG, "Network : " + isEnable);
        Rule.getInstance().setOnline(isEnable);
        updateSoundImage();

        //TODO ????
//        updateText(preferences.getStringValue(PrefValue.TEXT, PrefValue.DEFAULT_TEXT));
    }

    @Override
    public void startFireworks() {
        Log.d(TAG, "Start fireworks");
        int marginMin = (int) screenWidth / 3;
        int marginRightMax = (int) screenWidth * 2 / 3;
        int marginBottomMax = (int) (screenHeight - (screenWidth / 3));

        int marginLeft = ScreenUtil.getRandomNumber(marginMin, marginRightMax);
        int marginTop = ScreenUtil.getRandomNumber(marginMin, marginBottomMax);

        @SuppressLint("WrongViewCast")
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.findViewById(R.id.fireworks).getLayoutParams();
        params.setMargins(marginLeft, marginTop, 0, 0);
        v.findViewById(R.id.fireworks).setLayoutParams(params);

        int[] ballArrays = new int[]{R.drawable.ball_1, R.drawable.ball_2, R.drawable.ball_3, R.drawable.ball_4};

        new ParticleSystem(getActivity(), 10, ballArrays[ScreenUtil.getRandomNumber(0, ballArrays.length - 1)], 3000)
                .setSpeedByComponentsRange(-0.1f, 0.1f, -0.1f, 0.02f)
                .setAcceleration(0.000003f, 90)
                .setInitialRotationRange(0, 360)
                .setRotationSpeed(120)
                .setFadeOut(2000)
                .addModifier(new ScaleModifier(0f, 1.5f, 0, 1500))
                .oneShot(v.findViewById(R.id.fireworks), 10);
    }

    private void updateText(final String text) {
        Task.runOnUIThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                txtText.setText(presenter.updateText(text));
                txtText.setSelected(true);
                Log.d(TAG, "Text = " + txtText.getText());
            }
        });
    }

    @Override
    public void onDataChanged() {
        initUIFromFirebase();
    }
}
