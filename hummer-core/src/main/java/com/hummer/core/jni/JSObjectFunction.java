package com.hummer.core.jni;

public class JSObjectFunction {
    public String name;
    public JSCallAsFunction callback;

    public JSObjectFunction(String name, JSCallAsFunction callback){
        this.name = name; this.callback = callback;
    }
}
