package com.hummer.core.bridge.creator;

import android.content.Context;
import android.util.Log;

import com.hummer.core.Hummer;
import com.hummer.core.base.HMExportClass;
import com.hummer.core.base.NativeModule;
import com.hummer.core.bridge.HMJSClass;
import com.hummer.core.bridge.HMJSContext;
import com.hummer.core.component.HMBase;
import com.hummer.core.jni.JSCallAsFunction;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSValue;
import com.hummer.core.manager.IHMObjectManager;

import java.lang.reflect.Constructor;

public class HMComponentCreator implements JSCallAsFunction {
    public static final String HM_COMPONENT_PREFIX = "OBJC_";

    private IHMObjectManager mHMObjectManager;
    private HMJSContext mHMJSContext;

    public HMComponentCreator(IHMObjectManager HMObjectManager, HMJSContext hmJSContext) {
        mHMObjectManager = HMObjectManager;
        mHMJSContext = hmJSContext;
    }

    @Override
    public JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray) {
        String functionName = func.getFunctionName();
        String className = functionName.replace(HM_COMPONENT_PREFIX, "");

        HMExportClass exportClass = Hummer.getInstance()
                .getHMExportManager()
                .exportClassForJS(className);

        HMJSClass jsClass = mHMJSContext
                .getClassManager()
                .createJSClass(className, ctx);
        try {
            Class clazz = Class.forName(exportClass.className);
            Constructor<?> constructor = clazz.getConstructor(Context.class, JSValue[].class);

            Object instance = constructor.newInstance(
                    Hummer.sApplication,
                    argArray == null ? new JSValue[0] : argArray);

            JSValue ret = JSValue.makeObject(
                    jsClass.getClassRef(),
                    instance,
                    ctx);

            if (instance instanceof HMBase) {
                HMBase base = (HMBase) instance;

                mHMObjectManager.trackNJBase(base);
                base.onCreate();
                base.setAssociatedJSValue(ret);

            } else if (instance instanceof NativeModule) {
                NativeModule module = (NativeModule) instance;
                mHMObjectManager.trackNativeModule(module);
            }

            return ret;
        } catch (Exception e) {
            Log.i("HMJSContext", "+++++++>>>>:" + e.toString());
            return null;
        }
    }
}
