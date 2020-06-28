package com.hummer.core.jni;

public class JSStaticVariable {

    String name;
    JSGetProperty getProperty;
    JSSetProperty setProperty;
    int attributes;

    public JSStaticVariable(String name, JSGetProperty getProperty, JSSetProperty setProperty, int attributes){
        this.name = name; this.getProperty = getProperty; this.setProperty = setProperty; this.attributes = attributes;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        name = null;
        getProperty = null;
        setProperty = null;
        attributes = 0;
    }
}
