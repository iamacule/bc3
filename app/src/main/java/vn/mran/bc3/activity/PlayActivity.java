package vn.mran.bc3.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import vn.mran.bc3.R;
import vn.mran.bc3.base.BaseActivity;
import vn.mran.bc3.constant.PrefValue;
import vn.mran.bc3.draw.DrawParallaxStar;
import vn.mran.bc3.draw.DrawPlay;
import vn.mran.bc3.helper.Log;
import vn.mran.bc3.helper.OnDoubleClickListener;
import vn.mran.bc3.instance.Media;
import vn.mran.bc3.instance.Rule;
import vn.mran.bc3.mvp.presenter.PlayPresenter;
import vn.mran.bc3.mvp.view.PlayView;
import vn.mran.bc3.util.MyAnimation;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.ScreenUtil;
import vn.mran.bc3.util.Task;
import vn.mran.bc3.util.TouchEffect;
import vn.mran.bc3.util.toast.Boast;
import vn.mran.bc3.widget.AnimalChooserLayout;
import vn.mran.bc3.widget.CustomTextView;

/**
 * Created by Mr An on 18/12/2017.
 */

public class PlayActivity extends BaseActivity implements DrawPlay.OnDrawLidUpdate, View.OnClickListener, PlayView, Rule.OnFireBaseDataChanged {
    private static final int MONEY_VALUE = 100;
    private final String TAG = getClass().getSimpleName();

    private PlayPresenter presenter;

    private ImageView imgAction;

    private ImageView imgSound;
    private ImageView imgBack;

    private CustomTextView txtAction;
    private CustomTextView txtTitle;
    private CustomTextView txtMoney;

    private TextView txtTime;

    private DrawPlay drawPlay;

    private AnimalChooserLayout animalChooserLayout;

    private Bitmap bpSoundOn;
    private Bitmap bpSoundOff;
    private Bitmap bpBack;

    private Bitmap[] bpChooserArray = new Bitmap[6];
    private Bitmap[] bpResultArray = new Bitmap[6];
    private int[] resultArrays;

    private boolean isEnableMainRuleBySecretKey = false;

    private int currentMoney = 0;

    private ParticleSystem ps;

    @Override
    public void initLayout() {
        hideStatusBar();

        animalChooserLayout = new AnimalChooserLayout(getWindow().getDecorView().getRootView(), (int) screenWidth);

        imgAction = findViewById(R.id.imgAction);
        imgSound = findViewById(R.id.imgSound);
        imgBack = findViewById(R.id.imgBack);
        txtAction = findViewById(R.id.txtAction);
        txtTitle = findViewById(R.id.txtTitle);
        txtMoney = findViewById(R.id.txtMoney);
        txtTime = findViewById(R.id.txtTime);
        drawPlay = findViewById(R.id.drawPlay);
    }

    @Override
    public void initValue() {
        Rule.getInstance().setOnFireBaseDataChanged(this);
        presenter = new PlayPresenter(this);
        ((DrawParallaxStar) findViewById(R.id.drawParallaxStar)).setStarSize((int) screenWidth / 10);

        imgAction.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.button_background), screenWidth / 3));

        TouchEffect.addAlpha(imgAction);
        TouchEffect.addAlpha(imgBack);
        TouchEffect.addAlpha(imgSound);

        bpChooserArray[0] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.bau), screenWidth / 4);
        bpChooserArray[1] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.cua), screenWidth / 4);
        bpChooserArray[2] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.tom), screenWidth / 4);
        bpChooserArray[3] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ca), screenWidth / 4);
        bpChooserArray[4] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ga), screenWidth / 4);
        bpChooserArray[5] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.nai), screenWidth / 4);

        bpResultArray[0] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.bau), screenWidth / 4);
        bpResultArray[1] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.cua), screenWidth / 4);
        bpResultArray[2] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.tom), screenWidth / 4);
        bpResultArray[3] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ca), screenWidth / 4);
        bpResultArray[4] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ga), screenWidth / 4);
        bpResultArray[5] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.nai), screenWidth / 4);

        initUIFromFirebase();

        updateMoneyDetail();

        animalChooserLayout.setOnAnimalChooseListener(new AnimalChooserLayout.OnAnimalChooseListener() {
            @Override
            public void onChoose(int[] valueArrays) {
                Log.d(TAG, "Changed");
                presenter.onChoose(valueArrays, resultArrays);
            }

            @Override
            public void onError() {
                Boast.makeText(PlayActivity.this, getString(R.string.error_choose_money_type));
            }
        });
    }

    private void updateMoneyDetail() {
        checkingMoney();
        if (preferences.getIntValue(PrefValue.MONEY, PrefValue.DEFAULT_MONEY) >= MONEY_VALUE) {
            presenter.setCurrentMoney(MONEY_VALUE);
            animalChooserLayout.setMaxValue(currentMoney / MONEY_VALUE);
            animalChooserLayout.reset();
        } else {
            presenter.setCurrentMoney(0);
            animalChooserLayout.setMaxValue(0);
            animalChooserLayout.reset();
        }
    }

    private void checkingMoney() {
        currentMoney = preferences.getIntValue(PrefValue.MONEY, PrefValue.DEFAULT_MONEY);
        txtMoney.setText(presenter.updateMoneyValue(currentMoney));

        presenter.setEnablePlusMoney(currentMoney < 5000);
    }

    private void initUIFromFirebase() {
        //Text
        updateText(Rule.getInstance().getText());

        //Main rule
        String ruleMainStatus = preferences.getStringValue(PrefValue.RULE_MAIN_PLAY_STATUS, PrefValue.DEFAULT_STATUS);
        if (ruleMainStatus.equals(Rule.getInstance().STATUS_ON)) {
            if (isEnableMainRuleBySecretKey) {
                bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back_main_on_secret_on), screenWidth / 10));
            } else {
                bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back_main_on_secret_off), screenWidth / 10));
            }
        } else {
            bpBack = (ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.back), screenWidth / 10));

        }
        imgBack.setImageBitmap(bpBack);

        updateSoundImage();
    }

    private void updateSoundImage() {
        //Child rule

        bpSoundOn = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_on), screenWidth / 10);
        bpSoundOff = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.sound_off), screenWidth / 10);

        if (Rule.getInstance().isOnline()) {
            String ruleChildStatus = preferences.getStringValue(PrefValue.RULE_CHILD_PLAY_STATUS, PrefValue.DEFAULT_STATUS);
            if (ruleChildStatus.equals(Rule.getInstance().STATUS_ON)) {
                switch (Rule.getInstance().getRuleChildRule()) {
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
    }

    @Override
    public void initAction() {
        drawPlay.initialize((int) screenWidth, (int) screenHeight);
        drawPlay.setOnDrawLidUpdate(this);

        imgAction.setOnClickListener(this);
        findViewById(R.id.btnMain1).setOnClickListener(this);
        findViewById(R.id.btnMain2).setOnClickListener(this);
        findViewById(R.id.btnMain3).setOnClickListener(this);
        imgSound.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                switch (v.getId()) {
                    case R.id.btnRuleMain:
                        Log.d(TAG, "btnEnableRuleMain clicked");
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
                        break;
                    case R.id.btnRule3:
                        Log.d(TAG, " Rule 3 clicked");
                        if (Rule.getInstance().getRuleChildRule() != 3) {
                            Rule.getInstance().setRuleChildRule(3);
                        } else {
                            Rule.getInstance().resetRuleChild();
                        }
                        updateSoundImage();
                        break;
                }
            }
        };
        findViewById(R.id.btnRule3).setOnClickListener(onDoubleClickListener);
        animalChooserLayout.getBtnEnableRuleMain().setOnClickListener(onDoubleClickListener);

        //Set result at first time
        setResult();
    }

    private void setPreviousRule() {
        Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_NORMAL);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_play;
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
            animalChooserLayout.showResult();
            presenter.executeResult();
        } else {
            animalChooserLayout.hideResult();
            drawPlay.startAnimation(MyAnimation.shake(this));
            txtAction.setText(getString(R.string.open));
        }
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
        animalChooserLayout.updateResult(bpResultArray[resultArrays[0]], bpResultArray[resultArrays[1]], bpResultArray[resultArrays[2]]);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgSound:
                Log.d(TAG, "btnSound clicked");
                switchSound();
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
                        if (isEnableMainRuleBySecretKey) {
                            Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_1);
                            Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_MAIN);
                        } else {
                            Log.d(TAG, "Rule Main disabled");
                        }
                        drawPlay.action();
                    }
                });
                break;

            case R.id.btnMain2:
                Task.startAliveBackGroundThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnableMainRuleBySecretKey) {
                            Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_2);
                            Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_MAIN);
                        } else {
                            Log.d(TAG, "Rule Main disabled");
                        }
                        drawPlay.action();
                    }
                });
                break;
            case R.id.btnMain3:
                Task.startAliveBackGroundThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnableMainRuleBySecretKey) {
                            Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_3);
                            Rule.getInstance().setCurrentRule(Rule.getInstance().RULE_MAIN);
                        } else {
                            Log.d(TAG, "Rule Main disabled");
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
                            Media.playBackgroundMusic(getApplicationContext());
                        } else {
                            Media.stopBackgroundMusic();
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
    public void onBackPressed() {
        presenter.stopAllThread();
        super.onBackPressed();
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
    public void onTimeChanged(String value) {
        txtTime.setText(value);
    }

    @Override
    public void onResultExecute(int tong) {
        Log.d(TAG, "CurrentMoney : " + currentMoney);
        currentMoney = currentMoney + tong;
        if (currentMoney <= 0) {
            currentMoney = 0;
            animalChooserLayout.setMaxValue(0);
            presenter.setCurrentMoney(0);
        }
        preferences.storeValue(PrefValue.MONEY, currentMoney);

        if (tong < 0) {
            Boast.makeText(this, presenter.updateMoneyValue(tong), Color.RED);
        } else if (tong > 0) {
            Boast.makeText(this, "+" + presenter.updateMoneyValue(tong), Color.GREEN);
        }

        Task.postDelay(new Runnable() {
            @Override
            public void run() {
                updateMoneyDetail();
            }
        }, 500);
    }

    @Override
    public void onAddMoney() {
        currentMoney = currentMoney + 10;
        preferences.storeValue(PrefValue.MONEY, currentMoney);
        checkingMoney();
    }

    @Override
    public void startFireworks() {
        Log.d(TAG, "Start fireworks");
        int marginMin = (int) screenWidth / 3;
        int marginRightMax = (int) screenWidth * 2 / 3;
        int marginBottomMax = (int) (screenHeight - (screenWidth / 3));

        int marginLeft = ScreenUtil.getRandomNumber(marginMin, marginRightMax);
        int marginTop = ScreenUtil.getRandomNumber(marginMin, marginBottomMax);

        @SuppressLint("WrongViewCast") FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) findViewById(R.id.fireworks).getLayoutParams();
        params.setMargins(marginLeft, marginTop, 0, 0);
        findViewById(R.id.fireworks).setLayoutParams(params);

        ParticleSystem ps = new ParticleSystem(this, 100, R.drawable.fire_slot, 800);
        ps.setScaleRange(0.7f, 1.3f);
        ps.setSpeedRange(0.1f, 0.25f);
        ps.setRotationSpeedRange(90, 180);
        ps.setFadeOut(200, new AccelerateInterpolator());
        ps.oneShot(findViewById(R.id.fireworks), 70);

        ParticleSystem ps2 = new ParticleSystem(this, 100, R.drawable.fire_slot_2, 800);
        ps2.setScaleRange(0.7f, 1.3f);
        ps2.setSpeedRange(0.1f, 0.25f);
        ps.setRotationSpeedRange(90, 180);
        ps2.setFadeOut(200, new AccelerateInterpolator());
        ps2.oneShot(findViewById(R.id.fireworks), 70);
    }

    @Override
    public void onTextChanged(String text) {
        updateText(text);
    }

    private void updateText(final String text) {
        Task.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                txtTitle.setText(presenter.updateText(text));
            }
        });
    }

    @Override
    public void onDataChanged() {
        initUIFromFirebase();
    }
}
