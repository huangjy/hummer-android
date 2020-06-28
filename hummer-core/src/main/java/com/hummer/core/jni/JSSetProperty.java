package com.hummer.core.jni;

public interface JSSetProperty {
    /**
     * JS 设置属性函数实现
     * @param ctx : JSContextRef
     * @param object : JSObjectRef
     * @param name : 属性名
     * @param value : 属性值
     */
    void execute(JSContext ctx, JSValue object, String name, JSValue value);
}
