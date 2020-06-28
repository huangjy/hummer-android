//
// Created by huangjy on 2018/11/23.
//

#include "ValueWrapper.h"
#include "WrapperUtility.h"
#include "ContextManager.h"
#include "CallbackManager.h"
#include <android/log.h>

jobject ValueWrapper::JNI_NewJSValue(JSValueRef valueRef, jobject jsContext) {
    if(valueRef == 0 || jsContext == 0) return 0;

    std::string classPath = WrapperUtility::JNI_PackageClass("JSValue");
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->FindClass(classPath.c_str());

    std::string contextSign = WrapperUtility::JNI_ClassSignature("JSContext");
    std::string methodSign = WrapperUtility::JNI_MethodSignature("V", {"J", contextSign});
    jmethodID methodId = env->GetMethodID(clazz, "<init>", methodSign.c_str());
    jobject retValue = env->NewObject(clazz, methodId, (jlong)valueRef, jsContext);

    ValueManager *valueManager = ContextManager::manager().getValueManager(jsContext);
    if (valueManager != nullptr) {
        valueManager->storeJSValue(retValue);
    }

    env->DeleteLocalRef(clazz);
    JNI_DetachEnv();
    return retValue;
}

JSValueRef ValueWrapper::JNI_GetValueRef(jobject jsValue) {
    if(jsValue == 0) return 0;
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->GetObjectClass(jsValue);

    std::string methodSign = WrapperUtility::JNI_MethodSignature("J", {});
    jmethodID methodID = env->GetMethodID(clazz, "valueRef", methodSign.c_str());
    jlong valueRef = env->CallLongMethod(jsValue, methodID);

    env->DeleteLocalRef(clazz);
    JNI_DetachEnv();
    return (JSValueRef)valueRef;
}

jobject ValueWrapper::JNI_GetJSContext(jobject jsValue) {
    if(jsValue == 0) return 0;
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->GetObjectClass(jsValue);

    std::string contextSign = WrapperUtility::JNI_ClassSignature("JSContext");
    std::string methodSign = WrapperUtility::JNI_MethodSignature(contextSign, {});
    jmethodID methodID = env->GetMethodID(clazz, "getContext", methodSign.c_str());

    env->DeleteLocalRef(clazz);
    JNI_DetachEnv();
    return env->CallObjectMethod(jsValue, methodID);
}

void ValueWrapper::JNI_ReleaseJSValue(jobject jsValue) {
    if(jsValue == 0) return;
    jobject jsContext = ValueWrapper::JNI_GetJSContext(jsValue);
    ValueManager *valueManager = ContextManager::manager().getValueManager(jsContext);
    if (valueManager != NULL) valueManager->removeJSValue(jsValue);
}

/**
 * 向 JSC 注册的函数
 * @param ctx               JSContext
 * @param function          函数在 JS 侧的函数对象引用
 * @param thisObject        在 JS 侧的 this 对象引用
 * @param argumentCount     参数个数
 * @param arguments         参数数组
 * @param exception         异常
 * @return
 */
static JSValueRef JSObjectFuncCallback(JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount, const JSValueRef arguments[], JSValueRef* exception){
    std::string callbackKey = WrapperUtility::JNI_GetFunctionName(ctx, function);

    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject jsContext = ContextManager::manager().getJSContext(ctxRef);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);
    jobject funcHandler = CallbackManager::manager().getFuncHandler(callbackKey,refKey);

    return WrapperUtility::JNI_CallJSFunction(ctx, function, thisObject, argumentCount, arguments, funcHandler);
}

jobject ValueWrapper::JNI_NewJSFunction(jobject functionCallback, jobject context) {
    if(functionCallback == 0 || context == 0) return 0;

    std::string classPath = WrapperUtility::JNI_PackageClass("JSObjectFunction");
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->FindClass(classPath.c_str());

    // set static name
    jfieldID nameField = env->GetFieldID(clazz, "name", "Ljava/lang/String;");
    jstring funcName = (jstring)env->GetObjectField(functionCallback, nameField);
    const char *nameStr = env->GetStringUTFChars(funcName, NULL);
    JSStringRef nameRef = JSStringCreateWithUTF8CString(nameStr);

    // set static callback
    std::string classSign = WrapperUtility::JNI_ClassSignature("JSCallAsFunction");
    jfieldID callField = env->GetFieldID(clazz, "callback", classSign.c_str());
    jobject callbackHandler = (jstring)env->GetObjectField(functionCallback, callField);
    std::string callbackKey; callbackKey.assign(nameStr);

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    std::string refKey = std::to_string((jlong)contextRef);
    CallbackManager::manager().putFuncHandler(callbackKey, callbackHandler, refKey);

    JSValueRef valueRef = JSObjectMakeFunctionWithCallback(contextRef, nameRef, JSObjectFuncCallback);

    JSStringRelease(nameRef);
    env->ReleaseStringUTFChars(funcName, nameStr);
    env->DeleteLocalRef(funcName);
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(callbackHandler);
    JNI_DetachEnv();
    return ValueWrapper::JNI_NewJSValue(valueRef, context);
}

jobject ValueWrapper::JNI_NewJSFunction(jstring name, jobject func, jobject context) {
    if(func == NULL || context == NULL) return NULL;

    JNIEnv *env = JNI_GetEnv();

    // 函数名转为 JS 形式
    const char *nameStr = env->GetStringUTFChars(name, NULL);
    JSStringRef nameRef = JSStringCreateWithUTF8CString(nameStr);
    std::string callbackKey;
    callbackKey.assign(nameStr);

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    std::string refKey = std::to_string((jlong)contextRef);
    CallbackManager::manager().putFuncHandler(callbackKey, func, refKey);

    JSValueRef valueRef = JSObjectMakeFunctionWithCallback(contextRef, nameRef, JSObjectFuncCallback);

    JSStringRelease(nameRef);
    env->ReleaseStringUTFChars(name, nameStr);

    JNI_DetachEnv();
    return ValueWrapper::JNI_NewJSValue(valueRef, context);
}