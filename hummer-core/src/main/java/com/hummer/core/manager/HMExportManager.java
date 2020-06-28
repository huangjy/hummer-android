package com.hummer.core.manager;

import com.hummer.core.base.HMExportClass;
import com.hummer.core.common.ILifeCycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HMExportManager implements ILifeCycle {

    private HashMap<String, HMExportClass> mJsClasses;
    private HashMap<String, HMExportClass> mJavaClasses;

    @Override
    public void onCreate() {
        mJsClasses = new HashMap<>();
        mJavaClasses = new HashMap<>();
    }

    @Override
    public void onDestroy() {
        mJsClasses.clear();
        mJavaClasses.clear();
    }

    public void loadExportClasses(HashMap<String, String> hashtable) {
        for (Map.Entry<String, String> entry : hashtable.entrySet()) {
            HMExportClass exportClass = new HMExportClass(entry.getValue(), entry.getKey());
            mJsClasses.put(entry.getKey(), exportClass);
            mJavaClasses.put(entry.getValue(), exportClass);
        }
    }

    /**
     * 获取类描述
     *
     * @param jsClass JS 类名称
     * @return 类描述
     */
    public HMExportClass exportClassForJS(String jsClass) {
        if (jsClass == null) {
            return null;
        }
        return mJsClasses.get(jsClass);
    }

    /**
     * 获取导出 JS 类列表
     *
     * @return 导出类列表
     */
    public ArrayList<String> allExportJSClasses() {
        return new ArrayList<>(mJsClasses.keySet());
    }

    /**
     * 获取类描述
     *
     * @param javaClass Java 类名称
     * @return 类描述
     */
    public HMExportClass exportClassForJava(String javaClass) {
        if (javaClass == null) {
            return null;
        }

        return mJavaClasses.get(javaClass);
    }
}
