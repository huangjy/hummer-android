package com.hummer.core.bridge;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaNode;
import com.hummer.core.Hummer;
import com.hummer.core.base.HMExportClass;
import com.hummer.core.base.HMExportMethod;
import com.hummer.core.component.HMBase;
import com.hummer.core.jni.JSCallAsFunction;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSValue;

import java.lang.reflect.Method;

public class HMJSContextFuncs {
    private HMJSContextFuncs() {}

    /**
     * callFunc JSValue对象
     */
    public static class CallfuncMakeFunction implements JSCallAsFunction {

        @Override
        public JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray) {
            // 这里与 NJJSBuiltin-->NJJSUtility->registerStatics-->Hummer.callFunc() 有关，如果后期内嵌JS有修改，出现static method不好使，可从此链排查。
            if (argCount != 3) return null;

            JSValue clazzName = argArray[0];    // JS 侧 String
            JSValue methodName = argArray[1];   // JS 侧 String

            JSValue[] parameters = argArray[2].toArray();

            return callFunc_Method_Arguments(clazzName, methodName, parameters);

        }
    }

    /**
     * callUnExportFunc JSValue对象
     */
    public static class CallUnexpfuncMakeFunction implements JSCallAsFunction {

        @Override
        public JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray) {
            return callUnExportFunc_Method_Arguments(thiz,func,argArray);
        }
    }

    /**
     * render JSValue对象
     */
    public static class RenderMakeFunction implements JSCallAsFunction {
        private Callback mCallback;

        public RenderMakeFunction(Callback callback) {
            mCallback = callback;
        }

        @Override
        public JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray) {
            if (argCount != 1) {
                return null;
            }

            JSValue page = argArray[0];

            if (page.isObject()) {
                HMBase obj = (HMBase) page.toObject();
                if (obj == null) {
                    Log.e("NJSGlobal", "render error, page is not njbase");
                    return null;
                }
                if (mCallback != null) {
                    mCallback.onRendered(obj);
                }

                return page;
            }
            return null;
        }

        interface Callback {
            void onRendered(HMBase object);
        }
    }

    /**
     * consolse log JSValue对象
     */
    public static class ConsolsLogMakeFunction implements JSCallAsFunction {

        @Override
        public JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray) {
            for (int i = 0; i < argCount; i++) {
                JSValue jsValue = argArray[i];
                Log.i("NJJSContext","++++++++>>>>args:" + jsValue.toCharString());
            }
            return null;
        }
    }

    protected static JSValue callFunc_Method_Arguments(JSValue cls, JSValue method, JSValue[] arguments) {
        Object obj = cls.toObject();
        HMExportClass clzz = Hummer.getInstance().getHMExportManager().exportClassForJS(cls.toCharString());
        HMExportMethod hmmtd = clzz.methodForFuncName(method.toCharString());
        Method mtd = hmmtd.javaMethod;

        JSValue jsValue = null;
        try {
            Object ret = mtd.invoke(obj, (Object[]) arguments);
            jsValue = JSValue.makeObject(0, ret, cls.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsValue;
    }

    protected static JSValue callUnExportFunc_Method_Arguments(JSValue cls, JSValue method, JSValue[] arguments) {
        Object obj = cls.toObject();
        Method mtd = (Method) method.toObject();

        Object[] args = {};
        for (int i = 0; i < arguments.length; i++) {
            args[i] = arguments[i].toObject();
        }

        JSValue jsValue = null;
        try {
            Object ret = mtd.invoke(obj, args);
            jsValue = JSValue.makeObject(0, ret, cls.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsValue;
    }
}
