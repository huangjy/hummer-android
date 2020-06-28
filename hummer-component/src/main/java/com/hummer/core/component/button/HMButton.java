package com.hummer.core.component.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.Hummer;
import com.hummer.core.component.HMBase;
import com.hummer.core.component.text.FontManager;
import com.hummer.core.event.HMEventCollection;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.guesture.HMTapEvent;
import com.hummer.core.event.base.GestureUtils;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

@HM_EXPORT_CLASS("Button")
public class HMButton extends HMBase<android.widget.Button> {
    private StateListDrawable mStateListDrawable;

    public HMButton(Context context, @Nullable JSValue[] args) {
        super(context, args);

        getView().setAllCaps(false);
        //fixed HMButton 总是在图层最上层的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getView().setStateListAnimator(null);
        }
        getView().setOnClickListener((v) -> {
            mEventManager.dispatchEvent(HMBaseEvent.HM_CLICK_EVENT_NAME, jsContext -> {

                HMEventCollection collection = HMEventCollection.getInstance();
                Class clazz = collection.classWithEventName(HMBaseEvent.HM_CLICK_EVENT_NAME);
                JSValue tapEventJS = Hummer.getInstance().valueWithClass( clazz, jsContext);

                HMTapEvent event = (HMTapEvent) tapEventJS.toObject();
                event.setType(HMBaseEvent.HM_CLICK_EVENT_NAME);
                event.setPosition(new HashMap<String, Float>(){{
                    put("x", 0.0f); put("y", 0.0f);
                }});
                event.setState(GestureUtils.findStateInMotionEvent(null));
                event.setTarget(mAssociatedJSValue);

                return tapEventJS;
            });
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getView().setOnClickListener(null);
    }

    @Override
    protected android.widget.Button createViewInstance(Context context) {
        return new android.widget.Button(context);
    }

    /**
     * 标题
     */
    @HM_EXPORT_PROPERTY("text")
    public String text;

    public void setText(JSValue text) {
        this.text = text.toCharString();
        getView().setText(this.text);
    }

    @HM_EXPORT_PROPERTY("pressed")
    public HashMap pressed;

    public void setPressed(JSValue value) {
        pressed = (HashMap) value.toObject();
        updateDrawable(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
    }

    @HM_EXPORT_PROPERTY("disabled")
    public HashMap disabled;

    public void setDisabled(JSValue value) {
        disabled = (HashMap) value.toObject();
        updateDrawable(new int[]{-android.R.attr.state_enabled}, disabled);
    }

    /**
     * 标题文本对齐方式
     */
    @HM_EXPORT_ATTR("textAlign")
    public void setTextAlign(String textAlign) {
        switch (textAlign) {
            case "center":
                getView().setGravity(Gravity.CENTER);
                break;
            case "left":
                getView().setGravity(Gravity.LEFT);
                break;
            case "right":
                getView().setGravity(Gravity.RIGHT);
                break;
        }
    }

    /**
     * 字体
     */
    @HM_EXPORT_ATTR("fontFamily")
    public void setFontFamily(String fontFamily) {
        int style = Typeface.NORMAL;
        if (getView().getTypeface() != null) {
            style = getView().getTypeface().getStyle();
        }
        Typeface newTypeface = FontManager.getInstance().getTypeface(
                fontFamily,
                style,
                getView().getContext().getAssets());
        getView().setTypeface(newTypeface);
    }

    /**
     * 字体大小
     */
    @HM_EXPORT_ATTR("fontSize")
    public void setFontSize(String fontSize) {
        getView().setTextSize(TypedValue.COMPLEX_UNIT_PX, Float.parseFloat(fontSize));
    }

    @HM_EXPORT_ATTR("color")
    public void setColor(int color) {
        getView().setTextColor(color);
    }

    @Override
    public void onStyleUpdated(HashMap newStyle) {
        updateDrawable(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, newStyle);
    }

    private void updateDrawable(int[] state, HashMap value) {
        ColorDrawable drawable = StyleHelper.createColorDrawable(value);
        if (mStateListDrawable == null) {
            mStateListDrawable = new StateListDrawable();
        }
        if (drawable != null) {
            mStateListDrawable.addState(
                    state,
                    drawable);
            getView().setBackground(mStateListDrawable);
        }

        ColorStateList color = StyleHelper.getTextColor(style);
        if (color != null) {
            getView().setTextColor(color);
        }
    }
}
