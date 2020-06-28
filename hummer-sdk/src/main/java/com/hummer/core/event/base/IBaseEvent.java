package com.hummer.core.event.base;

import com.hummer.core.jni.JSValue;

import java.util.HashMap;

public interface IBaseEvent {

    /*
     * config event with data
     */
    void configWithData(HashMap<String, Object> hashMap);

    /*
     * set event type name
     */
    void setType(String typeName);

    /*
     * set event target
     */
    void setTarget(JSValue target);

    /*
     * set event state
     */
    void setState(int state);
}
