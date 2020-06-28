package com.hummer.core;

import android.app.Application;
import android.view.ViewGroup;

import com.hummer.core.base.HMExportClass;
import com.hummer.core.base.HMPerformancePlugin;
import com.hummer.core.bridge.Env;
import com.hummer.core.bridge.HMJSContext;
import com.hummer.core.common.ILifeCycle;
import com.hummer.core.event.HMEventCollection;
import com.hummer.core.event.HMEventManager;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.base.IBaseEvent;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSValue;
import com.hummer.core.manager.HMExportManager;
import com.hummer.core.manager.HMJSContextManager;
import com.hummer.core.module.HMModule;
import com.hummer.core.utility.HMContextUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;

public class Hummer {

    static {
        System.loadLibrary("jscore");
        System.loadLibrary("android-jsc");
    }

    public static Application sApplication;

    private static final Hummer instance = new Hummer();
    private HMPerformancePlugin performancePlugin;

    private HMExportManager mHMExportManager;           // 类导出信息管理器
    private HMJSContextManager mJSContextManager;     // JSContext 管理器

    private JSContext mGlobalJSContext;

    private Hummer() {}

    public static Hummer getInstance() {
        return instance;
    }

    public void setModule(HMModule module) {
        HMModuleManager.getInstance().register(module);
    }

    /**
     * 初始化 NativeJS 引擎
     * @param application
     */
    public void init(Application application) {
        sApplication = application;

        HMContextUtil.init(application);
        Env.initEnvironment(application);

        mHMExportManager = new HMExportManager();
        mHMExportManager.onCreate();

        mJSContextManager = new HMJSContextManager();
        mJSContextManager.onCreate();

        mGlobalJSContext = JSContext.create();
    }

    /**
     * 销毁 NativeJS 引擎
     */
    public void destroy() {
        mHMExportManager.onDestroy();
        mJSContextManager.onDestroy();
        mGlobalJSContext.destroy();
    }

    /**
     * 启用新的 NativeJS 实例，与视图容器绑定
     * @param rootView
     * @return
     */
    public HMJSContext createNewContext(ViewGroup rootView) {
        HMJSContext context = new HMJSContext(sApplication, rootView);
        context.setLifeCycleCallback(new ILifeCycle() {
            @Override
            public void onCreate() {}

            @Override
            public void onDestroy() {
                mJSContextManager.removeNJJSContext(context.toString());
            }
        });
        mJSContextManager.putNJJSContext(context);
        context.onCreate();
        return context;
    }

    public HMExportManager getHMExportManager() {
        return mHMExportManager;
    }

    public HMJSContextManager getNJJSContextManager() {
        return mJSContextManager;
    }

    public void setPerformancePlugin(HMPerformancePlugin plugin) {
        performancePlugin = plugin;
    }

    /**
     * 传入 Class 返回一个 JSValue
     * 前提是这个 Class 使用 Hummer 注解导出过
     * @return
     */
    public JSValue valueWithClass(Class clazz, JSContext context) {
        if (clazz == null || context == null) return null;

        HMExportClass exportClass = mHMExportManager.exportClassForJava(clazz.getName());
        if(exportClass != null && exportClass.jsClass != null){
            String command = String.format("new %s()", exportClass.jsClass);
            return context.evaluateScript( command, null);
        }

        return null;
    }

    public void start(HashMap<String, String> hashMap1, HashMap<String, Class> hashMap2) {

        HashMap<String, String> exportClasses = new HashMap<String, String>();

        try {
            Class classExportCollection = Class.forName("com.hummer.core.HMExportCollection");
            Method methodExportClasses = classExportCollection.getMethod("exportClasses");
            exportClasses.putAll((Hashtable)methodExportClasses.invoke(null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(exportClasses != null) { exportClasses.putAll(hashMap1);}
        mHMExportManager.loadExportClasses(exportClasses);

        // 注册 EventList
        HMEventCollection collection = HMEventCollection.getInstance();
        collection.addEventPlugins(hashMap2);
    }


    public void printPerformance(String key, int value) {
        if (Hummer.getInstance().performancePlugin != null) {
            Hummer.getInstance().performancePlugin.printPerformance(key, value);
        }
    }
}
