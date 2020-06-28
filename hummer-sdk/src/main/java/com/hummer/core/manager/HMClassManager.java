package com.hummer.core.manager;

import com.hummer.core.bridge.HMJSClass;
import com.hummer.core.common.ILifeCycle;
import com.hummer.core.jni.JSContext;

import java.util.HashMap;

public class HMClassManager implements ILifeCycle {
    HashMap jsClasses;
//    private Lock lock;


    @Override
    public void onCreate() {
        jsClasses = new HashMap();
    }

    @Override
    public void onDestroy() {
        jsClasses.clear();
    }

    /**
     * 创建JS Class
     * @param className 类对象名称
     * @return JS Class 对象
     */
    public HMJSClass createJSClass(String className, JSContext ctx) {
        if (className == null) return null;

        HMJSClass jsClass = jsClassWithName(className);
        if (jsClass != null) return jsClass;

        jsClass = new HMJSClass(className);
        if (jsClass != null && className != null) {
            setJsClassesWithName(jsClass,className);
        }

        jsClass.registerJSClassRef(ctx);
        return jsClass;
    }

    /**
     * 删除JS Class
     * @param className
     */
    public void removeJSClass(String className) {
        if (className == null) return;

        HMJSClass njjsClass = jsClassWithName(className);
        if (njjsClass != null) {
            njjsClass.unregisterJSClassRef();
        }

//        lock.lock();
        jsClasses.remove(className);
//        lock.unlock();
    }

    public void clearJSClass() {
        jsClasses.clear();
    }

    private HMJSClass jsClassWithName(String className) {
        if (className == null) return null;

        HMJSClass jsClass;

//        lock.lock();
        jsClass = (HMJSClass) jsClasses.get(className);
//        lock.unlock();

        return jsClass;
    }

    private void setJsClassesWithName(HMJSClass jsClass, String className) {
        if (jsClass == null || className == null) return;

//        lock.lock();
        jsClasses.put(className,jsClass);
//        lock.unlock();

        return;
    }
}
