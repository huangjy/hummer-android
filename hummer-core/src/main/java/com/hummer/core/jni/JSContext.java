package com.hummer.core.jni;

import android.support.annotation.Keep;

import java.util.ArrayList;

@Keep
public class JSContext {

    private long contextRef;

    ArrayList<JSValue> mJSValues = new ArrayList<>();

    public JSContext(long contextRef){
        this.contextRef = contextRef;
    }

    public long contextRef(){
        return this.contextRef;
    }

    /**
     * 强持JSValue，防止js对象被GC掉
     * @param value JSValue
     */
    public void retainedValue(JSValue value) {
        if (value != null && !mJSValues.contains(value)) {
            mJSValues.add(value);
        }
    }

    public ArrayList<JSValue> getJSValues() {
        return mJSValues;
    }

    /**
     * 创建 JSContext
     * @return JSContext 对象指针
     */
    static public native JSContext create();

    /**
     * 设置 JSContext 属性
     * @param key : 属性名
     * @param object : 属性值
     */
    public native void set(String key, JSValue object);

    /**
     * 删除 JSContext 属性
     * @param key : 属性名
     * @param object : 属性值
     */
    public native boolean del(String key, JSValue object);

    /**
     * 获取 JSContext 属性
     * @param key : 属性名
     * @return 属性对象指针
     */
    public native JSValue get(String key);

    /**
     * 执行 JS 脚本
     * @param script : JS 脚本
     * @param sourceURL : 执行脚本 URL
     * @return 执行返回值
     */
    public native JSValue evaluateScript(String script, String sourceURL);

    /**
     * 释放 JSContext 对象
     */
    public native void destroy();
}
