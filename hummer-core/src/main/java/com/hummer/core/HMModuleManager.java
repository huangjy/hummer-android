package com.hummer.core;

import com.hummer.core.module.HMBuilder;
import com.hummer.core.module.HMModule;

import java.util.HashMap;

/**
 * @author: linjizong
 * @date: 2019/4/12
 * @desc:
 */
public class HMModuleManager {
    private HashMap<Class, Object> mHandlerMap = new HashMap<>();


    private static class Holder {
        private static HMModuleManager INSTANCE = new HMModuleManager();
    }

    public static HMModuleManager getInstance() {
        return HMModuleManager.Holder.INSTANCE;
    }


    public void register(HMModule module) {
        HMBuilder builder = new HMBuilder();
        module.applyOptions(builder);
        mHandlerMap.clear();
        mHandlerMap.putAll(builder.build());
    }

    public <T> T getHandler(Class className) {
        if (className == null) {
            return null;
        }
        Object handler = mHandlerMap.get(className);
        if (handler != null) {
            return (T) handler;
        }
        return null;
    }

}
