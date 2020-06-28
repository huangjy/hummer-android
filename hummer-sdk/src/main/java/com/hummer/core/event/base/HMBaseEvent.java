package com.hummer.core.event.base;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

@HM_EXPORT_CLASS("Event")
public class HMBaseEvent implements IBaseEvent {
    public static final String HM_LONG_PRESS_EVENT_NAME     = "longPress";
    public static final String HM_CLICK_EVENT_NAME          = "tap";
    public static final String HM_SWIPE_EVENT_NAME          = "swipe";
    public static final String HM_PINCH_EVENT_NAME          = "pinch";
    public static final String HM_PAN_EVENT_NAME            = "pan";
    public static final String HM_SCROLL_EVENT_NAME         = "scroll";
    public static final String HM_INPUT_EVENT_NAME          = "input";
    public static final String HM_SWITCH_EVENT_NAME         = "switch";

    public static final int HM_GESTURE_STATE_NORMAL    = 0;
    public static final int HM_GESTURE_STATE_BEGAN     = 1;
    public static final int HM_GESTURE_STATE_CHANGED   = 2;
    public static final int HM_GESTURE_STATE_ENDED     = 3;
    public static final int HM_GESTURE_STATE_CANCELLED = 4;


    @HM_EXPORT_PROPERTY("type")
    public String type;

    @HM_EXPORT_PROPERTY("target")
    public JSValue target;

    @HM_EXPORT_PROPERTY("timestamp")
    public long timestamp;

    @HM_EXPORT_PROPERTY("state")
    public int state;

    public HMBaseEvent(Context context, JSValue[] args){
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public void configWithData(HashMap<String, Object> hashMap) {

    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setTarget(JSValue target) {
        this.target = target;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }
}
