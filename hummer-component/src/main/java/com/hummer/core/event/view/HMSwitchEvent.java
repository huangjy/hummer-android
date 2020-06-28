package com.hummer.core.event.view;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("SwitchEvent")
public class HMSwitchEvent extends HMBaseEvent {

    public HMSwitchEvent(Context context, JSValue[] args) {
        super(context, args);
    }

    @HM_EXPORT_PROPERTY("state")
    public boolean state;

    public void setState(boolean state) {
        this.state = state;
    }
}
