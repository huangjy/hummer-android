package com.hummer.core.jni;

public class JSStaticFunction {
    String name;
    JSCallAsFunction callback;
    int attributes;

    public JSStaticFunction(String name, JSCallAsFunction callback, int attributes){
        this.name = name; this.callback = callback; this.attributes = attributes;
    }
}
