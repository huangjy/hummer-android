//
// Created by huangjy on 2018/11/28.
//

#include "CallbackManager.h"
#include <list>

#define JS_CALLBACK_ObjectFunc  "objectFunc_"
#define JS_CALLBACK_CallFunc    "callFunc_"
#define JS_CALLBACK_StaticFunc  "staticFunc_"
#define JS_CALLBACK_VarGetter   "varGetter_"
#define JS_CALLBACK_VarSetter   "varSetter_"

void CallbackManager::putFuncHandler(std::string funcName, jobject funcHandler, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_ObjectFunc;
    cbKey.append(funcName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }

    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    JNIEnv *env = JNI_GetEnv();
    jobject cbValue = (*env).NewGlobalRef(funcHandler);

    m_callbacks[cbKey] = cbValue;
    JNI_DetachEnv();
}

jobject CallbackManager::getFuncHandler(std::string funcName, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_ObjectFunc;
    cbKey.append(funcName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }

    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    return m_callbacks[cbKey];
}

void CallbackManager::putCallHandler(std::string callName, jobject callHandler, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_CallFunc;
    cbKey.append(callName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }

    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    JNIEnv *env = JNI_GetEnv();
    jobject cbValue = (*env).NewGlobalRef(callHandler);

    m_callbacks[cbKey] = cbValue;
    JNI_DetachEnv();
}

jobject CallbackManager::getCallHandler(std::string callName, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_CallFunc;
    cbKey.append(callName);

    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    return m_callbacks[cbKey];
}


void CallbackManager::putStaticHandler(std::string funcName, jobject funcHandler, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_StaticFunc;
    cbKey.append(funcName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }
    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    JNIEnv *env = JNI_GetEnv();
    jobject cbValue = (*env).NewGlobalRef(funcHandler);

    m_callbacks[cbKey] = cbValue;
    JNI_DetachEnv();
}

jobject CallbackManager::getStaticHandler(std::string funcName, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_StaticFunc;
    cbKey.append(funcName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }
    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    return m_callbacks[cbKey];
}

void CallbackManager::putVarGetter(std::string varName, jobject getterHandler, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_VarGetter;
    cbKey.append(varName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }
    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    JNIEnv *env = JNI_GetEnv();
    jobject cbValue = (*env).NewGlobalRef(getterHandler);

    m_callbacks[cbKey] = cbValue;
    JNI_DetachEnv();
}

jobject CallbackManager::getVarGetter(std::string varName, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_VarGetter;
    cbKey.append(varName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }
    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    return m_callbacks[cbKey];
}

void CallbackManager::putVarSetter(std::string varName, jobject setterHandler, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_VarSetter;
    cbKey.append(varName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }
    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    JNIEnv *env = JNI_GetEnv();
    jobject cbValue = (*env).NewGlobalRef(setterHandler);

    m_callbacks[cbKey] = cbValue;
    JNI_DetachEnv();
}

jobject CallbackManager::getVarSetter(std::string varName, std::string ctxRefKey) {
    std::string cbKey = JS_CALLBACK_VarSetter;
    cbKey.append(varName);

//    if (jsContext != 0) {
//        JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
//        std::string refKey = std::to_string((jlong)contextRef);
//        cbKey.append(refKey);
//    }
    if (!ctxRefKey.empty()) {
        cbKey.append(ctxRefKey);
    }

    return m_callbacks[cbKey];
}

void CallbackManager::removeAllCallbacks(jobject jsContext) {
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);

    JNIEnv *env = JNI_GetEnv();
    std::list<std::string> keyToRemove = {};
    for (auto it = m_callbacks.begin(); it != m_callbacks.end(); it++){
        std::string key = it->first;
        if (key.find(refKey) != std::string::npos) {
            auto jsValue = (jobject) it->second;
            (*env).DeleteGlobalRef(jsValue);
            keyToRemove.push_back(key);
        }
    }
    for (auto k : keyToRemove) {
        m_callbacks.erase(k);
    }
    JNI_DetachEnv();
}

