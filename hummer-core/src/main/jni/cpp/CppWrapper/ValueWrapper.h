//
// Created by huangjy on 2018/11/23.
//

#ifndef NATIVE_JS_ANDROID_VALUEWRAPPER_H
#define NATIVE_JS_ANDROID_VALUEWRAPPER_H

#include <JavaScriptCore/JavaScript.h>
#include <JavaInterface/JavaScriptCore.h>

class ValueWrapper {
public:
    /**
     * create java jsvalue
     * @param valueRef : jsvalueref
     * @param jsContext : java jscontext
     * @return java jsvalue
     */
    static jobject JNI_NewJSValue(JSValueRef valueRef, jobject jsContext);

    /**
     * get jsvalueref
     * @param jsvalue : java jsvalue
     * @return jsvalueref
     */
    static JSValueRef JNI_GetValueRef(jobject jsValue);

    /**
     * get java jscontext
     * @param jsValue : java jsvalue
     * @return java jscontext
     */
    static jobject JNI_GetJSContext(jobject jsValue);

    /**
     * release java jscontext
     * @param jsValue : java jsvalue
     * @return java jscontext
     */
    static void JNI_ReleaseJSValue(jobject jsValue);

    /**
     * create java jsvalue with function callback
     * @param functionCallback : function callback
     * @param contextRef : jscontextRef
     * @return java jsvalue
     */
    static jobject JNI_NewJSFunction(jobject functionCallback, jobject context);

    /**
     * with better performance.
     * @param name function name
     * @param func java function instance
     * @param context java JSContext instance
     * @return
     */
    static jobject JNI_NewJSFunction(jstring name, jobject func, jobject context);
};


#endif //NATIVE_JS_ANDROID_VALUEWRAPPER_H
