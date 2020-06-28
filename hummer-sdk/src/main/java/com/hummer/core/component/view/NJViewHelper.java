package com.hummer.core.component.view;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

public class NJViewHelper {
    public  static  void  setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }
}


