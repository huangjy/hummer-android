package com.hummer.core.event;

import com.hummer.core.common.ILifeCycle;
import com.hummer.core.event.base.IBaseEvent;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HMEventManager implements ILifeCycle, IEventListener {
    private HashMap<String, List<JSValue>> mEventListeners;

    @Override
    public void onCreate() {
        mEventListeners = new HashMap<>();
    }

    @Override
    public void onDestroy() {
        mEventListeners.clear();
    }

    @Override
    public void addEventListener(String eventName, JSValue callback) {
        if (mEventListeners.containsKey(eventName)) {
            List<JSValue> callbacks = mEventListeners.get(eventName);

            if (callbacks.contains(callback)) {
                return;
            }

            callbacks.add(callback);
        } else {
            List<JSValue> callbacks = new ArrayList<>();

            callbacks.add(callback);

            mEventListeners.put(eventName, callbacks);
        }
    }

    @Override
    public void removeEventListener(String eventName, JSValue callback) {
        if (!mEventListeners.containsKey(eventName)) {
            return;
        }

        if (callback == null) return;

        List<JSValue> callbacks = mEventListeners.get(eventName);

        JSValue cbToRemove = null;
        for (JSValue cb : callbacks) {
            if (cb.valueRef() != callback.valueRef()) {
                continue;
            }

            cbToRemove = cb;
            break;
        }

        if (cbToRemove != null) {
            callbacks.remove(cbToRemove);
        }
    }

    public void dispatchEvent(String eventName, DispatchCallback dispatchCallback) {
        if (!mEventListeners.containsKey(eventName)) {
            return;
        }

        List<JSValue> callbacks = mEventListeners.get(eventName);

        for (JSValue callback : callbacks) {
            JSValue event = dispatchCallback.onCreateEvent(callback.getContext());

            if (event == null) continue;

            callback.call(new JSValue[]{
                event
            });
        }
    }

    public interface DispatchCallback {
        JSValue onCreateEvent(JSContext jsContext);
    }
}
