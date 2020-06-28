package com.hummer.core.component.anim;

import android.animation.ArgbEvaluator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.jni.JSValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 关键帧动画组件
 *
 * Created by XiaoFeng on 2019/3/27.
 */
@HM_EXPORT_CLASS("KeyframeAnimation")
public class HMKeyframeAnimation extends HMBasicAnimation {

    @HM_EXPORT_PROPERTY("keyframes")
    public List<KeyFrame> keyframes;

    public class KeyFrame {
        public float percent;
        public Object animValue;
        public String timeFunction;
    }

    public HMKeyframeAnimation(Context context, @Nullable JSValue[] args) {
        super(context, args);
    }

    @Override
    public void setDuration(JSValue duration) {
        this.duration = (float) duration.toNumber();
    }

    @Override
    public void setRepeatCount(JSValue repeatCount) {
        this.repeatCount = (int) repeatCount.toNumber();
    }

    public void setKeyframes(JSValue keyframes) {
        this.keyframes = new ArrayList<>();
        Object[] frames = (Object[]) keyframes.toObject();
        for (int i = 0; i < frames.length; i++) {
            Object obj = frames[i];
            KeyFrame f = new KeyFrame();
            if (((HashMap)obj).containsKey("percent")) {
                f.percent = ((Double) ((HashMap) obj).get("percent")).floatValue();
            }
            if (((HashMap)obj).containsKey("animValue")) {
                f.animValue = ((HashMap) obj).get("animValue");
            }
            if (((HashMap)obj).containsKey("timeFunction")) {
                f.timeFunction = (String) ((HashMap) obj).get("timeFunction");
            }
            // 如果只有一个，且百分比未填，默认取1；否则，从第二个起，如果百分比未填，默认取平均值
            if (f.percent == 0) {
                if (frames.length == 1) {
                    f.percent = 1f;
                } else if (i > 0) {
                    f.percent = 1f / (frames.length - 1) * i;
                }
            }
            this.keyframes.add(f);
        }
    }

    @Override
    protected void animTranslation(View view) {
        Keyframe[] frameXArray = new Keyframe[keyframes.size()];
        Keyframe[] frameYArray = new Keyframe[keyframes.size()];
        for (int i = 0; i < keyframes.size(); i++) {
            KeyFrame kf = keyframes.get(i);
            String[] strArray = trans2StringArray(kf.animValue);
            Keyframe frameX = Keyframe.ofFloat(kf.percent, parsePxValue(strArray[0]));
            Keyframe frameY = Keyframe.ofFloat(kf.percent, parsePxValue(strArray[1]));
            frameXArray[i] = frameX;
            frameYArray[i] = frameY;
        }

        PropertyValuesHolder frameXHolder = PropertyValuesHolder.ofKeyframe("translationX", frameXArray);
        PropertyValuesHolder frameYHolder = PropertyValuesHolder.ofKeyframe("translationY", frameYArray);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, frameXHolder, frameYHolder);
        animator = anim;
        anim.setDuration(getAnimDuration());
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    @Override
    protected void animScale(View view, int direction) {
        Keyframe[] frameArray = new Keyframe[keyframes.size()];
        for (int i = 0; i < keyframes.size(); i++) {
            KeyFrame kf = keyframes.get(i);
            Keyframe frame = Keyframe.ofFloat(kf.percent, parseFloatValue(trans2String(kf.animValue)));
            frameArray[i] = frame;
        }

        ObjectAnimator anim;
        switch (direction) {
            case DIRECTION_X: {
                PropertyValuesHolder holderX = PropertyValuesHolder.ofKeyframe("scaleX", frameArray);
                anim = ObjectAnimator.ofPropertyValuesHolder(view, holderX);
                break;
            }
            case DIRECTION_Y: {
                PropertyValuesHolder holderY = PropertyValuesHolder.ofKeyframe("scaleY", frameArray);
                anim = ObjectAnimator.ofPropertyValuesHolder(view, holderY);
                break;
            }
            case DIRECTION_XY:
            default: {
                PropertyValuesHolder holderX = PropertyValuesHolder.ofKeyframe("scaleX", frameArray);
                PropertyValuesHolder holderY = PropertyValuesHolder.ofKeyframe("scaleY", frameArray);
                anim = ObjectAnimator.ofPropertyValuesHolder(view, holderX, holderY);
                break;
            }
        }

        animator = anim;
        anim.setDuration(getAnimDuration());
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    @Override
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

        Keyframe[] frameArray = new Keyframe[keyframes.size()];
        for (int i = 0; i < keyframes.size(); i++) {
            KeyFrame kf = keyframes.get(i);
            Keyframe frame = Keyframe.ofFloat(kf.percent, parseFloatValue(trans2String(kf.animValue)));
            frameArray[i] = frame;
        }

        PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe(animName, frameArray);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, frameHolder);
        animator = anim;
        anim.setDuration(getAnimDuration());
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    @Override
    protected void animAlpha(View view) {
        Keyframe[] frameArray = new Keyframe[keyframes.size()];
        for (int i = 0; i < keyframes.size(); i++) {
            KeyFrame kf = keyframes.get(i);
            Keyframe frame = Keyframe.ofFloat(kf.percent, parseFloatValue(trans2String(kf.animValue)));
            frameArray[i] = frame;
        }

        PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("alpha", frameArray);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, frameHolder);
        animator = anim;
        anim.setDuration(getAnimDuration());
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }

    @Override
    protected void animBackgroundColor(View view) {
        Keyframe[] frameArray = new Keyframe[keyframes.size()];
        for (int i = 0; i < keyframes.size(); i++) {
            KeyFrame kf = keyframes.get(i);
            Keyframe frame = Keyframe.ofInt(kf.percent, parseColor(trans2String(kf.animValue)));
            frameArray[i] = frame;
        }

        PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("backgroundColor", frameArray);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, frameHolder);
        animator = anim;
        anim.setDuration(getAnimDuration());
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatCount(repeatCount);
        anim.setStartDelay(getAnimDelay());
        anim.setInterpolator(getInterpolator());
        anim.addListener(animatorListener);
        anim.start();
    }
}
