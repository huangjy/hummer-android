//
// Created by huangjy on 2018/11/29.
//

#ifndef NATIVE_JS_ANDROID_WRAPPERUTILITY_H
#define NATIVE_JS_ANDROID_WRAPPERUTILITY_H


#include <JavaInterface/JavaScriptCore.h>
#include <list>
#include <JavaScriptCore/JavaScript.h>
#include "ValueWrapper.h"
#include "ContextWrapper.h"

class WrapperUtility {

public:
    /**
     * 获取 Class 路径名
     */
    static std::string JNI_PackageClass(std::string className);

    /**
     * 获取 Class JNI 签名
     */
    static std::string JNI_ClassSignature(std::string className);

    /**
     * 获取 Array JNI 签名
     */
    static std::string JNI_ArraySignature(std::string className);

    /**
     * 获取 Method JNI 签名
     */
    static std::string JNI_MethodSignature(std::string retSign, std::list<std::string> argSigns);

    /**
     * 获取 Class 类名
     */
    static std::string JNI_ClassName(jobject clazz);

    /**
     * JSValueRef Array 转 JNI Long Array
     */
    static jobjectArray JNI_NewValueArray(JSGlobalContextRef contextRef, size_t length, JSValueRef *arguments);

    /**
     * 调用 Call JS Function
     */
    static JSValueRef JNI_CallJSFunction(JSContextRef ctx, JSObjectRef function, JSObjectRef thisObject, size_t argumentCount, const JSValueRef arguments[], jobject handler);

    /**
     * 调用 JS Getter Function
     */
    static JSValueRef JNI_CallJSGetter (JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, jobject handler);

    /**
     * 调用 JS Setter Function
     */
    static void JNI_CallJSSetter(JSContextRef ctx, JSObjectRef object, JSStringRef propertyName, JSValueRef value, jobject handler);

    /**
     * 调用 Call JS Function
     */
    static std::string JNI_GetFunctionName(JSContextRef ctx, JSObjectRef function);

    /**
     * JS String 转换
     */
    static std::string JNI_Convert2StdString(JSStringRef string);

    /**
     * JS String 转换
     */
    static jstring JNI_Convert2JavaString(JSStringRef string);

    /**
     * JS String 转换
     */
    static JSStringRef JNI_Convert2JSString(jstring string);

    /**
     * char -> jstring
     */
    static jstring JNI_ConvertChar2JString(const char* c_str);

    /**
     * jsvalue to java object
     */
    static jobject JNI_Convert2JavaMap(JSValueRef jsvalue, JSGlobalContextRef contextRef);

    /**
     * jsvalue to java object
     */
    static jobject JNI_Convert2JavaObject(JNIEnv *env, JSValueRef jsvalue, JSGlobalContextRef contextRef);
};


#endif //NATIVE_JS_ANDROID_WRAPPERUTILITY_H
