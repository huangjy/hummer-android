//
// Created by huangjy on 2018/11/28.
//

#ifndef NATIVE_JS_ANDROID_CALLBACKMANAGER_H
#define NATIVE_JS_ANDROID_CALLBACKMANAGER_H


#include <map>
#include <string>
#include <JavaScriptCore/JavaScript.h>
#include <JavaInterface/JavaScriptCore.h>
#include "ContextWrapper.h"


class CallbackManager {
private:
    std::map<std::string, jobject> m_callbacks;
    CallbackManager(){};
    CallbackManager(CallbackManager const&);
    CallbackManager& operator=(CallbackManager const&);
    ~CallbackManager(){};
public:
    static CallbackManager& manager() {
        static CallbackManager sManager;
        return sManager;
    }
    void putFuncHandler(std::string funcName, jobject funcHandler, std::string ctxRefKey);
    jobject getFuncHandler(std::string funcName, std::string ctxRefKey);

    void putStaticHandler(std::string funcName, jobject funcHandler, std::string ctxRefKey);
    jobject getStaticHandler(std::string funcName, std::string ctxRefKey);

    void putCallHandler(std::string funcName, jobject callHandler, std::string ctxRefKey);
    jobject getCallHandler(std::string funcName, std::string refKey);

    void putVarGetter(std::string varName, jobject getterHandler, std::string ctxRefKey);
    jobject getVarGetter(std::string varName, std::string ctxRefKey);

    void putVarSetter(std::string varName, jobject setterHandler, std::string ctxRefKey);
    jobject getVarSetter(std::string varName, std::string ctxRefKey);

    void removeAllCallbacks(jobject jsContext);
};


#endif //NATIVE_JS_ANDROID_CALLBACKMANAGER_H
