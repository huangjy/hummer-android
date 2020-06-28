package com.hummer.core.event.guesture;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

@HM_EXPORT_CLASS("TapEvent")
public class HMTapEvent extends HMBaseEvent {

    @HM_EXPORT_PROPERTY("position")
    public HashMap<String, Float> mPosition;

    public HMTapEvent(Context context, JSValue[] args) {
        super(context, args);
        mPosition = new HashMap<String, Float>();
    }

    public HashMap<String, Float> getPosition() {
        return mPosition;
    }

    public void setPosition(HashMap<String, Float> point) {
        mPosition = point;
    }

    @Override
    public void configWithData(HashMap<String, Object> hashMap) {
        super.configWithData(hashMap);
        for(HashMap.Entry<String, Object> entry : hashMap.entrySet()){
            mPosition.put(entry.getKey(), (Float)entry.getValue());
        }
    }
}
