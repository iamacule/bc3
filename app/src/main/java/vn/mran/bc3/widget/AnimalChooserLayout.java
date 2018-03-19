package vn.mran.bc3.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import vn.mran.bc3.R;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.TouchEffect;

/**
 * Created by Mr An on 01/01/2018.
 */

public class AnimalChooserLayout implements View.OnClickListener {

    public interface OnAnimalChooseListener {
        void onChoose(int[] valueArrays);

        void onError();
    }

    private final int IMAGE_WIDTH = 25;

    private OnAnimalChooseListener onAnimalChooseListener;

    private View view;

    private ImageView imgNai;
    private ImageView imgBau;
    private ImageView imgGa;
    private ImageView imgCa;
    private ImageView imgCua;
    private ImageView imgTom;

    private ImageView imgCoinNai;
    private ImageView imgCoinBau;
    private ImageView imgCoinGa;
    private ImageView imgCoinCa;
    private ImageView imgCoinCua;
    private ImageView imgCoinTom;

    private CustomTextView txtNai;
    private CustomTextView txtBau;
    private CustomTextView txtGa;
    private CustomTextView txtCa;
    private CustomTextView txtCua;
    private CustomTextView txtTom;

    private FrameLayout frResult;
    private LinearLayout lnChooser;

    private int valueNai = 0;
    private int valueBau = 0;
    private int valueGa = 0;
    private int valueCa = 0;
    private int valueCua = 0;
    private int valueTom = 0;

    private int maxValue = 0;
    private int currentValue = 0;

    private LinearLayout btnEnableRuleMain;

    public void setOnAnimalChooseListener(OnAnimalChooseListener onAnimalChooseListener) {
        this.onAnimalChooseListener = onAnimalChooseListener;
    }

    public AnimalChooserLayout(View view, int screenWidth) {
        this.view = view;

        lnChooser = view.findViewById(R.id.lnChooser);
        frResult = view.findViewById(R.id.frResult);


        btnEnableRuleMain = (LinearLayout) view.findViewById(R.id.btnRuleMain);

        imgNai = view.findViewById(R.id.imgNai);
        imgBau = view.findViewById(R.id.imgBau);
        imgGa = view.findViewById(R.id.imgGa);
        imgCa = view.findViewById(R.id.imgCa);
        imgCua = view.findViewById(R.id.imgCua);
        imgTom = view.findViewById(R.id.imgTom);

        imgCoinNai = view.findViewById(R.id.imgCoinNai);
        imgCoinBau = view.findViewById(R.id.imgCoinBau);
        imgCoinGa = view.findViewById(R.id.imgCoinGa);
        imgCoinCa = view.findViewById(R.id.imgCoinCa);
        imgCoinCua = view.findViewById(R.id.imgCoinCua);
        imgCoinTom = view.findViewById(R.id.imgCoinTom);

        txtNai = view.findViewById(R.id.txtNai);
        txtBau = view.findViewById(R.id.txtBau);
        txtGa = view.findViewById(R.id.txtGa);
        txtCa = view.findViewById(R.id.txtCa);
        txtCua = view.findViewById(R.id.txtCua);
        txtTom = view.findViewById(R.id.txtTom);

        imgNai.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.nai), screenWidth * IMAGE_WIDTH / 100));
        imgBau.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.bau), screenWidth * IMAGE_WIDTH / 100));
        imgGa.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.ga), screenWidth * IMAGE_WIDTH / 100));
        imgCa.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.ca), screenWidth * IMAGE_WIDTH / 100));
        imgCua.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.cua), screenWidth * IMAGE_WIDTH / 100));
        imgTom.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.tom), screenWidth * IMAGE_WIDTH / 100));

        imgCoinBau.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.coin), screenWidth / 10));
        imgCoinCa.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.coin), screenWidth / 10));
        imgCoinCua.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.coin), screenWidth / 10));
        imgCoinGa.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.coin), screenWidth / 10));
        imgCoinNai.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.coin), screenWidth / 10));
        imgCoinTom.setImageBitmap(ResizeBitmap.resize(BitmapFactory.decodeResource(view.getResources(), R.drawable.coin), screenWidth / 10));

        imgNai.setOnClickListener(this);
        imgBau.setOnClickListener(this);
        imgGa.setOnClickListener(this);
        imgCa.setOnClickListener(this);
        imgCua.setOnClickListener(this);
        imgTom.setOnClickListener(this);

        imgCoinTom.setVisibility(View.GONE);
        imgCoinNai.setVisibility(View.GONE);
        imgCoinGa.setVisibility(View.GONE);
        imgCoinCua.setVisibility(View.GONE);
        imgCoinCa.setVisibility(View.GONE);
        imgCoinBau.setVisibility(View.GONE);

        TouchEffect.addAlpha(imgBau);
        TouchEffect.addAlpha(imgNai);
        TouchEffect.addAlpha(imgGa);
        TouchEffect.addAlpha(imgCua);
        TouchEffect.addAlpha(imgTom);
        TouchEffect.addAlpha(imgCa);
    }

    @Override
    public void onClick(View view) {
        if (maxValue <= 0)
            onAnimalChooseListener.onError();
        else {
            if (currentValue < maxValue) {
                currentValue = currentValue + 1;
                switch (view.getId()) {
                    case R.id.imgNai:
                        valueNai = valueNai + 1;
                        txtNai.setText(addDisplayValue(valueNai));
                        imgCoinNai.setVisibility(View.VISIBLE);
                        break;
                    case R.id.imgBau:
                        valueBau = valueBau + 1;
                        txtBau.setText(addDisplayValue(valueBau));
                        imgCoinBau.setVisibility(View.VISIBLE);

                        break;
                    case R.id.imgGa:
                        valueGa = valueGa + 1;
                        txtGa.setText(addDisplayValue(valueGa));
                        imgCoinGa.setVisibility(View.VISIBLE);

                        break;
                    case R.id.imgCa:
                        valueCa = valueCa + 1;
                        txtCa.setText(addDisplayValue(valueCa));
                        imgCoinCa.setVisibility(View.VISIBLE);

                        break;
                    case R.id.imgCua:
                        valueCua = valueCua + 1;
                        txtCua.setText(addDisplayValue(valueCua));
                        imgCoinCua.setVisibility(View.VISIBLE);

                        break;
                    case R.id.imgTom:
                        valueTom = valueTom + 1;
                        txtTom.setText(addDisplayValue(valueTom));
                        imgCoinTom.setVisibility(View.VISIBLE);

                        break;
                }
                onAnimalChooseListener.onChoose(new int[]{valueBau, valueCua, valueTom, valueCa, valueGa, valueNai});
            }
        }
    }

    public void reset() {
        txtNai.setText("");
        txtBau.setText("");
        txtGa.setText("");
        txtCa.setText("");
        txtCua.setText("");
        txtTom.setText("");

        valueBau = 0;
        valueCa = 0;
        valueCua = 0;
        valueGa = 0;
        valueNai = 0;
        valueTom = 0;

        currentValue = 0;

        imgCoinTom.setVisibility(View.GONE);
        imgCoinNai.setVisibility(View.GONE);
        imgCoinGa.setVisibility(View.GONE);
        imgCoinCua.setVisibility(View.GONE);
        imgCoinCa.setVisibility(View.GONE);
        imgCoinBau.setVisibility(View.GONE);

        if (onAnimalChooseListener != null)
            onAnimalChooseListener.onChoose(new int[]{valueBau, valueCua, valueTom, valueCa, valueGa, valueNai});
    }

    private String addDisplayValue(int value) {
        return "x" + value;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        reset();
    }

    public void showResult() {
        lnChooser.setVisibility(View.GONE);
        frResult.setVisibility(View.VISIBLE);
    }

    public void hideResult() {
        lnChooser.setVisibility(View.VISIBLE);
        frResult.setVisibility(View.GONE);
    }

    public void updateResult(Bitmap bp1, Bitmap bp2, Bitmap bp3) {
        ((ImageView) view.findViewById(R.id.imgResult1)).setImageBitmap(bp1);
        ((ImageView) view.findViewById(R.id.imgResult2)).setImageBitmap(bp2);
        ((ImageView) view.findViewById(R.id.imgResult3)).setImageBitmap(bp3);
    }

    public LinearLayout getBtnEnableRuleMain() {
        return btnEnableRuleMain;
    }
}
