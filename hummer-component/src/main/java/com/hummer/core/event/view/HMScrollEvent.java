package com.hummer.core.event.view;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("ScrollEvent")
public class HMScrollEvent extends HMBaseEvent {
    public static final String HM_EVENT_LIST_SCROLL = "scroll";

    public static final int HM_SCROLL_STATE_SCROLL = 2;

    @HM_EXPORT_PROPERTY("state")
    public int state;

    @HM_EXPORT_PROPERTY("dx")
    public int dx;

    @HM_EXPORT_PROPERTY("dy")
    public int dy;

    public HMScrollEvent(Context context, JSValue[] args) {
        super(context, args);
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
}
