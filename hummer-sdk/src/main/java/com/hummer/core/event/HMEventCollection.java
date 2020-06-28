package com.hummer.core.event;

import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.base.IBaseEvent;

import java.util.HashMap;

public class HMEventCollection {

    static HMEventCollection instance = new HMEventCollection();
    private HashMap<String, Class> mEventHashMap;

    private HMEventCollection(){
        mEventHashMap = new HashMap<>();
    };
    static public HMEventCollection getInstance(){ return instance; }

    public void addEventPlugins(HashMap<String, Class> hashMap) {
        if(hashMap != null) {
            mEventHashMap.putAll(hashMap);
        }
    }

    public Class classWithEventName(String eventName) {
        Class clazz = mEventHashMap.get(eventName);
        if(clazz != null){
            return clazz;
        } else {
            return HMBaseEvent.class;
        }
    }
}
