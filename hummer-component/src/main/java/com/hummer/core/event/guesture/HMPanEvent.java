package com.hummer.core.event.guesture;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

@HM_EXPORT_CLASS("PanEvent")
public class HMPanEvent extends HMBaseEvent {

    @HM_EXPORT_PROPERTY("translation")
    public HashMap<String, Float> mTranslation;

    public HMPanEvent(Context context, JSValue[] args) {
        super(context, args);
        mTranslation = new HashMap<String, Float>();
    }

    @Override
    public void configWithData(HashMap<String, Object> hashMap) {
        super.configWithData(hashMap);
        for(HashMap.Entry<String, Object> entry : hashMap.entrySet()){
            mTranslation.put(entry.getKey(), (Float)entry.getValue());
        }
    }

    public HashMap<String, Float> getTranslation(){
        return mTranslation;
    }

    public void setTranslation(HashMap<String, Float>translation){
        mTranslation = translation;
    }
}
