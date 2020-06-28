package com.hummer.core.jni;

public interface JSCallAsFunction {

    /**
     * JS函数执行实现
     * @param ctx : JSContextRef
     * @param func : JSFunction
     * @param thiz : JSObjectRef
     * @param argCount : 参数个数
     * @param argArray : 参数列表
     * @return JSValueRef
     */
    JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray);
}
