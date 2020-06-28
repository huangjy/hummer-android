package com.hummer.core.jni;

public interface JSGetProperty {

    /**
     * 获取属性函数实现
     * @param ctx : JSContextRef
     * @param object : JSObjectRef
     * @param name : Property Name
     * @return JSValueRef
     */
    JSValue execute(JSContext ctx, JSValue object, String name);
}
