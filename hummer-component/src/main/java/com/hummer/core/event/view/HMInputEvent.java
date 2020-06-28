package com.hummer.core.event.view;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("InputEvent")
public class HMInputEvent extends HMBaseEvent {
    public static final int HM_INPUT_STATE_NORMAL = 0;
    public static final int HM_INPUT_STATE_BEGAN = 1;
    public static final int HM_INPUT_STATE_CHANGED = 2;
    public static final int HM_INPUT_STATE_ENDED = 3;

    @HM_EXPORT_PROPERTY("state")
    public int state;

    @HM_EXPORT_PROPERTY("text")
    public String text;

    public HMInputEvent(Context context, JSValue[] args) {
        super(context, args);
    }

    public void setText(String text) {
        this.text = text;
    }
}
