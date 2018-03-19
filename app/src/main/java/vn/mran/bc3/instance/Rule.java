package vn.mran.bc3.instance;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import vn.mran.bc3.constant.PrefValue;
import vn.mran.bc3.helper.Log;
import vn.mran.bc3.model.RuleChild;
import vn.mran.bc3.model.RuleMain;
import vn.mran.bc3.util.Preferences;
import vn.mran.bc3.util.Task;

import static vn.mran.bc3.util.ScreenUtil.getRandomNumber;

/**
 * Created by Mr An on 20/12/2017.
 */

public class Rule {
    public interface OnFireBaseDataChanged {
        void onTextChanged(String TEXT);

        void onDataChanged();
    }

    private final String TAG = getClass().getSimpleName();

    public final String STATUS_ON = "on";

    private static Rule instance;

    private Preferences preferences;

    private int ruleChildAdditionalNumber;
    private int ruleChildAssignNum1;
    private int ruleChildAssignNum2;
    private int ruleChildAssignNum3;
    private int ruleChildAssignNum4;
    private int ruleChildAssignNum5;
    private int ruleChildAssignNum6;
    private int ruleChildQuantum;
    private int ruleChildRule;
    private int previousRule;
    private String ruleChildStatus;

    private int ruleMainQuantum;
    private String ruleMainStatus;

    private String text;

    public final byte RULE_NORMAL = 0;
    public final byte RULE_MAIN = 1;
    private byte currentRule = RULE_NORMAL;

    //Bau, cua
    public final int[] RULE_MAIN_GONE_1 = new int[]{0, 1};

    //Tom ca
    public final int[] RULE_MAIN_GONE_2 = new int[]{2, 3};

    //Ga, nai
    public final int[] RULE_MAIN_GONE_3 = new int[]{4, 5};

    private int[] ruleMainGoneArrays;

    private int[] resultArrays = new int[]{};

    private OnFireBaseDataChanged onFireBaseDataChanged;

    private boolean isOnline = false;

    private Rule(Context context) {
        preferences = new Preferences(context);
        initValue();
        initFirebase();
    }

    private void initValue() {
        ruleChildAdditionalNumber = preferences.getIntValue(PrefValue.RULE_CHILD_ADDITIONAL_NUMBER, PrefValue.DEFAULT_ADDITIONAL_NUMBER);
        ruleChildAssignNum1 = preferences.getIntValue(PrefValue.RULE_CHILD_NUM_1, PrefValue.DEFAULT_ASSIGN_NUM_1);
        ruleChildAssignNum2 = preferences.getIntValue(PrefValue.RULE_CHILD_NUM_2, PrefValue.DEFAULT_ASSIGN_NUM_2);
        ruleChildAssignNum3 = preferences.getIntValue(PrefValue.RULE_CHILD_NUM_3, PrefValue.DEFAULT_ASSIGN_NUM_3);
        ruleChildAssignNum4 = preferences.getIntValue(PrefValue.RULE_CHILD_NUM_4, PrefValue.DEFAULT_ASSIGN_NUM_4);
        ruleChildAssignNum5 = preferences.getIntValue(PrefValue.RULE_CHILD_NUM_5, PrefValue.DEFAULT_ASSIGN_NUM_5);
        ruleChildAssignNum6 = preferences.getIntValue(PrefValue.RULE_CHILD_NUM_6, PrefValue.DEFAULT_ASSIGN_NUM_6);
        ruleChildQuantum = preferences.getIntValue(PrefValue.RULE_CHILD_QUANTUM, PrefValue.DEFAULT_QUANTUM);
        ruleChildRule = preferences.getIntValue(PrefValue.RULE_CHILD_RULE, PrefValue.DEFAULT_RULE);
        ruleChildStatus = preferences.getStringValue(PrefValue.RULE_CHILD_STATUS, PrefValue.DEFAULT_STATUS);

        ruleMainQuantum = preferences.getIntValue(PrefValue.RULE_MAIN_QUANTUM, PrefValue.DEFAULT_QUANTUM);
        ruleMainStatus = preferences.getStringValue(PrefValue.RULE_MAIN_STATUS, PrefValue.DEFAULT_STATUS);

        text = preferences.getStringValue(PrefValue.TEXT, PrefValue.DEFAULT_TEXT);
    }

    public static void init(Context context) {
        instance = new Rule(context);
    }

    public static Rule getInstance() {
        return instance;
    }

    public void setCurrentRule(byte currentRule) {
        this.currentRule = currentRule;
    }

    public void setRuleMainGoneArrays(int[] array) {
        this.ruleMainGoneArrays = array;
    }

    public String getRuleMainStatus() {
        return ruleMainStatus;
    }

    public void setOnFireBaseDataChanged(OnFireBaseDataChanged onFireBaseDataChanged) {
        this.onFireBaseDataChanged = onFireBaseDataChanged;
    }

    public void setRuleChildRule(int ruleChildRule) {
        previousRule = this.ruleChildRule;
        this.ruleChildRule = ruleChildRule;
        Log.d(TAG, "ruleChildRule : " + ruleChildRule);
    }

    public void resetRuleChild() {
        ruleChildRule = previousRule;
        Log.d(TAG, "ruleChildRule : " + ruleChildRule);
    }

    public int getRuleChildRule() {
        return ruleChildRule;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getText() {
        return text;
    }

    /**
     * Get result in battle
     *
     * @return
     */
    public int[] getResult() {
        int[] returnArrays = getRandomNumberArrays();
        Log.d(TAG, "Rule child status on");
        Log.d(TAG, "Current rule : " + currentRule);
        switch (currentRule) {
            case RULE_NORMAL:
                Log.d(TAG, "Rule normal");
                if (isOnline) {
                    if (ruleChildStatus.equals(STATUS_ON)) {
                        if (ruleChildQuantum == 0) {
                            switch (ruleChildRule) {
                                case 1:
                                    Log.d(TAG, "Rule 1");
                                    returnArrays = getRule1();
                                    break;
                                case 2:
                                    Log.d(TAG, "Rule 2");
                                    returnArrays = getRule2();
                                    break;
                                case 3:
                                    Log.d(TAG, "Rule 3");
                                    returnArrays = getRule3();
                                    break;

                            }
                        }
                    } else {
                        Log.d(TAG, "Rule child status off");
                    }
                } else {
                    Log.d(TAG, "Offline");
                }
                break;

            case RULE_MAIN:
                if (ruleMainStatus.equals(STATUS_ON)) {
                    Log.d(TAG, "Rule Main");
                    if (ruleMainQuantum == 0) {
                        returnArrays = getRuleMain();
                    }
                } else {
                    Log.d(TAG, "Rule Main status off");
                }
                break;
        }


        resultArrays = ArrayUtils.addAll(resultArrays, returnArrays);
        return returnArrays;
    }

    /**
     * Rule Main
     *
     * @return
     */
    private int[] getRuleMain() {
        int[] resultArray = new int[3];
        for (int i = 0; i < resultArray.length; i++) {
            while (true) {
                int value = getRandomAnimalPosition();
                if (value != ruleMainGoneArrays[0] && value != ruleMainGoneArrays[1]) {
                    resultArray[i] = value;
                    break;
                }
            }
        }
        return resultArray;
    }

    /**
     * Rule 2
     *
     * @return
     */
    private int[] getRule2() {
        int tong = 0;

        int range = 3;
        if (resultArrays.length == 0) {
            Log.d(TAG, "resultArrays length = 0");
            return getRandomNumberArrays();
        } else if (resultArrays.length >= 6) {
            range = 6;
        }
        for (int i = resultArrays.length - 1; i >= resultArrays.length - range; i--) {
            Log.d(TAG, "Result array sub : " + resultArrays[i]);
            switch (resultArrays[i]) {
                case 0:
                    tong += ruleChildAssignNum1;
                    break;
                case 1:
                    tong += ruleChildAssignNum2;
                    break;
                case 2:
                    tong += ruleChildAssignNum3;
                    break;
                case 3:
                    tong += ruleChildAssignNum4;
                    break;
                case 4:
                    tong += ruleChildAssignNum5;
                    break;
                default:
                    tong += ruleChildAssignNum6;
                    break;
            }
        }

        tong += ruleChildAdditionalNumber;

        int min = Integer.parseInt((new SimpleDateFormat("mm").format(new Date())).toString());

        if (min < 10)
            tong += 1;
        if (min >= 10 && min < 20)
            tong += 2;
        if (min >= 20 && min < 30)
            tong += 3;
        if (min >= 30 && min < 40)
            tong += 4;
        if (min >= 40 && min < 50)
            tong += 5;
        if (min >= 50 && min < 60)
            tong += 6;

        Log.d(TAG, "Tong : " + tong);
        int number = tong % 6;
        Log.d(TAG, "Number : " + number);

        int[] returnArrays = getRandomNumberArrays();
        returnArrays[getRandomNumber(0, 2)] = number;
        return returnArrays;
    }

    /**
     * Rule 1
     *
     * @return
     */
    private int[] getRule1() {
        int tong = 0;

        int range = 3;
        if (resultArrays.length <= 3) {
            Log.d(TAG, "resultArrays length = 0");
            return getRandomNumberArrays();
        } else if (resultArrays.length >= 6) {
            range = 6;
        }
        for (int i = resultArrays.length - 4; i >= resultArrays.length - range; i--) {
            Log.d(TAG, "Result array sub : " + resultArrays[i]);
            switch (resultArrays[i]) {
                case 0:
                    tong += ruleChildAssignNum1;
                    break;
                case 1:
                    tong += ruleChildAssignNum2;
                    break;
                case 2:
                    tong += ruleChildAssignNum3;
                    break;
                case 3:
                    tong += ruleChildAssignNum4;
                    break;
                case 4:
                    tong += ruleChildAssignNum5;
                    break;
                default:
                    tong += ruleChildAssignNum6;
                    break;
            }
        }

        tong += ruleChildAdditionalNumber;
        Log.d(TAG, "Tong : " + tong);
        int number = tong % 6;
        Log.d(TAG, "Number : " + number);

        int[] returnArrays = getRandomNumberArrays();
        returnArrays[getRandomNumber(0, 2)] = number;
        return returnArrays;
    }

    /**
     * Rule 3
     *
     * @return
     */
    private int[] getRule3() {
        int tong = 0;

        int range = 3;
        if (resultArrays.length == 0) {
            Log.d(TAG, "resultArrays length = 0");
            return getRandomNumberArrays();
        } else if (resultArrays.length >= 6) {
            range = 6;
        }
        for (int i = resultArrays.length - 1; i >= resultArrays.length - range; i--) {
            Log.d(TAG, "Result array sub : " + resultArrays[i]);
            switch (resultArrays[i]) {
                case 0:
                    tong += ruleChildAssignNum1;
                    break;
                case 1:
                    tong += ruleChildAssignNum2;
                    break;
                case 2:
                    tong += ruleChildAssignNum3;
                    break;
                case 3:
                    tong += ruleChildAssignNum4;
                    break;
                case 4:
                    tong += ruleChildAssignNum5;
                    break;
                default:
                    tong += ruleChildAssignNum6;
                    break;
            }
        }

        tong += ruleChildAdditionalNumber;
        Log.d(TAG, "Tong : " + tong);
        int number = tong % 6;
        Log.d(TAG, "Number : " + number);

        int[] returnArrays = getRandomNumberArrays();
        returnArrays[getRandomNumber(0, 2)] = number;
        return returnArrays;
    }

    /**
     * Initialize Firebase realtime database
     */
    private void initFirebase() {
        FirebaseDatabase.getInstance().getReference("BC2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Task.startNewBackGroundThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Rule child
                        RuleChild ruleChild = dataSnapshot.child("RuleChild").getValue(RuleChild.class);
                        Log.d(TAG, "[RuleChild] [additionalNumber : " + ruleChild.additionalNumber + " ]");
                        Log.d(TAG, "[RuleChild] [assignNumber : " + ruleChild.assignNumber + " ]");
                        Log.d(TAG, "[RuleChild] [quantum : " + ruleChild.quantum + " ]");
                        Log.d(TAG, "[RuleChild] [rule : " + ruleChild.rule + " ]");
                        Log.d(TAG, "[RuleChild] [status : " + ruleChild.status + " ]");

                        ruleChildAdditionalNumber = ruleChild.getAdditionalNumber();
                        preferences.storeValue(PrefValue.RULE_CHILD_ADDITIONAL_NUMBER, ruleChildAdditionalNumber);

                        int[] ruleChildAssignNumArray = ruleChild.getAssignNumberArray();
                        ruleChildAssignNum1 = ruleChildAssignNumArray[0];
                        ruleChildAssignNum2 = ruleChildAssignNumArray[1];
                        ruleChildAssignNum3 = ruleChildAssignNumArray[2];
                        ruleChildAssignNum4 = ruleChildAssignNumArray[3];
                        ruleChildAssignNum5 = ruleChildAssignNumArray[4];
                        ruleChildAssignNum6 = ruleChildAssignNumArray[5];
                        preferences.storeValue(PrefValue.RULE_CHILD_NUM_1, ruleChildAssignNum1);
                        preferences.storeValue(PrefValue.RULE_CHILD_NUM_2, ruleChildAssignNum2);
                        preferences.storeValue(PrefValue.RULE_CHILD_NUM_3, ruleChildAssignNum3);
                        preferences.storeValue(PrefValue.RULE_CHILD_NUM_4, ruleChildAssignNum4);
                        preferences.storeValue(PrefValue.RULE_CHILD_NUM_5, ruleChildAssignNum5);
                        preferences.storeValue(PrefValue.RULE_CHILD_NUM_6, ruleChildAssignNum6);

                        ruleChildQuantum = ruleChild.getQuantum();
                        preferences.storeValue(PrefValue.RULE_CHILD_QUANTUM, ruleChildQuantum);

                        ruleChildRule = ruleChild.getRule();
                        preferences.storeValue(PrefValue.RULE_CHILD_RULE, ruleChildRule);

                        ruleChildStatus = ruleChild.status;
                        preferences.storeValue(PrefValue.RULE_CHILD_STATUS, ruleChildStatus);

                        //Rule Main
                        RuleMain ruleMain = dataSnapshot.child("RuleMain").getValue(RuleMain.class);
                        Log.d(TAG, "[RuleMain] [quantum : " + ruleMain.quantum + " ]");
                        Log.d(TAG, "[RuleMain] [status : " + ruleMain.status + " ]");

                        ruleMainQuantum = ruleMain.getQuantum();
                        preferences.storeValue(PrefValue.RULE_MAIN_QUANTUM, ruleMainQuantum);

                        ruleMainStatus = ruleMain.status;
                        preferences.storeValue(PrefValue.RULE_MAIN_STATUS, ruleMainStatus);

                        //Text
                        final String text = dataSnapshot.child("Text").getValue().toString();
                        preferences.storeValue(PrefValue.TEXT, text);
                        Log.d(TAG, "Text : " + text);

                        Task.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onFireBaseDataChanged != null) {
                                    onFireBaseDataChanged.onTextChanged(text);
                                    onFireBaseDataChanged.onDataChanged();
                                }
                            }
                        });
                    }
                }));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private int[] getRandomNumberArrays() {
        Random random = new Random();
        return new int[]{random.nextInt((5 - 0) + 1),
                random.nextInt((5 - 0) + 1),
                random.nextInt((5 - 0) + 1)};
    }

    private int getRandomAnimalPosition() {
        return new Random().nextInt((5 - 0) + 1);
    }

    public void minusRuleNumber(byte ruleType) {

        switch (ruleType) {
            case RULE_NORMAL:
                if (ruleChildStatus.equals(STATUS_ON)) {
                    if (ruleChildQuantum > 0)
                        ruleChildQuantum = ruleChildQuantum - 1;
                    Log.d(TAG, "ruleChildQuantum : " + ruleChildQuantum);
                }
                break;

            case RULE_MAIN:
                if (ruleMainStatus.equals(STATUS_ON)) {
                    if (ruleMainQuantum > 0)
                        ruleMainQuantum = ruleMainQuantum - 1;
                    Log.d(TAG, "ruleMainQuantum : " + ruleMainQuantum);
                }
                break;
        }
    }
}
