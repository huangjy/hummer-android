package com.hummer.core.event;

import com.hummer.core.event.base.IBaseEvent;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

public interface IEventListener {

    void addEventListener(String eventName, JSValue callback);

    void removeEventListener(String eventName, JSValue callback);
}
