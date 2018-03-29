package vn.mran.bc3.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

import vn.mran.bc3.R;
import vn.mran.bc3.helper.Log;
import vn.mran.bc3.instance.Media;
import vn.mran.bc3.instance.Rule;
import vn.mran.bc3.util.ResizeBitmap;
import vn.mran.bc3.util.Task;

/**
 * Created by Mr An on 28/11/2017.
 */

public class DrawPlay extends View {

    private final int ANIMAL_SIZE_PERCENT = 40;
    private final int MID_POINT = 56;
    private final int ANIMAL_1_Y_POINT_1 = MID_POINT - 8;
    private final int ANIMAL_2_Y_POINT_1 = MID_POINT + 7;
    private final int SIZE = 97;

    public interface OnDrawLidUpdate {
        void onTouch();

        void onLidChanged(boolean isOpened);

        void onSoundEffect(int id);
    }

    private final String TAG = getClass().getSimpleName();

    private Bitmap[] animalArrays1 = new Bitmap[6];
    private Bitmap[] animalArrays2 = new Bitmap[6];
    private OnDrawLidUpdate onDrawLidUpdate;

    private Bitmap bpLid;
    private Bitmap bpPlate;

    private Rect rectLid;
    private Rect rectPlate;
    private int width;
    private int height;

    private Point midPoint;
    private boolean isLidOpened = false;
    private int[] resultArrays = new int[3];
    private int[] randomNumberArrays = new int[3];
    private Point[] randomMidPointArrays = new Point[]{new Point(), new Point(), new Point()};

    public DrawPlay(Context context) {
        super(context);
        init(context);
    }

    public DrawPlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnDrawLidUpdate(OnDrawLidUpdate onDrawLidUpdate) {
        this.onDrawLidUpdate = onDrawLidUpdate;
    }

    private void init(Context context) {
        bpLid = BitmapFactory.decodeResource(context.getResources(), R.drawable.lid);
        bpPlate = BitmapFactory.decodeResource(context.getResources(), R.drawable.plate);
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    public void initialize(int w, int h) {

        width = w;
        height = h;

        midPoint = new Point(width / 2, height * MID_POINT / 100);
        bpLid = ResizeBitmap.resize(bpLid, w * SIZE / 100);
        bpPlate = ResizeBitmap.resize(bpPlate, w * SIZE / 100);

        animalArrays1[0] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.bau_1), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays1[1] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.cua_1), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays1[2] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.tom_1), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays1[3] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ca_1), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays1[4] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ga_1), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays1[5] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.nai_1), width * ANIMAL_SIZE_PERCENT / 100);

        animalArrays2[0] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.bau_2), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays2[1] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.cua_2), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays2[2] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.tom_2), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays2[3] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ca_2), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays2[4] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.ga_2), width * ANIMAL_SIZE_PERCENT / 100);
        animalArrays2[5] = ResizeBitmap.resize(BitmapFactory.decodeResource(getResources(), R.drawable.nai_2), width * ANIMAL_SIZE_PERCENT / 100);

        updateRandomMidPointArrays();

        openLid(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Draw
        rectPlate = new Rect(width / 2 - bpPlate.getWidth() / 2, height * MID_POINT / 100 - bpPlate.getHeight() / 2, width / 2 + bpPlate.getWidth() / 2, height * MID_POINT / 100 + bpPlate.getHeight() / 2);
        canvas.drawBitmap(bpPlate, null, rectPlate, null);

        if (isLidOpened) {
            if (resultArrays.length > 0) {
                for (int i = 0; i < resultArrays.length; i++) {
                    Bitmap bp = null;
                    Rect rect;
                    if (randomNumberArrays[i] == 0) {
                        bp = animalArrays1[Rule.getInstance().getAnimal(resultArrays[i])];
                    } else {
                        bp = animalArrays2[Rule.getInstance().getAnimal(resultArrays[i])];
                    }

                    if (bp != null) {
                        rect = new Rect(randomMidPointArrays[i].x - bp.getWidth() / 2, randomMidPointArrays[i].y - bp.getHeight() / 2,
                                randomMidPointArrays[i].x + bp.getWidth() / 2, randomMidPointArrays[i].y + bp.getHeight() / 2);
                        canvas.drawBitmap(bp, null, rect, null);
                    }
                }
            }
        }

        rectLid = new Rect(midPoint.x - bpLid.getWidth() / 2, midPoint.y - bpLid.getHeight() / 2, midPoint.x + bpLid.getWidth() / 2, midPoint.y + bpLid.getHeight() / 2);
        canvas.drawBitmap(bpLid, null, rectLid, null);
    }

    private void updateRandomMidPointArrays() {
        switch (getRandomNumber(0, 1)) {
            case 0:
                for (int i = 0; i < randomMidPointArrays.length; i++) {
                    switch (i) {
                        case 0:
                            randomMidPointArrays[i].x = getRandomNumber(width * 32 / 100, width * 34 / 100);
                            randomMidPointArrays[i].y = height * ANIMAL_1_Y_POINT_1 / 100;
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].x);
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].y);
                            break;

                        case 1:
                            randomMidPointArrays[i].x = getRandomNumber(width * 67 / 100, width * 68 / 100);
                            randomMidPointArrays[i].y = height * ANIMAL_1_Y_POINT_1 / 100;
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].x);
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].y);
                            break;

                        case 2:
                            randomMidPointArrays[i].x = getRandomNumber(width * 36 / 100, width * 62 / 100);
                            randomMidPointArrays[i].y = height * ANIMAL_2_Y_POINT_1 / 100;
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].x);
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].y);
                            break;
                    }
                }
                break;
            case 1:
                for (int i = 0; i < randomMidPointArrays.length; i++) {
                    switch (i) {
                        case 0:
                            randomMidPointArrays[i].x = getRandomNumber(width * 32 / 100, width * 62 / 100);
                            randomMidPointArrays[i].y = height * ANIMAL_1_Y_POINT_1 / 100;
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].x);
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].y);
                            break;

                        case 1:
                            randomMidPointArrays[i].x = getRandomNumber(width * 32 / 100, width * 34 / 100);
                            randomMidPointArrays[i].y = height * ANIMAL_2_Y_POINT_1 / 100;
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].x);
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].y);
                            break;

                        case 2:
                            randomMidPointArrays[i].x = getRandomNumber(width * 67 / 100, width * 68 / 100);
                            randomMidPointArrays[i].y = height * ANIMAL_2_Y_POINT_1 / 100;
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].x);
                            Log.d(TAG, "randomMidPointArrays : " + randomMidPointArrays[i].y);
                            break;
                    }
                }
                break;
        }

        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onDrawLidUpdate != null)
                    onDrawLidUpdate.onTouch();
                break;
        }
        return false;
    }

    public void action() {
        if (isLidOpened)
            closeLid();
        else openLid(true);
    }

    private void closeLid() {
        for (int i = 0 - bpLid.getHeight() / 2; i <= height * MID_POINT / 100; i += width / 50) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            midPoint.y = i;
            postInvalidate();
            if (isLidOpened) {
                if (i >= (height * MID_POINT / 100) - width / 50) {
                    isLidOpened = false;
                    Task.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            onDrawLidUpdate.onSoundEffect(R.raw.shake_dice);
                            onDrawLidUpdate.onLidChanged(isLidOpened);
                        }
                    });
                }
            }
        }
    }

    private void openLid(final boolean sound) {
        isLidOpened = true;
        Task.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (sound)
                    onDrawLidUpdate.onSoundEffect(R.raw.open_close);
                onDrawLidUpdate.onLidChanged(isLidOpened);
            }
        });
        for (int i = height * MID_POINT / 100; i >= 0 - bpLid.getHeight() / 2; i -= width / 50) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            midPoint.y = i;
            postInvalidate();
        }
    }

    private int getRandomNumber(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public void setResultArrays(int[] resultArrays) {
        this.resultArrays = resultArrays;
        this.randomNumberArrays = new int[]{
                getRandomNumber(0, 1),
                getRandomNumber(0, 1),
                getRandomNumber(0, 1)
        };
        invalidate();

        if (width > 0) {
            Task.startNewBackGroundThread(new Thread(new Runnable() {
                @Override
                public void run() {
                    updateRandomMidPointArrays();
                }
            }));
        }
    }
}
