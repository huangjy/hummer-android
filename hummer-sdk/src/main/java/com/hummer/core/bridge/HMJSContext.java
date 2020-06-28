package com.hummer.core.bridge;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaNode;
import com.hummer.core.Hummer;
import com.hummer.core.base.HMExportClass;
import com.hummer.core.base.HMExportMethod;
import com.hummer.core.bridge.creator.HMComponentCreator;
import com.hummer.core.common.ILifeCycle;
import com.hummer.core.component.HMBase;
import com.hummer.core.component.notification.NotifyCenter;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSObjectFunction;
import com.hummer.core.jni.JSValue;
import com.hummer.core.manager.HMClassManager;
import com.hummer.core.manager.HMExportManager;
import com.hummer.core.manager.HMObjectManager;
import com.hummer.core.utility.HMUtility;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import static com.hummer.core.bridge.creator.HMComponentCreator.HM_COMPONENT_PREFIX;


public class HMJSContext implements ILifeCycle {

    JSContext mJsContext;
    Context mContext;
    private ViewGroup mRootView;
    private static Integer fileIndex = 0;

    private JSValue mGlobalValue;
    private JSValue mEnvValue;

    private HMClassManager mClassManager;
    private ILifeCycle mLifeCycleCallback;

    private HMObjectManager mHMObjectManager = new HMObjectManager();

    public HMJSContext(Context context, ViewGroup rootView) {
        mContext = context;
        mRootView = rootView;

        mClassManager = new HMClassManager();
    }

    @Override
    public void onCreate() {
        mJsContext = JSContext.create();
        mHMObjectManager.onCreate();
        mClassManager.onCreate();

        registerJSClasses();
        setupJSContext();

        Env.mEnvListener = new Env.HMEnvListener() {
            @Override
            public void onUpdate(HashMap<String, Object> newEnv) {
                if (mGlobalValue != null) {
                    updateEnv(newEnv);
                }
            }
        };

        if (mLifeCycleCallback != null) {
            mLifeCycleCallback.onCreate();
        }
    }

    @Override
    public void onDestroy() {
        if (mLifeCycleCallback != null) {
            mLifeCycleCallback.onDestroy();
        }

        NotifyCenter.onDestroy();

        // 视图清理
        mRootView = null;

        // 注意：顺序，先释放所有 JSValue 再释放 JSContext

        // 释放所有 JSValue
        for (JSValue jsValue : mJsContext.getJSValues()) {
            jsValue.destroy();
        }

        Env.mEnvListener = null;
        mGlobalValue = null;

        // 释放 JSContext
        unregisterJSContext();

        // Native 层释放 JSContext
        mJsContext.destroy();
        mJsContext = null;

        mHMObjectManager.onDestroy();
        mClassManager.onDestroy();
    }

    public HMClassManager getClassManager() {
        return mClassManager;
    }

    public void setLifeCycleCallback(ILifeCycle lifeCycleCallback) {
        mLifeCycleCallback = lifeCycleCallback;
    }

    public ViewGroup getRootView() {
        return mRootView;
    }

    /**
     * 注册 JS Class
     */
    public void registerJSClasses() {
        ArrayList jsClasses = Hummer.getInstance().getHMExportManager().allExportJSClasses();
        registerJSContext(jsClasses);
        registerJSScript(jsClasses);
    }

    /**
     * 执行 JS 脚本
     * @param jsScript JS 脚本
     * @param fileName 文件名称
     * @return
     */
    public JSValue evaluateScript(String jsScript, String fileName) {
        if (jsScript == null)
            return null;
        if (fileName == null)
            fileName = createFileName();
        Log.i("evaluateScript","++++++++>>>>:"+jsScript+"\n"+jsScript.length());
        String sourceURL = fileName;
        JSValue value = mJsContext.evaluateScript(jsScript,sourceURL);

        return value;
    }

    /**
     * 获取JS上下文
     * @return JS上下文
     */
    public JSContext context() {
        return mJsContext;
    }

    /**
     * 注册JSContext
     * @param jsClasses
     */
    private void registerJSContext(ArrayList<String> jsClasses) {
        if (jsClasses == null || jsClasses.isEmpty()) return;

        HMComponentCreator hmComponentCreator = new HMComponentCreator(mHMObjectManager, this);

        for (String jsClass : jsClasses) {
            // 关联用于创建类的函数
            String className = HM_COMPONENT_PREFIX + jsClass;

            mJsContext.set(className,
                    JSValue.makeFunction2(className, hmComponentCreator, mJsContext));
        }

        JSValue renderVal = JSValue.makeFunction(
                new JSObjectFunction(
                        "render",
                        new HMJSContextFuncs.RenderMakeFunction(
                                new HMJSContextFuncs.RenderMakeFunction.Callback() {
                            @Override
                            public void onRendered(HMBase object) {
                                ViewGroup view = (ViewGroup) object.getView();

                                if (view instanceof ViewGroup) {
                                    YogaNode yogaNode = object.getDomNode().getYogaNode();

                                    yogaNode.setAlignContent(YogaAlign.CENTER);
                                    yogaNode.setAlignItems(YogaAlign.CENTER);
                                    yogaNode.setJustifyContent(YogaJustify.CENTER);

                                    yogaNode.calculateLayout(0, 0);
                                }

                                if (mRootView != null && view != null) {
                                    mRootView.removeAllViews();
                                    mRootView.addView(view);
                                }
                            }
                        })),
                mJsContext);

        JSValue callFuncVal = JSValue.makeFunction(
                new JSObjectFunction(
                        "callFunc",
                        new HMJSContextFuncs.CallfuncMakeFunction()),
                mJsContext);

        JSValue callUnExportFuncVal = JSValue.makeFunction(
                new JSObjectFunction(
                        "callUnExportFunc",
                        new HMJSContextFuncs.CallUnexpfuncMakeFunction()),
                mJsContext);

        mGlobalValue = JSValue.makeObject(
                0,
                Hummer.getInstance().getNJJSContextManager(),
                mJsContext);

        mGlobalValue.set("render", renderVal);
        mGlobalValue.set("callFunc", callFuncVal);
        mGlobalValue.set("callUnExportFunc", callUnExportFuncVal);
        mGlobalValue.set("env", updateEnv(Env.sContextInfo));

        mJsContext.set("Hummer", mGlobalValue);
    }

    private JSValue updateEnv(HashMap<String, Object> env) {
        if (mEnvValue != null) {
            mEnvValue.destroy();
        }

        Gson gson = new Gson();
        mEnvValue = JSValue.makeFromJSON(gson.toJson(env), mJsContext);
        return mEnvValue;
    }

    /**
     * 注册JSScript
     * @param jsClasses
     */
    private void registerJSScript(ArrayList jsClasses){
        String jsScript = HMJSBuiltin._HMJSBuiltinFunc;
        HashMap classMethods = allClassesMethods(jsClasses);
        String jsonString = HMUtility.HMJSONEncode(classMethods);

        jsScript += String.format("\nHMJSUtility.initGlobalEnv(%s);\n",jsonString);
        jsScript += HMJSBuiltin.HMGlabalFuntion;

        evaluateScript(jsScript,"builtin.js");
    }

    /**
     * 收集类方法
     * @param jsClasses
     * @return
     */
    private HashMap allClassesMethods(ArrayList<String> jsClasses) {
        if (jsClasses == null) return null;

        HashMap classMethods = new HashMap();

        for (String jsClass: jsClasses) {
            ArrayList staticMethods = (ArrayList) classMethods.get(jsClass);
            if (staticMethods == null) {
                staticMethods = new ArrayList();
                classMethods.put(jsClass,staticMethods);
            }

            HMExportClass export = Hummer.getInstance().getHMExportManager().exportClassForJS(jsClass);
            ArrayList<String> methods = export.allExportMethodList();

            for (String methodName : methods) {
                HMExportMethod method = export.methodForFuncName(methodName);
                if (method.methodType == HMExportMethod.HMMethodType.HMClassMethod) {
                    staticMethods.add(method.funcName);
                }
            }
        }

        return classMethods;
    }

    /**
     * 设置JSContext
     */
    private void setupJSContext() {
        JSValue logVal = JSValue.makeFunction(
                new JSObjectFunction(
                        "log",
                        new HMJSContextFuncs.ConsolsLogMakeFunction()),
                mJsContext);
        JSValue consoleVal = JSValue.makeObject(0, null, mJsContext);

        if (consoleVal == null) return;
        consoleVal.set("log", logVal);
        mJsContext.set("console", consoleVal);
    }

    /**
     * 创建文件名
     * @return
     */
    private String createFileName() {
        fileIndex++;
        String fileName = "NativeJS" + fileIndex.toString();

        return fileName;
    }

    /**
     * 卸载JSContext
     */
    private void unregisterJSContext() {
        if (mJsContext != null) {
            mJsContext.del("Hummer", mJsContext.get("Hummer"));
        }

        HMExportManager manager = Hummer.getInstance().getHMExportManager();
        ArrayList<String> jsClasses = manager.allExportJSClasses();
        if (jsClasses == null || jsClasses.isEmpty()) return;
        for (String jsClass : jsClasses) {
            mClassManager.removeJSClass(jsClass);
            String ctxKey = "OBJC_" + jsClass;
            if (mJsContext != null) {
                JSValue jsValue = mJsContext.get(ctxKey);
                mJsContext.del(ctxKey, jsValue);
                jsValue.destroy();
            }
        }
        mClassManager.clearJSClass();
    }
}
