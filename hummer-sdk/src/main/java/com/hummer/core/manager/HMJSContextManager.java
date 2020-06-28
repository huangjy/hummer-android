package com.hummer.core.manager;

import com.hummer.core.bridge.HMJSContext;
import com.hummer.core.common.ILifeCycle;

import java.util.HashMap;

public class HMJSContextManager implements ILifeCycle {
    private HashMap<String, HMJSContext> mContextMap;

    @Override
    public void onCreate() {
        mContextMap = new HashMap<>();
    }

    @Override
    public void onDestroy() {
        mContextMap.clear();
    }

    public void putNJJSContext(HMJSContext njjsContext) {
        mContextMap.put(njjsContext.toString(), njjsContext);
    }

    public void removeNJJSContext(String njjsContextName) {
        mContextMap.remove(njjsContextName);
    }
}
