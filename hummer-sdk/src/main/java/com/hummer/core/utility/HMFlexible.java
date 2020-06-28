package com.hummer.core.utility;

import android.content.res.Resources;

public class HMFlexible {

    private static final float BASE_WIDTH = 750f; //默认屏幕宽度(750rem)
    private static final int SCREEN_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels; //当前设备宽度

    /**
     * rem 转 dp
     * @param value
     * @return
     */
    public static int rem2px(float value) {
        return (int) (value * (SCREEN_WIDTH / BASE_WIDTH));
    }

    /**
     * px 转 rem
     * @param value
     * @return
     */
    public static float px2rem(int value) {
        return value / (SCREEN_WIDTH / BASE_WIDTH);
    }
}
