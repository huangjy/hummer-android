//
// Created by huangjy on 2018/11/15.
//

#include "ContextWrapper.h"
#include "WrapperUtility.h"
#include "ContextManager.h"
#include "CallbackManager.h"
#include <android/log.h>

jobject ContextWrapper::JNI_NewJSContext(JSGlobalContextRef contextRef) {
    JNIEnv *env = JNI_GetEnv();
    std::string classPath = WrapperUtility::JNI_PackageClass("JSContext");
    jclass clazz = env->FindClass(classPath.c_str());

    std::string methodSign = WrapperUtility::JNI_MethodSignature("V", {"J"});
    jmethodID methodId = env->GetMethodID(clazz, "<init>", methodSign.c_str());
    jobject retValue = env->NewObject(clazz, methodId, (jlong)contextRef);
    ContextManager::manager().storeJSContext(retValue);

    env->DeleteLocalRef(clazz);
    JNI_DetachEnv();
    return retValue;
}

JSGlobalContextRef ContextWrapper::JNI_GetContextRef(jobject jsContext) {
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->GetObjectClass(jsContext);

    std::string methodSign = WrapperUtility::JNI_MethodSignature("J", {});
    jmethodID methodID = env->GetMethodID(clazz, "contextRef", methodSign.c_str());
    jlong contextRef = env->CallLongMethod(jsContext, methodID);

    env->DeleteLocalRef(clazz);
    JNI_DetachEnv();
    return reinterpret_cast<JSGlobalContextRef>(contextRef);
}

void ContextWrapper::JNI_ReleseJSContext(jobject jsContext) {
    ContextManager::manager().removeJSContext(jsContext);
    CallbackManager::manager().removeAllCallbacks(jsContext);
    ContextManager::manager().removeClsObject();
}

//JSGlobalContextRef ContextWrapper::globalContext() {
//    return m_context;
//}
//
//void ContextWrapper::notifyException(JSValueRef exception) {
//    if(exception == 0) return;
//
//    JSStringRef string = JSValueToStringCopy(m_context, exception, 0);
//    std::string error = WrapperUtility::JNI_Convert2StdString(string);
//    LOGE("[JSException]:%s", error.c_str());
//    JSStringRelease(string);
//}
//
//ValueWrapper* ContextWrapper::wrapperValue(JSValueRef valueRef) {
//    std::string refKey = std::to_string((long)valueRef);
//    ValueWrapper *valueWrapper = m_values[refKey];
//    if(valueWrapper == 0){
//        valueWrapper = new ValueWrapper(m_context, valueRef);
//        m_values[refKey] = valueWrapper;
//    }
//    return valueWrapper;
//}