//
// Created by huangjy on 2018/11/14.
//

#include "JavaScriptCore.h"
#include <JavaScriptCore/JavaScript.h>
#include <CppWrapper/ValueWrapper.h>
#include <CppWrapper/ContextManager.h>
#include <CppWrapper/WrapperUtility.h>
#include "CppWrapper/ContextWrapper.h"

NATIVE(JSContext, jobject, create)(JNIEnv *env, jclass thiz) {
    JSGlobalContextRef contextRef = JSGlobalContextCreate(0);
    return ContextWrapper::JNI_NewJSContext(contextRef);
}

NATIVE(JSContext, void, set)(JNIEnv *env, jobject thiz, jstring key, jobject object) {
    if (thiz == 0 || key == 0 || object == 0) return;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(thiz);
    JSObjectRef globalObj = JSContextGetGlobalObject(contextRef);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(object);

    const char *keyStr = (*env).GetStringUTFChars(key, NULL);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(keyStr);
    JSObjectSetProperty(contextRef, globalObj, keyRef, valueRef, 0, NULL);

    if (keyRef) JSStringRelease(keyRef);
    (*env).ReleaseStringUTFChars(key, keyStr);
}

NATIVE(JSContext, jboolean, del)(JNIEnv *env, jobject thiz, jstring key, jobject object) {
    if (thiz == 0 || key == 0 || object == 0) return false;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(thiz);
    JSObjectRef globalObj = JSContextGetGlobalObject(contextRef);

    const char *keyStr = (*env).GetStringUTFChars(key, NULL);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(keyStr);
    bool ret = JSObjectDeleteProperty(contextRef,globalObj,keyRef,NULL);

    if (keyRef) JSStringRelease(keyRef);
    (*env).ReleaseStringUTFChars(key, keyStr);

    return ret;
}

NATIVE(JSContext, jobject, get)(JNIEnv *env, jobject thiz, jstring key) {
    if (thiz == 0 || key == 0) return 0;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(thiz);
    JSObjectRef globalObj = JSContextGetGlobalObject(contextRef);

    const char *keyStr = (*env).GetStringUTFChars(key, NULL);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(keyStr);

    JSValueRef retValue = JSObjectGetProperty(contextRef, globalObj, keyRef, NULL);
    if (keyRef) JSStringRelease(keyRef);

    (*env).ReleaseStringUTFChars(key, keyStr);
    return ValueWrapper::JNI_NewJSValue(retValue, thiz);
}

NATIVE(JSContext, jobject, evaluateScript)(JNIEnv *env, jobject thiz, jstring script,
                                           jstring sourceURL) {
    if (thiz == 0 || script == 0) return 0;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(thiz);

    JSStringRef sourceRef = 0;
    const char *sourceStr = 0;
    if (sourceURL) {
        sourceStr = (*env).GetStringUTFChars(sourceURL, NULL);
        sourceRef = JSStringCreateWithUTF8CString(sourceStr);
    }

    const char *scriptStr = (*env).GetStringUTFChars(script, NULL);
    JSStringRef scriptRef = JSStringCreateWithUTF8CString(scriptStr);

    JSValueRef exception = 0;
    JSValueRef retValue = JSEvaluateScript(contextRef, scriptRef, 0, sourceRef, 0, &exception);

    if (scriptRef) JSStringRelease(scriptRef);
    (*env).ReleaseStringUTFChars(script, scriptStr);

    if (sourceRef) JSStringRelease(sourceRef);
    if (sourceStr)(*env).ReleaseStringUTFChars(sourceURL, sourceStr);

    if (exception) {
        JSStringRef string = JSValueToStringCopy(contextRef, exception, 0);
        std::string error = WrapperUtility::JNI_Convert2StdString(string);
        LOGE("[JSException]:%s", error.c_str());
        JSStringRelease(string);
    }
    return ValueWrapper::JNI_NewJSValue(retValue, thiz);
}

NATIVE(JSContext, void, destroy)(JNIEnv *env, jobject thiz) {
    if (thiz == 0) return;
    ContextWrapper::JNI_ReleseJSContext(thiz);
}

//NATIVE(JSContext, bool, setRemoteURL)(JNIEnv *env, jstring url) {
//    const char *urlStr = (*env).GetStringUTFChars(url, NULL);
//
//}