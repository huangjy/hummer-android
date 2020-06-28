package com.hummer.core.event.guesture;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

@HM_EXPORT_CLASS("PinchEvent")
public class HMPinchEvent extends HMBaseEvent {

    @HM_EXPORT_PROPERTY("scale")
    public float scale;

    public HMPinchEvent(Context context, JSValue[] args) {
        super(context, args);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void configWithData(HashMap<String, Object> hashMap) {
        super.configWithData(hashMap);
        scale = (Float) hashMap.get("scale");
    }
}
