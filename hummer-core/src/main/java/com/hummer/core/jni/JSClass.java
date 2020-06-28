package com.hummer.core.jni;

public class JSClass {
    private long classRef;
    private long contextRef;
    public JSClass(JSClassDefinition classDefinition, JSContext jsContext){
        contextRef = jsContext.contextRef();
        classRef = this.create(classDefinition, contextRef);
    }
    @Override
    protected void finalize() throws Throwable {
        this.destroy(classRef,contextRef);
        super.finalize();
    }

    public long getClassRef() {
        return classRef;
    }

    /**
     * 创建 JSClass 对象
     * @param clsDef : JSClass 定义对象
     * @return JSClass 对象指针
     */
    private native long create(JSClassDefinition clsDef, long contextRef);

    /**
     * 销毁 JSClass 对象
     * @param clazz : JSClass 对象指针
     */
    private native void destroy(long clazz, long contextRef);
}
