//
// Created by huangjy on 2018/11/29.
//

#include "WrapperUtility.h"
#include "ValueWrapper.h"
#include "ContextManager.h"
#include "CallbackManager.h"
#include <math.h>

std::string WrapperUtility::JNI_ClassName(jobject object) {

    if(object == 0) return 0;
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->GetObjectClass(object);
    jmethodID classMid = env->GetMethodID(clazz, "getClass", "()Ljava/lang/Class;");
    jobject clsObj = env->CallObjectMethod(object, classMid);
    jclass clsDef = env->GetObjectClass(clsObj);

    // Find the getName() method on the class object
    jmethodID nameMid =  env->GetMethodID(clsDef, "getName", "()Ljava/lang/String;");
    jstring name = (jstring)env->CallObjectMethod(clsObj, nameMid);
    const char *cName = env->GetStringUTFChars(name, 0);
    std::string className; className.assign(cName);
    // Release the memory pinned char array
    env->ReleaseStringUTFChars(name, cName);

    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(clsDef);
    JNI_DetachEnv();
    return className;
}

std::string WrapperUtility::JNI_PackageClass(std::string className) {
    std::string classpath = "com/hummer/core/jni/";
    classpath.append(className);
    return classpath;
}


std::string WrapperUtility::JNI_ClassSignature(std::string className) {
    std::string signature = "Lcom/hummer/core/jni/";
    signature.append(className); signature.append(";");
    return signature;
}

std::string WrapperUtility::JNI_ArraySignature(std::string className) {
    std::string signature = "[Lcom/hummer/core/jni/";
    signature.append(className); signature.append(";");
    return signature;
}

std::string WrapperUtility::JNI_MethodSignature(std::string retSign, std::list<std::string> argSigns) {
    std::string signature = "(";
    if(!argSigns.empty()){
        for(std::list<std::string>::iterator iter = argSigns.begin();  iter != argSigns.end(); ++iter ){
            signature.append(*iter);
        }
    }
    signature.append(")"); signature.append(retSign);
    return signature;
}

jobjectArray WrapperUtility::JNI_NewValueArray(JSGlobalContextRef contextRef, size_t length, JSValueRef *arguments) {
    if(length <= 0 || arguments == 0) return 0;

    std::string classPath = WrapperUtility::JNI_PackageClass("JSValue");
    JNIEnv *env = JNI_GetEnv();
    jclass clazz = env->FindClass(classPath.c_str());
    jobjectArray objArray = env->NewObjectArray(length, clazz, 0);

    jobject context = ContextManager::manager().getJSContext(contextRef);
    for(int idx = 0; idx < length; idx++){
        jobject object = ValueWrapper::JNI_NewJSValue(arguments[idx], context);
        env->SetObjectArrayElement(objArray, idx, object);
        env->DeleteLocalRef(object);
    }

    env->DeleteLocalRef(clazz);
    JNI_DetachEnv();
    return objArray;
}

JSValueRef WrapperUtility::JNI_CallJSFunction(JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount, const JSValueRef arguments[], jobject handler) {
    JNIEnv *env = JNI_GetEnv();
    jclass javaClass = env->GetObjectClass(handler);

    std::string valueSign = WrapperUtility::JNI_ClassSignature("JSValue");
    std::string contextSign = WrapperUtility::JNI_ClassSignature("JSContext");
    std::string arraySign = WrapperUtility::JNI_ArraySignature("JSValue");
    std::string methodSign = WrapperUtility::JNI_MethodSignature(valueSign, {contextSign, valueSign, valueSign, "I", arraySign});
    jmethodID javaCallback = env->GetMethodID(javaClass, "execute", methodSign.c_str());

    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    LOGD("Function Name:%s", WrapperUtility::JNI_GetFunctionName(ctxRef, function).c_str());
    jobject contextVal = ContextManager::manager().getJSContext(ctxRef);

    jobject funcVal = ValueWrapper::JNI_NewJSValue(function, contextVal);
    jobject thizVal = ValueWrapper::JNI_NewJSValue(thisObject, contextVal);

    jobjectArray valArray = WrapperUtility::JNI_NewValueArray(ctxRef, argumentCount, (JSValueRef *)arguments);
    jobject retValue = env->CallObjectMethod(handler, javaCallback, contextVal, funcVal, thizVal, argumentCount, valArray);

    env->DeleteLocalRef(javaClass);
    env->DeleteLocalRef(funcVal);
    env->DeleteLocalRef(thizVal);
    env->DeleteLocalRef(valArray);
    JNI_DetachEnv();
    return ValueWrapper::JNI_GetValueRef(retValue);
}

JSValueRef WrapperUtility::JNI_CallJSGetter(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, jobject handler) {
    JNIEnv *env = JNI_GetEnv();
    jclass javaClass = env->GetObjectClass(handler);

    std::string valueSign = WrapperUtility::JNI_ClassSignature("JSValue");
    std::string contextSign = WrapperUtility::JNI_ClassSignature("JSContext");

    std::string methodSign = WrapperUtility::JNI_MethodSignature(valueSign, {contextSign, valueSign, "Ljava/lang/String;"});
    jmethodID javaCallback = env->GetMethodID(javaClass, "execute", methodSign.c_str());

    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject contextVal = ContextManager::manager().getJSContext(ctxRef);
    jobject thizVal = ValueWrapper::JNI_NewJSValue(object, contextVal);

    jstring propStr = WrapperUtility::JNI_Convert2JavaString(propertyName);
    jobject retValue = env->CallObjectMethod(handler, javaCallback, contextVal, thizVal, propStr);

    env->DeleteLocalRef(javaClass);
    env->DeleteLocalRef(thizVal);
    env->DeleteLocalRef(propStr);

    JNI_DetachEnv();
    return ValueWrapper::JNI_GetValueRef(retValue);
}


void WrapperUtility::JNI_CallJSSetter(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, JSValueRef value, jobject handler) {
    JNIEnv *env = JNI_GetEnv();
    jclass javaClass = env->GetObjectClass(handler);

    std::string valueSign = WrapperUtility::JNI_ClassSignature("JSValue");
    std::string contextSign = WrapperUtility::JNI_ClassSignature("JSContext");

    std::string methodSign = WrapperUtility::JNI_MethodSignature("V", {contextSign, valueSign, "Ljava/lang/String;", valueSign});
    jmethodID javaCallback = env->GetMethodID(javaClass, "execute", methodSign.c_str());

    JSGlobalContextRef ctxRef = JSContextGetGlobalContext(ctx);
    jobject contextVal = ContextManager::manager().getJSContext(ctxRef);

    jobject thizVal = ValueWrapper::JNI_NewJSValue(object, contextVal);
    jobject valueVal = ValueWrapper::JNI_NewJSValue(value, contextVal);

    jstring propStr = WrapperUtility::JNI_Convert2JavaString(propertyName);
    env->CallVoidMethod(handler, javaCallback, contextVal, thizVal, propStr, valueVal);

    env->DeleteLocalRef(thizVal);
    env->DeleteLocalRef(valueVal);
    env->DeleteLocalRef(propStr);
    env->DeleteLocalRef(javaClass);
    JNI_DetachEnv();
}

std::string WrapperUtility::JNI_GetFunctionName(JSContextRef ctx, JSObjectRef function) {
    JSStringRef nameStr = JSStringCreateWithUTF8CString("name");
    JSValueRef funcRef = JSObjectGetProperty(ctx, function, nameStr, NULL);
    JSStringRelease(nameStr);

    JSStringRef funcName = JSValueToStringCopy(ctx, funcRef, NULL);

    size_t strLength = JSStringGetLength(funcName);
    char *buffer = (char *)malloc(strLength + 1);
    memset(buffer, 0, strLength + 1);
    JSStringGetUTF8CString(funcName, buffer, strLength + 1);

    std::string retValue; retValue.assign(buffer);
    if(buffer) free(buffer);
    JSStringRelease(funcName);

    return retValue;
}

std::string WrapperUtility::JNI_Convert2StdString(JSStringRef string) {
    size_t strLength = JSStringGetLength(string);
    char *buffer = (char *)malloc(strLength + 1);
    memset(buffer, 0, strLength + 1);
    JSStringGetUTF8CString(string, buffer, strLength + 1);

    std::string retValue; retValue.assign(buffer);
    if(buffer) free(buffer);

    return retValue;
}

jstring WrapperUtility::JNI_Convert2JavaString(JSStringRef string) {
    JNIEnv *env = JNI_GetEnv();
    size_t strLength = JSStringGetMaximumUTF8CStringSize(string);
    char *buffer = (char *)malloc(strLength + 1);
    memset(buffer, 0, strLength + 1);
    JSStringGetUTF8CString(string, buffer, strLength + 1);

    jstring retValue = env->NewStringUTF(buffer);
    if(buffer) free(buffer);

    JNI_DetachEnv();
    return retValue;
}


JSStringRef WrapperUtility::JNI_Convert2JSString(jstring string) {
    JNIEnv *env = JNI_GetEnv();
    const char *cStr = env->GetStringUTFChars(string, 0);
    JSStringRef ret = JSStringCreateWithUTF8CString(cStr);
    env->ReleaseStringUTFChars(string, cStr);
    JNI_DetachEnv();
    return ret;
}

jstring WrapperUtility::JNI_ConvertChar2JString(const char* c_str) {
    JNIEnv *env = JNI_GetEnv();
    jclass strClass = env->FindClass("Ljava/lang/String;");

    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = env->NewByteArray(strlen(c_str));
    env->SetByteArrayRegion(bytes, 0, strlen(c_str), (jbyte*)c_str);
    jstring encoding = env->NewStringUTF("utf-8");

    jstring ret = (jstring)env->NewObject(strClass, ctorID, bytes, encoding);

    env->DeleteLocalRef(strClass);
    env->DeleteLocalRef(bytes);
    env->DeleteLocalRef(encoding);
    JNI_DetachEnv();

    return ret;
}

jobject WrapperUtility::JNI_Convert2JavaMap(JSValueRef jsvalue, JSGlobalContextRef contextRef) {
    if(jsvalue == 0) return 0;

    JSValueRef exception = 0; JNIEnv *env = JNI_GetEnv();
    JSObjectRef retObj = JSValueToObject(contextRef, jsvalue, &exception);

    if(!JSValueIsObject(contextRef, jsvalue)) return 0;
    //map 类型
    jclass class_hashmap = env->FindClass("java/util/HashMap");
    jmethodID hashmap_init = env->GetMethodID(class_hashmap, "<init>", "()V");
    jobject HashMap = env->NewObject(class_hashmap, hashmap_init, "");
    jmethodID HashMap_put = env->GetMethodID(class_hashmap, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    JSPropertyNameArrayRef properties = JSObjectCopyPropertyNames(contextRef, retObj);
    size_t propertyCount = JSPropertyNameArrayGetCount(properties);
    for (size_t i = 0; i < propertyCount; ++i) {
        JSStringRef name = JSPropertyNameArrayGetNameAtIndex(properties, i);

        jstring keyName = WrapperUtility::JNI_Convert2JavaString(name);
        jobject value=0;
        JSValueRef property = JSObjectGetProperty(contextRef, retObj, name, &exception);
        bool isUndefined = JSValueIsUndefined(contextRef,property);
        if (!isUndefined) {
            if (JSValueIsNumber(contextRef,property) == true) {
                int num = JSValueToNumber(contextRef,property,&exception);
                std::string numstring = std::to_string(num);
                value = WrapperUtility::JNI_ConvertChar2JString(numstring.c_str());
            } else if(JSValueIsString(contextRef,property) == true){
                JSStringRef stringRef = JSValueToStringCopy(contextRef,property,&exception);
                value = WrapperUtility::JNI_Convert2JavaString(stringRef);
            } else if(JSValueIsObject(contextRef,property)){
                value = JNI_Convert2JavaMap(property,contextRef);
            }
        }

        env->CallObjectMethod(HashMap, HashMap_put, keyName, value);
        env->DeleteLocalRef(value);
        env->DeleteLocalRef(keyName);
    }
    JSPropertyNameArrayRelease(properties);
    env->DeleteLocalRef(class_hashmap);
    JNI_DetachEnv();
    return HashMap;
}

jobject WrapperUtility::JNI_Convert2JavaObject(JNIEnv *env, JSValueRef jsvalue, JSGlobalContextRef contextRef) {
    if(jsvalue == 0) return 0;
    JSValueRef exception = 0;
    //string 类型
    if(JSValueIsString(contextRef, jsvalue)) {
        JSStringRef jsString = JSValueToStringCopy(contextRef, jsvalue, &exception);
        jstring retString = WrapperUtility::JNI_Convert2JavaString(jsString);
        return retString;
    }

    //number类型
    if(JSValueIsNumber(contextRef, jsvalue)) {
        double num = JSValueToNumber(contextRef,jsvalue,&exception);

        // is int
        if (floor(num) == num) {
            int64_t integer = (int64_t) num;
            jclass cls = env->FindClass("java/lang/Long");
            jmethodID midInit = env->GetMethodID(cls, "<init>", "(J)V");
            if (NULL == midInit) return NULL;
            jobject obj = env->NewObject(cls, midInit, integer);
            env->DeleteLocalRef(cls);
            return (jobject)obj;
        } else {
            jclass cls = env->FindClass("java/lang/Double");
            jmethodID midInit = env->GetMethodID(cls, "<init>", "(D)V");
            if (NULL == midInit) return NULL;
            jobject obj = env->NewObject(cls, midInit, num);
            env->DeleteLocalRef(cls);
            return (jobject)obj;
        }
    }

    if(JSValueIsBoolean(contextRef,jsvalue)) {
        bool bl = JSValueToBoolean(contextRef,jsvalue);
        jclass cls = env->FindClass("java/lang/Boolean");
        jmethodID midInit = env->GetMethodID(cls, "<init>", "(Z)V");
        if (NULL == midInit) return NULL;
        jobject obj = env->NewObject(cls, midInit, bl);
        env->DeleteLocalRef(cls);
        return (jobject)obj;
    }

    if(!JSValueIsObject(contextRef, jsvalue)) return 0;

    //private 类型
    JSObjectRef retObj = JSValueToObject(contextRef, jsvalue, &exception);
    JSStringRef propertyRef = JSStringCreateWithUTF8CString("private");
    JSValueRef privateRef = JSObjectGetProperty(contextRef, retObj, propertyRef, NULL);

    bool isUndefined = JSValueIsUndefined(contextRef,privateRef);
    if (!isUndefined) {
        JSObjectRef valueToObjc = JSValueToObject(contextRef, privateRef, NULL);
        if (valueToObjc) {
            return (jobject)JSObjectGetPrivate(valueToObjc);
        }
    }

    // function/class 类型
    if (JSObjectIsFunction(contextRef,retObj)) {
        JSStringRef jsString = JSValueToStringCopy(contextRef, retObj, NULL);
        jstring retString = WrapperUtility::JNI_Convert2JavaString(jsString);
        return (jobject)retString;
    }

    JSPropertyNameArrayRef properties = JSObjectCopyPropertyNames(contextRef, retObj);
    size_t propertyCount = JSPropertyNameArrayGetCount(properties);
    if (propertyCount > 0) {
        // Array 类型
        if(JSValueIsArray(contextRef,jsvalue)){
            jclass jcls = env->FindClass("java/lang/Object");
            jobjectArray outJNIArray = env->NewObjectArray(propertyCount, jcls, NULL);
            int i;
            for (i = 0; i < propertyCount; i++) {
                JSValueRef property = JSObjectGetPropertyAtIndex(contextRef, retObj, i, &exception);
                bool isUndefined = JSValueIsUndefined(contextRef,property);
                jobject value = 0;
                if (!isUndefined) {
                    value = JNI_Convert2JavaObject(env,property,contextRef);
                    env->SetObjectArrayElement(outJNIArray, i, value);
                    env->DeleteLocalRef(value);
                }
            }

            env->DeleteLocalRef(jcls);
            return outJNIArray;
        }

        //map 类型
        jclass class_hashmap = env->FindClass("java/util/HashMap");
        jmethodID hashmap_init = env->GetMethodID(class_hashmap, "<init>", "()V");
        jobject HashMap = env->NewObject(class_hashmap, hashmap_init, "");
        jmethodID HashMap_put = env->GetMethodID(class_hashmap, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        for (size_t i = 0; i < propertyCount; ++i) {
            JSStringRef name = JSPropertyNameArrayGetNameAtIndex(properties, i);

            std::string ss = WrapperUtility::JNI_Convert2StdString(name);
            jstring keyName = WrapperUtility::JNI_Convert2JavaString(name);
            jobject value=0;
            JSValueRef property = JSObjectGetProperty(contextRef, retObj, name, &exception);
            bool isUndefined = JSValueIsUndefined(contextRef,property);
            if (!isUndefined) {
                value = JNI_Convert2JavaObject(env,property,contextRef);
                if (value != 0) {
                    env->CallObjectMethod(HashMap, HashMap_put, keyName, value);
                    env->DeleteLocalRef(value);
                }
            }
            env->DeleteLocalRef(keyName);
        }
        JSPropertyNameArrayRelease(properties);

        env->DeleteLocalRef(class_hashmap);

        return HashMap;
    } else {

    }

    return 0;
}
