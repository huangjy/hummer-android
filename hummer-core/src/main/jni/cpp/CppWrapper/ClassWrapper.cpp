//
// Created by huangjy on 2018/11/15.
//

#include <JavaScriptCore/JSObjectRef.h>
#include "ClassWrapper.h"
#include "ContextWrapper.h"
#include "ValueWrapper.h"
#include "CallbackManager.h"
#include "WrapperUtility.h"
#include <map>
#include <JavaScriptCore/JavaScript.h>
#include <memory.h>
#import "ContextManager.h"

static void JSFinalizeCallback (JSObjectRef object) {
    JNIEnv *env = JNI_GetEnv();
    void *privateData = JSObjectGetPrivate(object);
    if(privateData) env->DeleteGlobalRef((jobject)privateData);
}

static void JSInitializeCallback (JSContextRef ctx, JSObjectRef object) {
}

static JSValueRef JSCallAsFunctionCallback (JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount, const JSValueRef arguments[], JSValueRef* exception)
{
    std::string funcName = WrapperUtility::JNI_GetFunctionName(ctx, function);
    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject jsContext = ContextManager::manager().getJSContext(ctxRef);

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);
    jobject funcHandler = CallbackManager::manager().getCallHandler(funcName, refKey);

    return WrapperUtility::JNI_CallJSFunction(ctx, function, thisObject, argumentCount, arguments, funcHandler);
}

ClassWrapper::ClassWrapper(jobject jvdef, jlong ctxRef) {
    contextRef = ctxRef;
    std::string refKey = std::to_string((jlong)ctxRef);
    m_jvdef = jvdef;

    definition = kJSClassDefinitionEmpty;
    this->setJSClassDef(&definition);

    m_jscls = JSClassCreate(&definition);
}

void ClassWrapper::setJSClassDef(JSClassDefinition *classdef)
{
    JNIEnv *env = JNI_GetEnv();
    std::string classPath = WrapperUtility::JNI_PackageClass("JSClassDefinition");
    jclass clazz = env->FindClass(classPath.c_str());

    // set class name
    jfieldID nameFiled = env->GetFieldID(clazz, "className", "Ljava/lang/String;");
    jstring className = (jstring)env->GetObjectField(m_jvdef, nameFiled);
    const char *nameStr = env->GetStringUTFChars(className, 0);
    classdef->className = strdup(nameStr);

    // set initialize callback
    classdef->initialize = JSInitializeCallback;
    // set finalize callback
    classdef->finalize = JSFinalizeCallback;

    // set function callbacks
    std::string callSign = WrapperUtility::JNI_ClassSignature("JSCallAsFunction");
    jfieldID callField = env->GetFieldID(clazz, "callAsFunction", callSign.c_str());
    jobject callHandler = (jstring)env->GetObjectField(m_jvdef, callField);

    std::string classStr; classStr.assign(nameStr);
    std::string refKey = std::to_string((jlong)contextRef);
    CallbackManager::manager().putCallHandler(classStr, callHandler, refKey);
    classdef->callAsFunction = JSCallAsFunctionCallback;

    // set static functions
    std::string funcSign = WrapperUtility::JNI_ArraySignature("JSStaticFunction");
    jfieldID funcField = env->GetFieldID(clazz, "functions", funcSign.c_str());
    jobjectArray functions = (jobjectArray)env->GetObjectField(m_jvdef, funcField);

    if(functions != 0){
        jsize funcLen = env->GetArrayLength(functions);
        JSStaticFunction *staticFuncs = (JSStaticFunction *)malloc((funcLen + 1) * sizeof(JSStaticFunction));
        memset(staticFuncs, 0, (funcLen + 1) * sizeof(JSStaticFunction));
        for(int idx = 0; idx < funcLen; idx++){
            JSStaticFunction *staticFunc = staticFuncs + idx;
            jobject jvObj = env->GetObjectArrayElement(functions, idx);
            this->setJSStaticFunction(staticFunc, jvObj);
            env->DeleteLocalRef(jvObj);
        }
        classdef->staticFunctions = staticFuncs;
    }

    // set static variables
    std::string varSign = WrapperUtility::JNI_ArraySignature("JSStaticVariable");
    jfieldID varField = env->GetFieldID(clazz, "variables", varSign.c_str());
    jobjectArray variables = (jobjectArray)env->GetObjectField(m_jvdef, varField);

    if(variables != 0){
        jsize varLen = env->GetArrayLength(variables);
        JSStaticValue *staticVars = (JSStaticValue *)malloc((varLen + 1) * sizeof(JSStaticValue));
        memset(staticVars, 0, (varLen + 1) * sizeof(JSStaticValue));
        for(int idx = 0; idx < varLen; idx++){
            JSStaticValue *staticVar = staticVars + idx;
            jobject jvObj = env->GetObjectArrayElement(variables, idx);
            this->setJSStaticValue(staticVar, jvObj);
            env->DeleteLocalRef(jvObj);
        }
        classdef->staticValues = staticVars;
    }

    // release memory
    env->DeleteLocalRef(callHandler);
    env->DeleteLocalRef(functions);
    env->DeleteLocalRef(variables);


    env->DeleteLocalRef(clazz);
    env->ReleaseStringUTFChars(className, nameStr);
    env->DeleteLocalRef(className);
    JNI_DetachEnv();
}


static JSValueRef JSGetPropertyCallback (JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, JSValueRef* exception) {
    std::string getterKey = WrapperUtility::JNI_Convert2StdString(propertyName);

    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject jsContext = ContextManager::manager().getJSContext(ctxRef);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);
    jobject getterHandler = CallbackManager::manager().getVarGetter(getterKey, refKey);

    return WrapperUtility::JNI_CallJSGetter(ctx, object, propertyName, getterHandler);
}


static bool JSSetPropertyCallback(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, JSValueRef value, JSValueRef* exception){

    std::string setterKey = WrapperUtility::JNI_Convert2StdString(propertyName);
    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject jsContext = ContextManager::manager().getJSContext(ctxRef);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);
    jobject setterHandler = CallbackManager::manager().getVarSetter(setterKey,refKey);

    WrapperUtility::JNI_CallJSSetter(ctx, object, propertyName, value, setterHandler);
    return false;
}

void ClassWrapper::setJSStaticValue(JSStaticValue *staticValue, jobject jvValue) {
    JNIEnv *env = JNI_GetEnv();
    std::string refKey = std::to_string((jlong)contextRef);

    std::string classPath = WrapperUtility::JNI_PackageClass("JSStaticVariable");
    jclass clazz = env->FindClass(classPath.c_str());

    // set static name
    jfieldID nameField = env->GetFieldID(clazz, "name", "Ljava/lang/String;");
    jstring varName = (jstring)env->GetObjectField(jvValue, nameField);
    const char *varStr = env->GetStringUTFChars(varName, NULL);
    staticValue->name = strdup(varStr);

    // set getter callback
    std::string getterSign = WrapperUtility::JNI_ClassSignature("JSGetProperty");
    jfieldID getterField = env->GetFieldID(clazz, "getProperty", getterSign.c_str());
    jobject getterHandler = (jstring)env->GetObjectField(jvValue, getterField);
    if(getterHandler){
        std::string getterKey; getterKey.assign(varStr);
        CallbackManager::manager().putVarGetter(getterKey, getterHandler, refKey);
        staticValue->getProperty = JSGetPropertyCallback;
    }

    // set setter callback
    std::string setterSign = WrapperUtility::JNI_ClassSignature("JSSetProperty");
    jfieldID setterField = env->GetFieldID(clazz, "setProperty", setterSign.c_str());
    jobject setterHandler = (jstring)env->GetObjectField(jvValue, setterField);
    if(setterHandler){
        std::string setterKey; setterKey.assign(varStr);
        CallbackManager::manager().putVarSetter(setterKey, setterHandler, refKey);
        staticValue->setProperty = JSSetPropertyCallback;
    }

    jfieldID attrField = env->GetFieldID(clazz, "attributes", "I");
    int attributes = (int)env->GetIntField(jvValue, attrField);
    staticValue->attributes = attributes;

    env->DeleteLocalRef(getterHandler);
    env->DeleteLocalRef(setterHandler);
    env->DeleteLocalRef(clazz);
    env->ReleaseStringUTFChars(varName, varStr);
    env->DeleteLocalRef(varName);
    JNI_DetachEnv();
}


static JSValueRef JSStaticFuncCallback(JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount, const JSValueRef arguments[], JSValueRef* exception){

    std::string funcName = WrapperUtility::JNI_GetFunctionName(ctx, function);
    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject jsContext = ContextManager::manager().getJSContext(ctxRef);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(jsContext);
    std::string refKey = std::to_string((jlong)contextRef);
    jobject funcHandler = CallbackManager::manager().getStaticHandler(funcName, refKey);

    return WrapperUtility::JNI_CallJSFunction(ctx, function, thisObject, argumentCount, arguments, funcHandler);
}

void ClassWrapper::setJSStaticFunction(JSStaticFunction *staticFunction, jobject jvValue) {
    JNIEnv *env = JNI_GetEnv();
    std::string classPath = WrapperUtility::JNI_PackageClass("JSStaticFunction");
    jclass clazz = env->FindClass(classPath.c_str());

    // set static name
    jfieldID nameField = env->GetFieldID(clazz, "name", "Ljava/lang/String;");
    jstring funcName = (jstring)env->GetObjectField(jvValue, nameField);
    const char *funcStr = env->GetStringUTFChars(funcName, NULL);
    staticFunction->name = strdup(funcStr);

    // set static callback
    std::string callSign = WrapperUtility::JNI_ClassSignature("JSCallAsFunction");
    jfieldID callField = env->GetFieldID(clazz, "callback", callSign.c_str());
    jobject callbackHandler = (jstring)env->GetObjectField(jvValue, callField);

    std::string callbackKey; callbackKey.assign(funcStr);
    std::string refKey = std::to_string((jlong)contextRef);
    CallbackManager::manager().putStaticHandler(callbackKey, callbackHandler, refKey);
    staticFunction->callAsFunction = JSStaticFuncCallback;

    jfieldID attrField = env->GetFieldID(clazz, "attributes", "I");
    int attributes = (int)env->GetIntField(jvValue, attrField);
    staticFunction->attributes = attributes;

    env->DeleteLocalRef(callbackHandler);
    env->DeleteLocalRef(clazz);
    env->ReleaseStringUTFChars(funcName, funcStr);
    env->DeleteLocalRef(funcName);
    JNI_DetachEnv();
}

ClassWrapper::~ClassWrapper() {
    if (definition.staticFunctions != NULL) {
        free((void*)definition.staticFunctions);
    }

    if (definition.staticValues != NULL) {
        free((void*)definition.staticValues);
    }

    JSClassRelease(m_jscls);
}