package com.hummer.core.utility;

import android.content.Context;
import android.util.TypedValue;

public class HMConverter {
    /**
     * 对象类型转换
     * @param value 转换数据value
     * @param type 目标类型
     * @return 转换后的object对象
     */
    public static Object doConverter(Object value, Class type)
    {
        Object ret = value;
        if (Integer.TYPE.equals(type)) {
            try {
                ret = Integer.valueOf(value.toString());
            } catch (NumberFormatException e) {
                ret = Float.valueOf(value.toString()).intValue();
            }
        }
        if (Byte.TYPE.equals(type)) {
            ret = Byte.valueOf(value.toString());
        }
        if (Short.TYPE.equals(type)) {
            ret = Short.valueOf(value.toString());
        }
        if (Long.TYPE.equals(type)) {
            ret = Long.valueOf(value.toString());
        }
        if (Float.TYPE.equals(type)) {
            ret = Float.valueOf(value.toString());
        }
        if (Double.TYPE.equals(type)) {
            ret = Double.valueOf(value.toString());
        }
        if (Boolean.TYPE.equals(type)) {
            ret = Boolean.valueOf(value.toString());
        }
        if (Character.TYPE.equals(type)) {
            ret = value.toString().charAt(0);
        }
        if (type.getName().equals("java.lang.String")) {
            ret = value.toString();
        }
        // TODO others.
        return ret;
    }

    /**
     * dp转px
     * @param context Context 对象
     * @param dp dp值
     * @return px值
     */
    public static float dp2px(Context context,float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());

    }

    /**
     * px转dp
     * @param context Context对象
     * @param px px值
     * @return dp值
     */
    public static float px2dp(Context context,float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f);
    }
}
