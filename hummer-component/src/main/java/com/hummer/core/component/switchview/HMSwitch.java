package com.hummer.core.component.switchview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.Hummer;
import com.hummer.core.component.HMBase;
import com.hummer.core.event.HMEventCollection;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.view.HMSwitchEvent;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("Switch")
public class HMSwitch extends HMBase<android.widget.Switch> implements CompoundButton.OnCheckedChangeListener {
    @Nullable
    private Integer mOnTrackColor;
    @Nullable
    private Integer mOffTrackColor;

    public HMSwitch(Context context, @Nullable JSValue[] args) {
        super(context,args);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getView().setOnCheckedChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getView().setOnCheckedChangeListener(null);
    }

    @Override
    protected android.widget.Switch createViewInstance(Context context) {
        return new android.widget.Switch(context);
    }

    /**
     * 是否打开
     */
    @HM_EXPORT_PROPERTY("checked")
    public boolean checked;

    public void setChecked(JSValue checked) {
        this.checked = checked.toBoolean();
        doChecked(this.checked);
    }

    public boolean getChecked() {
        return this.checked;
    }

    @HM_EXPORT_ATTR("onColor")
    public void setOnColor(int color) {
        mOnTrackColor = color;
        if (getView().isChecked()) {
            setTrackColor(color);
        }
    }

    @HM_EXPORT_ATTR("offColor")
    public void setOffColor(int color) {
        mOffTrackColor = color;
        if (!getView().isChecked()) {
            setTrackColor(color);
        }
    }

    @HM_EXPORT_ATTR("thumbColor")
    public void setThumbColor(int color) {
        setColor(getView().getThumbDrawable(), color);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked = isChecked;
        setTrackColor(isChecked);

        mEventManager.dispatchEvent(HMBaseEvent.HM_SWITCH_EVENT_NAME, jsContext -> {

            HMEventCollection collection = HMEventCollection.getInstance();
            Class clazz = collection.classWithEventName(HMBaseEvent.HM_SWITCH_EVENT_NAME);
            JSValue switchEventJS = Hummer.getInstance().valueWithClass( clazz, jsContext);

            HMSwitchEvent event = (HMSwitchEvent) switchEventJS.toObject();
            event.setType(HMBaseEvent.HM_SWITCH_EVENT_NAME);
            event.setState(isChecked);
            event.setTarget(mAssociatedJSValue);

            return switchEventJS;
        });
    }

    public void doChecked(boolean checked) {
        if (getView().isChecked() != checked) {
            getView().setChecked(checked);
            setTrackColor(checked);
        }
    }

    private void setColor(Drawable drawable, @Nullable Integer color) {
        if (color == null) {
            drawable.clearColorFilter();
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setTrackColor(boolean checked) {
        setTrackColor(checked ? mOnTrackColor : mOffTrackColor);
    }

    private void setTrackColor(@Nullable Integer color) {
        setColor(getView().getTrackDrawable(), color);
    }
}
