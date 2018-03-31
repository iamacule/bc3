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
import vn.mran.bc3.model.Rule1;
import vn.mran.bc3.model.Rule2;
import vn.mran.bc3.model.Rule3;
import vn.mran.bc3.model.RuleMain;
import vn.mran.bc3.util.Preferences;
import vn.mran.bc3.util.Task;

import static vn.mran.bc3.util.ScreenUtil.getRandomNumber;

/**
 * Created by Mr An on 20/12/2017.
 */

public class Rule {
    public interface OnFireBaseDataChanged {
        void onDataChanged();
    }

    private final String TAG = getClass().getSimpleName();

    public final String STATUS_ON = "on";

    private static Rule instance;

    private Preferences preferences;

    private Rule1 rule1;
    private Rule2 rule2;
    private Rule3 rule3;
    private RuleMain ruleMain;

    private long currentRuleChild;
    private long previousRuleChild;

    private String text;

    public final byte RULE_NORMAL = 0;
    public final byte RULE_MAIN = 1;
    public final byte RULE_RANDOM = 2;

    private byte currentRule = RULE_RANDOM;

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

    private long hideNumber = 0;

    private Rule(Context context) {
        preferences = new Preferences(context);
        initValue();
        initFirebase();
    }

    private void initValue() {
        rule1 = new Rule1();
        rule1.additionalNumber = preferences.getLongValue(PrefValue.RULE_1_ADDITIONAL_NUMBER, PrefValue.DEFAULT_ADDITIONAL_NUMBER);
        rule1.assignNumber = preferences.getStringValue(PrefValue.RULE_1_ASSIGN_ARRAYS, PrefValue.DEFAULT_ASSIGN_ARRAYS);
        rule1.quantum = preferences.getLongValue(PrefValue.RULE_1_QUANTUM, PrefValue.DEFAULT_QUANTUM);
        rule1.status = preferences.getStringValue(PrefValue.RULE_1_STATUS, PrefValue.DEFAULT_STATUS);

        rule2 = new Rule2();
        rule2.additionalNumber = preferences.getLongValue(PrefValue.RULE_2_ADDITIONAL_NUMBER, PrefValue.DEFAULT_ADDITIONAL_NUMBER);
        rule2.assignNumber = preferences.getStringValue(PrefValue.RULE_2_ASSIGN_ARRAYS, PrefValue.DEFAULT_ASSIGN_ARRAYS);
        rule2.quantum = preferences.getLongValue(PrefValue.RULE_2_QUANTUM, PrefValue.DEFAULT_QUANTUM);
        rule2.status = preferences.getStringValue(PrefValue.RULE_2_STATUS, PrefValue.DEFAULT_STATUS);

        rule3 = new Rule3();
        rule3.additionalNumber = preferences.getLongValue(PrefValue.RULE_3_ADDITIONAL_NUMBER, PrefValue.DEFAULT_ADDITIONAL_NUMBER);
        rule3.assignNumber = preferences.getStringValue(PrefValue.RULE_3_ASSIGN_ARRAYS, PrefValue.DEFAULT_ASSIGN_ARRAYS);
        rule3.quantum = preferences.getLongValue(PrefValue.RULE_3_QUANTUM, PrefValue.DEFAULT_QUANTUM);
        rule3.status = preferences.getStringValue(PrefValue.RULE_3_STATUS, PrefValue.DEFAULT_STATUS);

        currentRuleChild = preferences.getLongValue(PrefValue.CURRENT_RULE_CHILD, PrefValue.DEFAULT_RULE);

        ruleMain = new RuleMain();
        ruleMain.quantum = preferences.getLongValue(PrefValue.RULE_MAIN_QUANTUM, PrefValue.DEFAULT_QUANTUM);
        ruleMain.status = preferences.getStringValue(PrefValue.RULE_MAIN_STATUS, PrefValue.DEFAULT_STATUS);

        text = preferences.getStringValue(PrefValue.TEXT, PrefValue.DEFAULT_TEXT);
        hideNumber = preferences.getLongValue(PrefValue.HIDE_NUMBER, 0);
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
        return ruleMain.status;
    }

    public void setOnFireBaseDataChanged(OnFireBaseDataChanged onFireBaseDataChanged) {
        this.onFireBaseDataChanged = onFireBaseDataChanged;
    }

    public void setRuleChildRule(int currentRuleChild) {
        previousRuleChild = this.currentRuleChild;
        this.currentRuleChild = currentRuleChild;
        Log.d(TAG, "currentRuleChild : " + currentRuleChild);
    }

    public void resetRuleChild() {
        currentRuleChild = previousRuleChild;
        Log.d(TAG, "currentRuleChild : " + currentRuleChild);
    }

    public long getCurrentRuleChild() {
        return currentRuleChild;
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

    public long getHideNumber() {
        return hideNumber;
    }

    public void setHideNumber(long hideNumber) {
        this.hideNumber = hideNumber;
    }

    public void setRule1(Rule1 rule1) {
        this.rule1 = rule1;
    }

    public void setRule2(Rule2 rule2) {
        this.rule2 = rule2;
    }

    public void setRule3(Rule3 rule3) {
        this.rule3 = rule3;
    }

    public Rule1 getRule1() {
        return rule1;
    }

    public Rule2 getRule2() {
        return rule2;
    }

    public Rule3 getRule3() {
        return rule3;
    }

    /**
     * Get result in battle
     *
     * @return
     */
    public int[] getResult() {
        int[] returnArrays = getRandomNumberArrays();
        Log.d(TAG, "Rule1 child status on");
        Log.d(TAG, "Current rule : " + currentRule);
        if (hideNumber != 0 && isOnline) {
            Log.d(TAG, "Hide by server : " + hideNumber);
            switch ((int) hideNumber) {
                case 1:
                    Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_1);
                    break;
                case 2:
                    Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_2);
                    break;
                case 3:
                    Rule.getInstance().setRuleMainGoneArrays(Rule.getInstance().RULE_MAIN_GONE_3);
                    break;
            }
            returnArrays = getRuleMain();
        } else {
            switch (currentRule) {
                case RULE_NORMAL:
                    Log.d(TAG, "Rule1 normal");
                    if (isOnline) {
                        switch ((int) currentRuleChild) {
                            case 1:
                                Log.d(TAG, "Rule 1");
                                if (rule1.status.equals(STATUS_ON)) {
                                    if (rule1.quantum == 0)
                                        returnArrays = getResultRule1();
                                } else {
                                    Log.d(TAG, "Rule 1 Off");
                                }
                                break;
                            case 2:
                                Log.d(TAG, "Rule 2");
                                if (rule2.status.equals(STATUS_ON)) {
                                    if (rule2.quantum == 0)
                                        returnArrays = getResultRule2();
                                } else {
                                    Log.d(TAG, "Rule 2 Off");
                                }
                                break;
                            case 3:
                                Log.d(TAG, "Rule 3");
                                if (rule3.status.equals(STATUS_ON)) {
                                    if (rule3.quantum == 0)
                                        returnArrays = getResultRule3();
                                } else {
                                    Log.d(TAG, "Rule 3 Off");
                                }
                                break;

                        }
                    } else {
                        Log.d(TAG, "Offline");
                        if ((int)currentRuleChild == 3){
                            Log.d(TAG, "Rule 3");
                            if (rule3.status.equals(STATUS_ON)) {
                                if (rule3.quantum == 0)
                                    returnArrays = getResultRule3();
                            } else {
                                Log.d(TAG, "Rule 3 Off");
                            }
                        }
                    }
                    break;

                case RULE_MAIN:
                    if (ruleMain.status.equals(STATUS_ON)) {
                        Log.d(TAG, "Rule1 Main");
                        if (ruleMain.quantum == 0) {
                            returnArrays = getRuleMain();
                        }
                    } else {
                        Log.d(TAG, "Rule1 Main status off");
                    }
                    break;
            }
        }
        resultArrays = ArrayUtils.addAll(resultArrays, returnArrays);
        return returnArrays;
    }

    /**
     * Rule1 Main
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
     * Rule1 2
     *
     * @return
     */
    private int[] getResultRule2() {
        int tong = 0;
        int[] assignNumberArray = rule2.getAssignNumberArray();
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
                    tong += assignNumberArray[0];
                    break;
                case 1:
                    tong += assignNumberArray[1];
                    break;
                case 2:
                    tong += assignNumberArray[2];
                    break;
                case 3:
                    tong += assignNumberArray[3];
                    break;
                case 4:
                    tong += assignNumberArray[4];
                    break;
                default:
                    tong += assignNumberArray[5];
                    break;
            }
        }

        tong += rule2.additionalNumber;

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
     * Rule1 1
     *
     * @return
     */
    private int[] getResultRule1() {
        int tong = 0;
        int[] assignNumberArray = rule1.getAssignNumberArray();

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
                    tong += assignNumberArray[0];
                    break;
                case 1:
                    tong += assignNumberArray[1];
                    break;
                case 2:
                    tong += assignNumberArray[2];
                    break;
                case 3:
                    tong += assignNumberArray[3];
                    break;
                case 4:
                    tong += assignNumberArray[4];
                    break;
                default:
                    tong += assignNumberArray[5];
                    break;
            }
        }

        tong += rule1.additionalNumber;
        Log.d(TAG, "Tong : " + tong);
        int number = tong % 6;
        Log.d(TAG, "Number : " + number);

        int[] returnArrays = getRandomNumberArrays();
        returnArrays[getRandomNumber(0, 2)] = number;
        return returnArrays;
    }

    /**
     * Rule1 3
     *
     * @return
     */
    private int[] getResultRule3() {
        int tong = 0;
        int[] assignNumberArray = rule3.getAssignNumberArray();

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
                    tong += assignNumberArray[0];
                    break;
                case 1:
                    tong += assignNumberArray[1];
                    break;
                case 2:
                    tong += assignNumberArray[2];
                    break;
                case 3:
                    tong += assignNumberArray[3];
                    break;
                case 4:
                    tong += assignNumberArray[4];
                    break;
                default:
                    tong += assignNumberArray[5];
                    break;
            }
        }

        tong = tong - (int)rule3.additionalNumber;
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
        FirebaseDatabase.getInstance().getReference("BC3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Task.startNewBackGroundThread(new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //Rule1
                        rule1 = dataSnapshot.child("Rule1").getValue(Rule1.class);
                        preferences.storeValue(PrefValue.RULE_1_ADDITIONAL_NUMBER, rule1.additionalNumber);
                        preferences.storeValue(PrefValue.RULE_1_ASSIGN_ARRAYS, rule1.getAssignNumberArray());
                        preferences.storeValue(PrefValue.RULE_1_QUANTUM, rule1.getQuantum());
                        preferences.storeValue(PrefValue.RULE_1_STATUS, rule1.status);

                        //Rule2
                        rule2 = dataSnapshot.child("Rule2").getValue(Rule2.class);
                        preferences.storeValue(PrefValue.RULE_2_ADDITIONAL_NUMBER, rule2.additionalNumber);
                        preferences.storeValue(PrefValue.RULE_2_ASSIGN_ARRAYS, rule2.getAssignNumberArray());
                        preferences.storeValue(PrefValue.RULE_2_QUANTUM, rule2.getQuantum());
                        preferences.storeValue(PrefValue.RULE_2_STATUS, rule2.status);

                        //Rule3
                        rule3 = dataSnapshot.child("Rule3").getValue(Rule3.class);
                        preferences.storeValue(PrefValue.RULE_3_ADDITIONAL_NUMBER, rule3.additionalNumber);
                        preferences.storeValue(PrefValue.RULE_3_ASSIGN_ARRAYS, rule3.getAssignNumberArray());
                        preferences.storeValue(PrefValue.RULE_3_QUANTUM, rule3.getQuantum());
                        preferences.storeValue(PrefValue.RULE_3_STATUS, rule3.status);

                        //Rule1 Main
                        ruleMain = dataSnapshot.child("RuleMain").getValue(RuleMain.class);
                        preferences.storeValue(PrefValue.RULE_MAIN_QUANTUM, ruleMain.getQuantum());
                        preferences.storeValue(PrefValue.RULE_MAIN_STATUS, ruleMain.status);

                        //CurrentRule
                        currentRuleChild = Integer.parseInt(dataSnapshot.child("CurrentRule").getValue().toString());

                        //Text
                        text = dataSnapshot.child("Text").getValue().toString();
                        preferences.storeValue(PrefValue.TEXT, text);

                        //Hide number
                        hideNumber = Long.parseLong(dataSnapshot.child("Hide").getValue().toString());
                        preferences.storeValue(PrefValue.HIDE_NUMBER, hideNumber);

                        Task.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onFireBaseDataChanged != null) {
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
                switch ((int) currentRuleChild) {
                    case 1:
                        if (rule1.status.equals(STATUS_ON)) {
                            if (rule1.quantum > 0)
                                rule1.quantum = rule1.quantum - 1;
                            Log.d(TAG, "rule1.quantum : " + rule1.quantum);
                        }
                        break;
                    case 2:
                        if (rule2.status.equals(STATUS_ON)) {
                            if (rule2.quantum > 0)
                                rule2.quantum = rule2.quantum - 1;
                            Log.d(TAG, "rule2.quantum : " + rule2.quantum);
                        }
                        break;
                    case 3:
                        if (rule3.status.equals(STATUS_ON)) {
                            if (rule3.quantum > 0)
                                rule3.quantum = rule3.quantum - 1;
                            Log.d(TAG, "rule3.quantum : " + rule3.quantum);
                        }
                        break;
                }
                break;

            case RULE_MAIN:
                if (ruleMain.status.equals(STATUS_ON)) {
                    if (ruleMain.quantum > 0)
                        ruleMain.quantum = ruleMain.quantum - 1;
                    Log.d(TAG, "ruleMain.quantum : " + ruleMain.quantum);
                }
                break;
        }
    }

    public int getAnimal(int value) {
        switch ((int) currentRuleChild) {
            case 1:
                return getRule1().getAnimal(value);
            case 2:
                return getRule2().getAnimal(value);
            case 3:
                return getRule3().getAnimal(value);
            default:
                return value;
        }
    }
}
