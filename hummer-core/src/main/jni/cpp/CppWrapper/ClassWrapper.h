//
// Created by huangjy on 2018/11/15.
//

#ifndef NATIVE_JS_ANDROID_CLASSWRAPPER_H
#define NATIVE_JS_ANDROID_CLASSWRAPPER_H

#include <JavaScriptCore/JavaScript.h>
#include <JavaInterface/JavaScriptCore.h>

class ClassWrapper {
private:
    jobject m_jvdef;
    JSClassRef m_jscls;
    JSClassDefinition definition;
    jlong contextRef;

public:
    ClassWrapper(jobject jvdef, jlong ctxRef);
    ~ClassWrapper();
    JSClassRef classRef(){ return m_jscls; };

private:
    void setJSClassDef(JSClassDefinition *classdef);
    void setJSStaticValue(JSStaticValue *staticValue, jobject jvValue);
    void setJSStaticFunction(JSStaticFunction *staticFunction, jobject jvValue);
};


#endif //NATIVE_JS_ANDROID_CLASSWRAPPER_H
