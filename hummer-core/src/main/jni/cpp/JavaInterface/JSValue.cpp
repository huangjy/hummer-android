//
// Created by huangjy on 2018/11/14.
//

#include "JavaScriptCore.h"
#include <JavaScriptCore/JavaScript.h>
#include <CppWrapper/ValueWrapper.h>
#include <CppWrapper/ContextWrapper.h>
#include <CppWrapper/ClassWrapper.h>
#include <CppWrapper/WrapperUtility.h>
#include <CppWrapper/ContextManager.h>

NATIVE(JSValue, jobject, makeObject)(JNIEnv *env, jclass thiz, jlong classRef, jobject privateobj, jobject ctx){
    if(ctx == 0) return 0;

    JSObjectRef objectRef = 0; JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(ctx);
    if (classRef != 0) {
        ClassWrapper *classWrapper;
        classWrapper = reinterpret_cast<ClassWrapper *>((long) classRef);
        jobject object = privateobj ? (*env).NewGlobalRef(privateobj) : 0;
        if (object != 0) ContextManager::manager().stroreClsObject(object);
        objectRef = JSObjectMake(contextRef, classWrapper->classRef(), object);
    } else {
        objectRef = JSObjectMake(contextRef, 0, 0);
    }
    return ValueWrapper::JNI_NewJSValue(objectRef, ctx);
}

NATIVE(JSValue, jobject, makeFromJSON)(JNIEnv *env, jclass thiz, jstring json, jobject ctx){
    if(ctx == 0) return 0;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(ctx);
    JSStringRef string = WrapperUtility::JNI_Convert2JSString(json);
    JSValueRef valueRef = JSValueMakeFromJSONString(contextRef, string);
    JSStringRelease(string);

    return ValueWrapper::JNI_NewJSValue(valueRef, ctx);
}

NATIVE(JSValue, jobject, makeNumber)(JNIEnv *env, jclass thiz, jdouble number, jobject ctx){
    if(ctx == 0) return 0;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(ctx);
    JSValueRef valueRef = JSValueMakeNumber(contextRef, number);
    return ValueWrapper::JNI_NewJSValue(valueRef, ctx);
}

NATIVE(JSValue, jobject, makeFunction)(JNIEnv *env, jclass thiz, jobject func, jobject ctx){
    if(ctx == 0) return 0;

    return ValueWrapper::JNI_NewJSFunction(func, ctx);
}

NATIVE(JSValue, jobject, makeFunction2)(JNIEnv *env, jclass thiz, jstring name, jobject func, jobject ctx) {
    if (ctx == NULL) return NULL;

    return ValueWrapper::JNI_NewJSFunction(name, func, ctx);
}


NATIVE(JSValue, jobject, makeString)(JNIEnv *env, jclass thiz, jstring string, jobject ctx){
    if(ctx == 0) return 0;

    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(ctx);

    const char *charStr = (*env).GetStringUTFChars(string, 0);
    JSStringRef jsString = JSStringCreateWithUTF8CString(charStr);
    JSValueRef jsValue = JSValueMakeString(contextRef, jsString);
    JSStringRelease(jsString);
    return ValueWrapper::JNI_NewJSValue(jsValue, ctx);
}

NATIVE(JSValue, jboolean, isObject)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return false;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    return JSValueIsObject(contextRef, valueRef);
}

NATIVE(JSValue, jboolean, isNumber)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return false;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    return JSValueIsNumber(contextRef, valueRef);
}

NATIVE(JSValue, jboolean, isBoolean)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return false;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    return JSValueIsBoolean(contextRef, valueRef);
}

NATIVE(JSValue, jdouble, toNumber)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    if(!JSValueIsNumber(contextRef, valueRef)) return 0;

    JSValueRef exception = 0;
    double retNumber = JSValueToNumber(contextRef, valueRef, &exception);
//    ContextWrapper *contextWrapper = ContextManager::manager().getContextWrapper(contextRef);
//    if(exception) contextWrapper->notifyException(exception);
    return retNumber;
}

NATIVE(JSValue, jstring , getFunctionName)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    if(!JSValueIsObject(contextRef,valueRef)) return 0;

    JSValueRef exception = 0;
    JSObjectRef retObj = JSValueToObject(contextRef, valueRef, &exception);
//    ContextWrapper *contextWrapper = ContextManager::manager().getContextWrapper(contextRef);
//    if(exception) contextWrapper->notifyException(exception);

    std::string funcName = WrapperUtility::JNI_GetFunctionName(contextRef, retObj);
    JSStringRef nameStr = JSStringCreateWithUTF8CString(funcName.c_str());
    return WrapperUtility::JNI_Convert2JavaString(nameStr);
}

NATIVE(JSValue, jstring, toCharString)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    if(!JSValueIsString(contextRef, valueRef)) return 0;

    JSValueRef exception = 0;
    JSStringRef jsString = JSValueToStringCopy(contextRef, valueRef, &exception);
    jstring retString = WrapperUtility::JNI_Convert2JavaString(jsString);

//    ContextWrapper *contextWrapper = ContextManager::manager().getContextWrapper(contextRef);
//    if(exception) contextWrapper->notifyException(exception);

    return retString;
}

NATIVE(JSValue, jboolean, toBoolean)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return false;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    if(!JSValueIsBoolean(contextRef, valueRef)) return false;

    return JSValueToBoolean(contextRef, valueRef);
}

NATIVE(JSValue, jobjectArray, toArray)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);
    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);

    jobjectArray outJNIArray = 0;
    if(JSValueIsArray(contextRef, valueRef)) {
        JSValueRef exception = 0;
        JSObjectRef retObj = JSValueToObject(contextRef, valueRef, &exception);

        JSPropertyNameArrayRef properties = JSObjectCopyPropertyNames(contextRef, retObj);
        size_t propertyCount = JSPropertyNameArrayGetCount(properties);

        jclass jcls = (*env).FindClass("com/hummer/core/jni/JSValue");
        outJNIArray = (*env).NewObjectArray(propertyCount, jcls, NULL);

        int i = 0;
        for (i = 0; i < propertyCount; i++) {
            JSValueRef property = JSObjectGetPropertyAtIndex(contextRef, retObj, i, &exception);
            bool isUndefined = JSValueIsUndefined(contextRef,property);
            jobject value = 0;
            if (!isUndefined) {
                value = ValueWrapper::JNI_NewJSValue(property, context);;
                (*env).SetObjectArrayElement(outJNIArray, i, value);
            }
        }
        (*env).DeleteLocalRef(jcls);
    }

    (*env).DeleteLocalRef(context);
    return outJNIArray;
}


NATIVE(JSValue, jobject , toObject)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    return WrapperUtility::JNI_Convert2JavaObject(env,valueRef, contextRef);
}

NATIVE(JSValue, jobject, get)(JNIEnv *env, jobject thiz, jstring key){
    if(thiz == 0 || key == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    if(!JSValueIsObject(contextRef, valueRef)) return 0;

    const char *keyStr = (*env).GetStringUTFChars(key, NULL);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(keyStr);
    JSValueRef retVal = JSObjectGetProperty(contextRef, (JSObjectRef)valueRef, keyRef, NULL);
    (*env).ReleaseStringUTFChars(key, keyStr);

    return ValueWrapper::JNI_NewJSValue(retVal, context);
}

NATIVE(JSValue, void, set)(JNIEnv *env, jobject thiz, jstring key, jobject property){
    if(thiz == 0 || key == 0) return;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    (*env).DeleteLocalRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    if(!JSValueIsObject(contextRef, valueRef)) return;

    const char *keyStr = (*env).GetStringUTFChars(key, NULL);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(keyStr);

    JSValueRef propRef =  ValueWrapper::JNI_GetValueRef(property);
    JSObjectSetProperty(contextRef, (JSObjectRef)valueRef, keyRef, propRef, 0, NULL);

    (*env).ReleaseStringUTFChars(key, keyStr);
}

NATIVE(JSValue, jobject, call)(JNIEnv *env, jobject thiz, jobjectArray arguments){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);

    if (context == NULL) {
        return NULL;
    }
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);

    jsize argCount = arguments ? (*env).GetArrayLength(arguments) : 0;
    JSValueRef *argArray = (JSValueRef *)malloc(argCount * sizeof(JSValueRef));
    memset(argArray, 0, argCount * sizeof(JSValueRef));

    for(int idx = 0; idx < argCount; idx++){
        jobject jsArg = (*env).GetObjectArrayElement(arguments, idx);
        argArray[idx] = ValueWrapper::JNI_GetValueRef(jsArg);
    }

    JSValueRef retVal = JSObjectCallAsFunction(contextRef, (JSObjectRef)valueRef, 0, argCount, argArray, NULL);
    if(argArray) free(argArray);

    return ValueWrapper::JNI_NewJSValue(retVal, context);
}

NATIVE(JSValue, jobject, privateData)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return 0;

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);
    return (jobject)JSObjectGetPrivate((JSObjectRef)valueRef);
}

NATIVE(JSValue, void, destroy)(JNIEnv *env, jobject thiz){
    if(thiz == 0) return;

    ValueWrapper::JNI_ReleaseJSValue(thiz);
}

NATIVE(JSValue, jobject , valueForProperty)(JNIEnv *env, jobject thiz, jstring property){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);

    if(!JSValueIsObject(contextRef, valueRef)) return 0;

    const char *keyStr = (*env).GetStringUTFChars(property, NULL);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(keyStr);
    JSValueRef retValueRef = JSObjectGetProperty(contextRef, (JSObjectRef)valueRef, keyRef, NULL);
    JSStringRelease(keyRef);

    if(JSValueIsObject(contextRef, retValueRef)) {
        return ValueWrapper::JNI_NewJSValue(retValueRef, context);
    }
    return 0;
}

NATIVE(JSValue, jobject , valueAtIndex)(JNIEnv *env, jobject thiz, jint index){
    if(thiz == 0) return 0;

    jobject context = ValueWrapper::JNI_GetJSContext(thiz);
    JSGlobalContextRef contextRef = ContextWrapper::JNI_GetContextRef(context);

    JSValueRef valueRef = ValueWrapper::JNI_GetValueRef(thiz);

    if(!JSValueIsObject(contextRef, valueRef)) return 0;

    std::string numberString = std::to_string(index);
    JSStringRef keyRef = JSStringCreateWithUTF8CString(numberString.c_str());
    JSValueRef retVal = JSObjectGetProperty(contextRef, (JSObjectRef)valueRef, keyRef, NULL);
    JSStringRelease(keyRef);

    return ValueWrapper::JNI_NewJSValue(retVal, context);
}
