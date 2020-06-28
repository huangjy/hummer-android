//
// Created by huangjy on 2018/11/15.
//

#include "JavaScriptCore.h"
#include <CppWrapper/ClassWrapper.h>
#include <JavaScriptCore/JSObjectRef.h>
#include <CppWrapper/ValueWrapper.h>

NATIVE(JSClass, jlong, create)(JNIEnv *env, jobject thiz, jobject clsDef, jlong contextRef){
    return (long)(new ClassWrapper(clsDef, contextRef));
}

NATIVE(JSClass, void, destroy)(JNIEnv *env, jobject thiz, jlong clazz, jlong contextRef){
    if(clazz == 0) return;
    delete ((ClassWrapper *)clazz);
}