package com.hummer.core.base;

import java.lang.reflect.Method;

public class HMExportMethod {
    public String funcName;
    public Method javaMethod;
    public HMMethodType methodType;

    public enum HMMethodType {
        HMInstanceMethod,
        HMClassMethod
    }

    public HMExportMethod copy(){
        HMExportMethod method = new HMExportMethod();
        method.funcName = funcName;
        method.javaMethod = javaMethod;
        method.methodType = methodType;
        return method;
    }
}
