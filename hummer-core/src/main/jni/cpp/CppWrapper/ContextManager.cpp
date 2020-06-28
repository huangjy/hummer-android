//
// Created by huangjy on 2018/12/24.
//

#include "ContextManager.h"
#include "WrapperUtility.h"
#include "ValueManager.h"
#include <android/log.h>

void ContextManager::storeJSContext(jobject jsContext) {
    if(jsContext == 0) return;
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    JNIEnv *env = JNI_GetEnv(); std::string refKey = std::to_string((jlong)contextRef);
    if(m_ctxDict[refKey] == 0){
        jobject contextVal = env->NewGlobalRef(jsContext);
        m_ctxDict[refKey] = contextVal;
        m_valueDict[refKey] = new ValueManager();
    }

    JSGlobalContextRetain(contextRef);
    JNI_DetachEnv();
}

void ContextManager::removeJSContext(jobject jsContext) {
    if(jsContext == 0) return;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    JNIEnv *env = JNI_GetEnv(); std::string refKey = std::to_string((jlong)contextRef);

    ValueManager *valueManager = m_valueDict[refKey];
    m_valueDict.erase(refKey);

    if(valueManager) delete valueManager;

    jobject contextVal = m_ctxDict[refKey];
    if (contextVal != NULL)env->DeleteGlobalRef(contextVal);
    m_ctxDict.erase(refKey); JSGlobalContextRelease(contextRef);
    JNI_DetachEnv();
}

jobject ContextManager::getJSContext(JSGlobalContextRef contextRef) {
    std::string refKey = std::to_string((jlong)contextRef);

    return m_ctxDict[refKey];
}

ValueManager* ContextManager::getValueManager(jobject jsContext) {
    if(jsContext == 0) return nullptr;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);

    return m_valueDict[refKey];
}

void ContextManager::stroreClsObject(jobject obj) {
    if(obj == 0) return;
    m_clsObjects.push_back(obj);
}

void ContextManager::removeClsObject() {
    JNIEnv *env = JNI_GetEnv();
    for(int i=0;i<m_clsObjects.size();i++) {
        jobject obj = m_clsObjects[i];
        if(obj) env->DeleteGlobalRef(obj);
    }
    m_clsObjects.clear();
    JNI_DetachEnv();
}