package com.hummer.core.jni;

import java.lang.ref.WeakReference;

public class JSValue extends Object {
    private long valueRef;
    private WeakReference<JSContext> context;

    private JSValue(long valueRef, JSContext context){
        this.valueRef = valueRef;
        this.context = new WeakReference<>(context);
        context.retainedValue(this);
    }

    public long valueRef(){
        return valueRef;
    }

    public JSContext getContext() {return context.get(); }

    /**
     * 创建 JS 对象
     * @param clazz : JSClass 定义对象
     * @param privateobj : 私有 Java 对象
     * @param ctx : JSContext 对象指针
     * @return JS 对象指针
     */
    static public native JSValue makeObject(long clazzRef, Object privateobj, JSContext ctx);

    /**
     * 创建 JS 对象
     * @param json : JSON String
     * @param ctx : JSContext 对象指针
     * @return JS 对象指针
     */
    static public native JSValue makeFromJSON(String json, JSContext ctx);

    /**
     * 创建 JS 数值
     * @param number : 浮点数
     * @param ctx : JSContext 对象指针
     * @return JS 对象指针
     */
    static public native JSValue makeNumber(double number, JSContext ctx);


    /**
     *  创建 JS 函数
     * @param func : JS 函数回调函数
     * @param ctx : JSContext 对象指针
     * @return JS 对象指针
     */
    static public native JSValue makeFunction(JSObjectFunction func, JSContext ctx);

    static public native JSValue makeFunction2(String name, JSCallAsFunction func, JSContext ctx);

    /**
     *  创建 JS 字符串
     * @param string : JS 字符串
     * @param ctx : JSContext 对象指针
     * @return JS 对象指针
     */
    static public native JSValue makeString(String string, JSContext ctx);

    /**
     * 是否是 JS 对象
     * @return true / false
     */
    public native boolean isObject();

    /**
     * 是否是 JS 数值
     * @return true / false
     */
    public native boolean isNumber();

    /**
     * 是否是 JS 布尔值
     * @return true / false
     */
    public native boolean isBoolean();

    /**
     * 转换 JS 数值
     * @return Java 浮点数
     */
    public native double toNumber();

    /**
     * 转换 JS 数值
     * @return func name
     */
    public native String getFunctionName();

    /**
     * 转换 JS 数值
     * @return Java 浮点数
     */
    public native String toCharString();
    /**
     * 转换 JS 布尔值
     * @return true / false
     */
    public native boolean toBoolean();

    /**
     * 转换 JS Java对象
     * @return Object对象
     */
    public native Object toObject();

    /**
     * 转换 JS Java对象
     * @return Object array对象
     */
    public native JSValue[] toArray();


    /**
     *
     * @param property 访问属性的值
     * @return JSValue
     */
    public native JSValue valueForProperty(String property);

    /**
     *
     * @param index 访问的位置
     * @return JSValue
     */
    public native JSValue valueAtIndex(int index);

    /**
     * 获取 JS 对象属性
     * @param key : 属性名
     * @return JS 对象属性值
     */
    public native JSValue get(String key);

    /**
     * 设置 JS 对象属性
     * @param key : 属性名
     * @param property : JS 对象属性值
     */
    public native void set(String key, JSValue property);

    /**
     * 调用执行 JS 函数
     * @param arguments : 参数列表
     * @return JS 函数执行结果
     */
    public native JSValue call(JSValue[] arguments);

    /**
     * 获取私有 Java 对象
     * @return 私有 Java 对象
     */
    public native Object privateData();

    /**
     * 销毁 JS 对象
     */
    public native void destroy();
}
