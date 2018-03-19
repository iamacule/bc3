package vn.mran.bc3.util.toast;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleSystem;

import vn.mran.bc3.R;
import vn.mran.bc3.util.Task;
import vn.mran.bc3.widget.CustomTextView;

/**
 * Created by AnPham on 04.05.2016.
 * <p>
 * Last modified on 19.01.2017
 * <p>
 * Copyright 2017 Audi AG, All Rights Reserved
 */
public class ToastInfo {
    private Toast toast;
    private CustomTextView text;

    public ToastInfo(Activity activity, String message) {
        create(activity, message);
    }

    public ToastInfo(Activity activity, String message, int color) {
        create(activity, message, color);
    }

    public void cancel() {
        toast.cancel();
    }

    public void show() {
        toast.show();
    }

    private void create(Activity activity, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.toast_parent));
        CustomTextView text = (CustomTextView) layout.findViewById(R.id.toast_message);
        text.setText(message);

        toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    private void create(final Activity activity, String message, final int color) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.toast_parent));
        text = (CustomTextView) layout.findViewById(R.id.toast_message);
        text.setTextColor(color);
        text.setText(message);
        text.setTextSize(activity.getResources().getDimension(R.dimen.result_text_size));

        toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0);

        Task.postDelay(new Runnable() {
            @Override
            public void run() {
                if (color== Color.GREEN){
                    new ParticleSystem(activity, 100, R.drawable.star, 2000)
                            .setSpeedRange(0.1f, 0.25f)
                            .oneShot(text, 100);
                }
            }
        },700);
    }
}
