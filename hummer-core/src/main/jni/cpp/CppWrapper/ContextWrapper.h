//
// Created by huangjy on 2018/11/15.
//

#ifndef NATIVE_JS_ANDROID_CONTEXTWRAPPER_H
#define NATIVE_JS_ANDROID_CONTEXTWRAPPER_H

#include <JavaScriptCore/JavaScript.h>
#include <JavaInterface/JavaScriptCore.h>

class ContextWrapper {
public:
    /**
     * create java jscontext
     * @param contextRef : jscontextref
     * @return java jscontext
     */
    static jobject JNI_NewJSContext(JSGlobalContextRef contextRef);

    /**
     * get jscontextref
     * @param jsContext : java jscontext
     * @return jscontextref
     */
    static JSGlobalContextRef JNI_GetContextRef(jobject jsContext);

    /**
     * release java jscontext
     * @param jsContext : java jscontext
     */
    static void JNI_ReleseJSContext(jobject jsContext);
};


#endif //NATIVE_JS_ANDROID_CONTEXTWRAPPER_H
