package com.hummer.core.jni;


public class JSClassDefinition {
    public String className;
    public JSCallAsFunction callAsFunction;
    public JSStaticFunction[] functions;
    public JSStaticVariable[] variables;

    protected void finalize() throws Throwable {
        super.finalize();
        className = null;
        callAsFunction = null;
        functions = null;
        variables = null;
    }
}
