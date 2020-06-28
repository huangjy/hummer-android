package com.hummer.core.layout;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaNode;
import com.facebook.yoga.android.YogaLayout;

public class HummerLayout extends YogaLayout {
    public HummerLayout(Context context) {
        super(context);
        init();
    }

    public HummerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HummerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        YogaNode node = getYogaNode();
        node.setAlignContent(YogaAlign.CENTER);
        node.setAlignItems(YogaAlign.CENTER);
        node.setJustifyContent(YogaJustify.CENTER);
    }
}
