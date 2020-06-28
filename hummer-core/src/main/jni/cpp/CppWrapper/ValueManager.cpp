//
// Created by huangjy on 2018/12/25.
//

#include "ValueManager.h"
#include "ValueWrapper.h"
#include "ContextWrapper.h"

ValueManager::~ValueManager() {
    //ValueManager::removeJSValue 逻辑已DeleteGlobalRef、JSValueUnprotect，这里存在重复调用。
//    JNIEnv *env = JNI_GetEnv();
//    for(std::map<std::string, jobject>::iterator it=m_values.begin(); it!=m_values.end(); it++){
//        jobject jsValue = (jobject)it->second;
//        env->DeleteGlobalRef(jsValue);
//
//        JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(jsValue);
//        std::string refKey = std::to_string((jlong)valueRef);
//
//        jobject jsContext = ValueWrapper::JNI_GetJSContext(jsValue);
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        JSValueUnprotect(contextRef, valueRef);
//    }
    m_values.clear();
//    JNI_DetachEnv();
}

void ValueManager::storeJSValue(jobject jsValue) {
    if(jsValue == 0) return;
    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(jsValue);
    JNIEnv *env = JNI_GetEnv(); std::string refKey = std::to_string((jlong)valueRef);

    if(m_values[refKey] == 0){
        jobject valVal = env->NewGlobalRef(jsValue);
        m_values[refKey] = valVal;
    }
    jobject jsContext = ValueWrapper::JNI_GetJSContext(jsValue);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);

    env->DeleteLocalRef(jsContext);

    JSValueProtect(contextRef, valueRef);
    JNI_DetachEnv();
}

void ValueManager::removeJSValue(jobject jsValue) {
    if(jsValue == 0) return;

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(jsValue);
    JNIEnv *env = JNI_GetEnv(); std::string refKey = std::to_string((jlong)valueRef);

    jobject jsContext = ValueWrapper::JNI_GetJSContext(jsValue);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);

    env->DeleteLocalRef(jsContext);

    JSValueUnprotect(contextRef, valueRef);

    jobject valVal = m_values[refKey];
    env->DeleteGlobalRef(valVal); m_values.erase(refKey);
    JNI_DetachEnv();
}