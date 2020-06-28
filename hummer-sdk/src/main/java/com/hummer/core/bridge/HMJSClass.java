package com.hummer.core.bridge;

import android.util.Log;

import com.hummer.core.Hummer;
import com.hummer.core.base.HMExportClass;
import com.hummer.core.base.HMExportMethod;
import com.hummer.core.base.HMExportProperty;
import com.hummer.core.jni.JSCallAsFunction;
import com.hummer.core.jni.JSClass;
import com.hummer.core.jni.JSClassDefinition;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSGetProperty;
import com.hummer.core.jni.JSPropertyAttributes;
import com.hummer.core.jni.JSSetProperty;
import com.hummer.core.jni.JSStaticFunction;
import com.hummer.core.jni.JSStaticVariable;
import com.hummer.core.jni.JSValue;
import com.hummer.core.manager.HMExportManager;
import com.hummer.core.utility.HMConverter;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class HMJSClass {
    JSClass jsClass;
    public String className;
    ArrayList<String> jsMethods;
    ArrayList<String> jsProperties;
    JSClassDefinition classDefinition;

    JSGetPropertyHandler jsGetProperty;
    JSSetPropertyHandler jsSetProperty;
    JSCallAsFunctionHandler jsCallAsFunction;

    private static class JSCallAsFunctionHandler implements JSCallAsFunction {
        @Override
        public JSValue execute(JSContext ctx, JSValue func, JSValue thiz, int argCount, JSValue[] argArray) {
            return HMJSCallStaticFunction(ctx, func, thiz, argCount, argArray);
        }
    }

    private static class JSGetPropertyHandler implements JSGetProperty {
        @Override
        public JSValue execute(JSContext ctx, JSValue object, String name) {
            return HMJSGetStaticValue(ctx, object, name);
        }
    }

    private static class JSSetPropertyHandler implements JSSetProperty {
        @Override
        public void execute(JSContext ctx, JSValue object, String name, JSValue value) {
            HMJSSetStaticValue(ctx, object, name, value);
        }
    }

    /**
     * 创建JS Class
     *
     * @param clsName 类对象名称
     */
    public HMJSClass(String clsName) {
        className = clsName;
        classDefinition = new JSClassDefinition();
        classDefinition.className = className;
    }

    public long getClassRef() {
        return jsClass.getClassRef();
    }

    /**
     * 注册JS Class
     */
    public void registerJSClassRef(JSContext ctx) {
        loadJSClassExports();
        initInterfaceObject();

        // 注册静态方法
        createStaticJSFunctions(jsMethods);

        // 注册静态变量
        createStaticJSValues(jsProperties);
        jsClass = new JSClass(classDefinition, ctx);
    }

    /**
     * 卸载JSClassRef
     */
    public void unregisterJSClassRef() {
        this.jsGetProperty = null;
        this.jsSetProperty = null;
        this.jsCallAsFunction = null;
        this.classDefinition = null;
        this.jsClass = null;
        this.className = null;
        this.jsProperties = null;
        this.jsMethods = null;
    }

    /**
     * 获取Class
     *
     * @return JS Class define
     */
    public JSClassDefinition classRef() {
        return classDefinition;
    }

    /**
     * 初始化接口属性
     */
    private void initInterfaceObject() {
        jsCallAsFunction = new JSCallAsFunctionHandler();
        jsSetProperty = new JSSetPropertyHandler();
        jsGetProperty = new JSGetPropertyHandler();
    }

    /**
     * 加载JS导出类数据
     */
    private void loadJSClassExports() {
        if (className == null) return;

        HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
        HMExportClass exportClass = exportManager.exportClassForJS(className);

        this.jsMethods = exportClass.allExportMethodList();
        this.jsProperties = exportClass.allExportPropertyList();

        return;
    }

    /**
     * 创建静态funcs
     *
     * @param jsMethods
     */
    private void createStaticJSFunctions(ArrayList<String> jsMethods) {
        if (jsMethods == null || jsMethods.isEmpty()) return;

        classDefinition.functions = new JSStaticFunction[jsMethods.size()];
        int index = 0;
        for (String method : jsMethods) {
            JSStaticFunction function = new JSStaticFunction(method, jsCallAsFunction, JSPropertyAttributes.kJSPropertyAttributeReadOnly | JSPropertyAttributes.kJSPropertyAttributeDontEnum | JSPropertyAttributes.kJSPropertyAttributeDontDelete);
            classDefinition.functions[index++] = function;
        }
    }

    /**
     * 创建静态value
     *
     * @param jsProps
     */
    private void createStaticJSValues(ArrayList<String> jsProps) {
        classDefinition.variables = new JSStaticVariable[jsProps.size() + 2];

        for (int index = 0; index < jsProps.size() + 2; index++) {
            JSStaticVariable variable = null;
            if (index < jsProps.size()) {
                String jsProp = jsProps.get(index);
                variable = new JSStaticVariable(jsProp, jsGetProperty, jsSetProperty, JSPropertyAttributes.kJSPropertyAttributeDontDelete);
            } else {
                String name = null;
                if (index == jsProps.size()) {
                    name = "methods";
                }
                if (index == jsProps.size() + 1) {
                    name = "variables";
                }
                if (name != null) {
                    variable = new JSStaticVariable(name, jsGetProperty, null, JSPropertyAttributes.kJSPropertyAttributeDontDelete | JSPropertyAttributes.kJSPropertyAttributeReadOnly | JSPropertyAttributes.kJSPropertyAttributeDontEnum);
                }
            }
            classDefinition.variables[index] = variable;
        }
    }

    /**
     * 获取静态value
     *
     * @param ctx
     * @param object
     * @param name
     * @return
     */
    private static JSValue HMJSGetStaticValue(JSContext ctx, JSValue object, String name) {
        if (!object.isObject()) return JSValue.makeObject(0, null, ctx);

        Object thisObj = object.privateData();
        if (name.equals("methods")) {
            String clsName = thisObj.getClass().getName();
            HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
            HMExportClass exportClass = exportManager.exportClassForJava(clsName);
            ArrayList<String> allMethods = exportClass.allExportMethodList();
            JSONArray jsonArray = new JSONArray(allMethods);
            return JSValue.makeFromJSON(jsonArray.toString(), ctx);
        } else if (name.equals("variables")) {
            String clsName = thisObj.getClass().getName();
            HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
            HMExportClass exportClass = exportManager.exportClassForJava(clsName);
            ArrayList<String> allProps = exportClass.allExportPropertyList();
            JSONArray jsonArray = new JSONArray(allProps);
            return JSValue.makeFromJSON(jsonArray.toString(), ctx);
        } else if (thisObj != null) {
            return _HMJSCallGetter(thisObj, name, ctx);
        }

        return null;
    }

    /**
     * 调用静态funcs
     *
     * @param ctx
     * @param func
     * @param thiz
     * @param argCount
     * @param argArray
     * @return
     */
    private static JSValue HMJSCallStaticFunction(JSContext ctx, JSValue func, JSValue thiz, long argCount, JSValue[] argArray) {
        Object thisObj = thiz.privateData();
        String funcName = func.getFunctionName();

        if (thisObj == null) {
            return null;
        }

        Class javaClass = thisObj.getClass();
        HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
        HMExportClass exportClass = exportManager.exportClassForJava(javaClass.getName());
        if (exportClass == null) {
            Log.e("HMJSClass", "Java class [" + javaClass.getSimpleName() + "] which export can not be found!");
            return null;
        }

        HMExportMethod exportMethod = exportClass.methodForFuncName(funcName);
        if (exportMethod == null) {
            Log.e("HMJSClass", "JS method [" + funcName + "] which export can not be found!");
            return null;
        }
        if (exportMethod.methodType == HMExportMethod.HMMethodType.HMClassMethod) {
            return _HMJSCallFunc(exportMethod.javaMethod, ctx, null, argCount, argArray);
        } else if (exportMethod.methodType == HMExportMethod.HMMethodType.HMInstanceMethod) {
            return _HMJSCallFunc(exportMethod.javaMethod, ctx, thisObj, argCount, argArray);
        }

        return null;
    }

    /**
     * set静态value
     *
     * @param ctx
     * @param object
     * @param name
     * @param value
     */
    private static void HMJSSetStaticValue(JSContext ctx, JSValue object, String name, JSValue value) {
        Object thisObj = object.privateData();
        _HMJSCallSetter(ctx, thisObj, name, value);
    }

    private static JSValue _HMJSCallFunc(Method method, JSContext ctx, Object thisObj, long argCount, JSValue[] argArray) {
        JSValue jsValue = null;
        try {
            Object ret = method.invoke(thisObj, (Object[]) argArray);
            if (ret instanceof JSValue)
                jsValue = (JSValue) ret;
            else {
                Gson gson = new Gson();
                jsValue = JSValue.makeFromJSON(gson.toJson(ret), ctx);
            }
        } catch (Exception e) {
            Log.e("_HMJSCallFunc", "method:" + method.toString(), e);
        }

        return jsValue;
    }

    /**
     * getter方法调用
     *
     * @param object
     * @param jsProp
     * @param ctx
     * @return
     */
    private static JSValue _HMJSCallGetter(Object object, String jsProp, JSContext ctx) {
        if (jsProp == null) return null;

        String javaClass = object.getClass().getName();
        HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
        HMExportClass exportClass = exportManager.exportClassForJava(javaClass);

        if (exportClass == null) {
            Log.e("HMJSClass", "Java class [" + javaClass + "] which export can not be found!");
            return null;
        }

        HMExportProperty exportProperty = exportClass.propertyForName(jsProp);
        if (exportProperty == null) {
            Log.e("HMJSClass", "JS property [" + jsProp + "] which export can not be found!");
            return null;
        }

        String name = exportProperty.propField.getName();
        Object obj = null;
        if (exportProperty.getterMethod != null) {
            Method method = exportProperty.getterMethod;
            try {
                obj = method.invoke(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                obj = exportProperty.propField.get(object);
            } catch (IllegalAccessException IllE) {
                Log.e("HMJSClass", IllE.toString());
            }
        }

        if (obj == null) return null;

        if (obj instanceof JSValue) return (JSValue) obj;

        Gson gson = new Gson();
        return JSValue.makeFromJSON(gson.toJson(obj), ctx);
    }

    /**
     * setter方法调用
     *
     * @param ctx
     * @param object
     * @param propertyName
     * @param propertyValue
     */
    private static void _HMJSCallSetter(JSContext ctx, Object object, String propertyName, JSValue propertyValue) {
        if (propertyName == null) return;
        String javaClass = object.getClass().getName();
        HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
        HMExportClass exportClass = exportManager.exportClassForJava(javaClass);

        if (exportClass == null) {
            Log.e("HMJSClass", "Java class [" + javaClass + "] which export can not be found!");
            return;
        }

        HMExportProperty exportProperty = exportClass.propertyForName(propertyName);
        if (exportProperty == null) {
            Log.e("HMJSClass", "JS property [" + propertyName + "] which export can not be found!");
            return;
        }

        if (exportProperty.setterMethod != null) {
            Method method = exportProperty.setterMethod;
            try {
                method.invoke(object, propertyValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Log.e("HMJSClass", "JS property [" + propertyName + "] which setter method can not be found!");
                /// property需要把JSValue转成对应的object
                Class type = exportProperty.propField.getType();
                Object oriObj = propertyValue.toObject();
                Object curObj = HMConverter.doConverter(oriObj, type);
                exportProperty.propField.set(object, curObj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}



