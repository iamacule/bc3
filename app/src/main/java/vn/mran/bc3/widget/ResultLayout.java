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

public class ResultLayout {
    private View view;
    private LinearLayout btnEnableRuleMain;

    public ResultLayout(View view) {
        this.view = view;
        btnEnableRuleMain = (LinearLayout) view.findViewById(R.id.btnRuleMain);
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
