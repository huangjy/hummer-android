package com.hummer.core.component.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.Hummer;
import com.hummer.core.component.view.HMViewBackgroundDrawable;
import com.hummer.core.jni.JSValue;
import com.hummer.core.utility.HMConverter;

import java.util.HashMap;

/**
 * 基础动画组件
 *
 * Created by XiaoFeng on 2019/3/27.
 */
@HM_EXPORT_CLASS("BasicAnimation")
public class HMBasicAnimation {

    @HM_EXPORT_PROPERTY("animValue")
    public Object animValue;
    @HM_EXPORT_PROPERTY("duration")
    public float duration; //单位(s)
    @HM_EXPORT_PROPERTY("delay")
    public float delay; //单位(s)
    @HM_EXPORT_PROPERTY("repeatCount")
    public int repeatCount;
    @HM_EXPORT_PROPERTY("timeFunction")
    public String timeFunction;

    public static final int AXIS_X = 1;
    public static final int AXIS_Y = 2;
    public static final int AXIS_Z = 3;

    public static final int DIRECTION_X = 11;
    public static final int DIRECTION_Y = 12;
    public static final int DIRECTION_XY = 13;

    protected Animator animator;
    protected String animType;
    protected JSValue animNJStartListener;
    protected JSValue animNJEndListener;

    public HMBasicAnimation(Context context, @Nullable JSValue[] args) {
        animType = args[0].toCharString();
    }

    public void setDuration(JSValue duration) {
        this.duration = (float) duration.toNumber();
    }

    public void setDelay(JSValue delay) {
        this.delay = (float) delay.toNumber();
    }

    public void setRepeatCount(JSValue repeatCount) {
        this.repeatCount = (int) repeatCount.toNumber();
    }

    protected long getAnimDuration() {
        return (long) (duration * 1000);
    }

    protected int getAnimDelay() {
        return (int) (delay * 1000);
    }

    protected TimeInterpolator getInterpolator() {
        if ("Linear".equalsIgnoreCase(timeFunction)) {
            return new LinearInterpolator();
        } else if ("EaseIn".equalsIgnoreCase(timeFunction)) {
            return new AccelerateInterpolator();
        } else if ("EaseOut".equalsIgnoreCase(timeFunction)) {
            return new DecelerateInterpolator();
        } else if ("EaseInEaseOut".equalsIgnoreCase(timeFunction)) {
            return new AccelerateDecelerateInterpolator();
        } else {
            return new AccelerateDecelerateInterpolator();
        }
    }

    protected String[] trans2StringArray(Object value) {
        String[] array = null;
        if (value instanceof HashMap) {
            HashMap map = (HashMap) value;
            array = new String[2];
            Object objX = map.get("x");
            if (objX instanceof String) {
                array[0] = (String) objX;
            } else {
                array[0] = String.valueOf(objX);
            }
            Object objY = map.get("y");
            if (objY instanceof String) {
                array[1] = (String) objY;
            } else {
                array[1] = String.valueOf(objY);
            }
        }
        return array;
    }

    protected String trans2String(Object value) {
        String str;
        if (value instanceof String) {
            str = (String) value;
        } else {
            str = String.valueOf(value);
        }
        return str;
    }

    /**
     * 解析有可能带px或dp的长度值
     *
     * @param strValue
     * @return
     */
    protected float parsePxValue(String strValue) {
        float value = 0;
        try {
            if (strValue.toLowerCase().endsWith("px")) {
                strValue = strValue.replace("px", "");
                value = Float.parseFloat(strValue);
            } else {
                value = Float.parseFloat(strValue);
                value = HMConverter.dp2px(Hummer.sApplication, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 解析普通浮点型的值
     *
     * @param strValue
     * @return
     */
    protected float parseFloatValue(String strValue) {
        float value = 0;
        try {
            value = Float.parseFloat(strValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 解析颜色值
     *
     * @param strColor
     * @return
     */
    protected int parseColor(String strColor) {
        if (!strColor.startsWith("#")) {
            strColor = "#" + strColor;
        }
        int color = Color.WHITE;
        try {
            color = Color.parseColor(strColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }

    @HM_EXPORT_METHOD("on")
    public void on(JSValue jsEvent, JSValue jsListener) {
        String event = jsEvent.toCharString();
        if ("start".equalsIgnoreCase(event)) {
            animNJStartListener = jsListener;
        } else if ("end".equalsIgnoreCase(event)) {
            animNJEndListener = jsListener;
        }
    }

    public void start(View view) {
        if ("position".equalsIgnoreCase(animType)) {
            animTranslation(view);
        } else if ("opacity".equalsIgnoreCase(animType)) {
            animAlpha(view);
        } else if ("scale".equalsIgnoreCase(animType)) {
            animScale(view, DIRECTION_XY);
        } else if ("scaleX".equalsIgnoreCase(animType)) {
            animScale(view, DIRECTION_X);
        } else if ("scaleY".equalsIgnoreCase(animType)) {
            animScale(view, DIRECTION_Y);
        } else if ("rotationX".equalsIgnoreCase(animType)) {
            animRotation(view, AXIS_X);
        } else if ("rotationY".equalsIgnoreCase(animType)) {
            animRotation(view, AXIS_Y);
        } else if ("rotationZ".equalsIgnoreCase(animType)) {
            animRotation(view, AXIS_Z);
        } else if ("bgColor".equalsIgnoreCase(animType)) {
            animBackgroundColor(view);
        }
    }

    public void stop() {
        if (isRunning()) {
            animator.cancel();
            animator = null;
        }
    }

    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }

    protected AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if (animNJStartListener != null) {
                JSValue[] params = {};
                animNJStartListener.call(params);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (animNJEndListener != null) {
                JSValue[] params = {};
                animNJEndListener.call(params);
            }
        }
    };

    /**
     * 平移动画
     */
    protected void animTranslation(View view) {
        float[] vs = {0, 0};
        String[] strArray = trans2StringArray(animValue);
        if (strArray != null && strArray.length == 2) {
            vs[0] = parsePxValue(strArray[0]);
            vs[1] = parsePxValue(strArray[1]);
        }
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("translationX", vs[0]);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("translationY", vs[1]);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, holderX, holderY);
        animator = anim;
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    /**
     * 缩放动画
     */
    protected void animScale(View view, int direction) {
        float v = parseFloatValue(trans2String(animValue));
        ObjectAnimator anim;
        switch (direction) {
            case DIRECTION_X:
                anim = ObjectAnimator.ofFloat(view, "scaleX", 0, v).setDuration(getAnimDuration());
                break;
            case DIRECTION_Y:
                anim = ObjectAnimator.ofFloat(view, "scaleY", 0, v).setDuration(getAnimDuration());
                break;
            case DIRECTION_XY:
            default:
                PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", v);
                PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", v);
                anim = ObjectAnimator.ofPropertyValuesHolder(view, holderX, holderY);
                break;
        }
        animator = anim;
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    /**
     * 旋转动画
     */
    protected void animRotation(View view, int axis) {
        String animName;
        switch (axis) {
            case AXIS_X:
                animName = "rotationX";
                break;
            case AXIS_Y:
                animName = "rotationY";
                break;
            case AXIS_Z:
            default:
                animName = "rotation";
                break;
        }

        float v = parseFloatValue(trans2String(animValue));
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, animName, 0, v).setDuration(getAnimDuration());
        animator = anim;
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }


    /**
     * 透明度动画
     */
    protected void animAlpha(View view) {
        float v = parseFloatValue(trans2String(animValue));
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1, v).setDuration(getAnimDuration());
        animator = anim;
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    /**
     * 背景颜色渐变动画
     */
    protected void animBackgroundColor(View view) {
        int orgColor = ((HMViewBackgroundDrawable) view.getBackground()).getColor();
        int color = parseColor(trans2String(animValue));
        ObjectAnimator anim = ObjectAnimator.ofInt(view, "backgroundColor", orgColor, color).setDuration(getAnimDuration());
        animator = anim;
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.setEvaluator(new ArgbEvaluator());
        anim.addListener(animatorListener);
        anim.start();
    }

}
