package com.hummer.core.base;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.utility.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class HMExportClass {
    public String className;
    public String jsClass;
    public Hashtable<String, HMExportMethod> methods;
    public Hashtable<String, HMExportMethod> attrs;
    public Hashtable<String, HMExportProperty> variables;

    public HMExportClass(String className, String jsClass) {
        this.className = className;
        this.jsClass = jsClass;
        this.methods = new Hashtable<>();
        this.attrs = new Hashtable<>();
        this.variables = new Hashtable<>();
        loadAllExports(className);
    }

    /**
     * 获取方法列表
     *
     * @return 方法列表
     */
    public ArrayList<String> allExportMethodList() {
        ArrayList arrayList = new ArrayList(methods.keySet());
        return arrayList;
    }

    /**
     * 获取属性列表
     *
     * @return 属性列表
     */
    public ArrayList<String> allExportPropertyList() {
        ArrayList arrayList = new ArrayList(variables.keySet());
        return arrayList;
    }

    /**
     * 获取方法描述
     *
     * @param funcName 方法名称
     * @return 方法描述
     */
    public HMExportMethod methodForFuncName(String funcName) {
        if (funcName == null) {
            return null;
        }
        return methods.get(funcName);
    }

    /**
     * 获取属性描述
     *
     * @param propName JS属性名称
     * @return 属性描述
     */
    public HMExportProperty propertyForName(String propName) {
        if (propName == null) {
            return null;
        }
        return variables.get(propName);
    }

    /**
     * 根据属性返回对应的设置方法
     *
     * @param attr 属性名
     * @return
     */
    public HMExportMethod methodForAttr(String attr) {
        if (attr == null) {
            return null;
        }
        return attrs.get(attr);
    }

    private void loadAllExports(String className) {
        try {
            Class javaClass = Class.forName(className);
            // 获取 variable
            loadAllVariables(javaClass);
            // 获取 method
            loadAllMethods(javaClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAllMethods(Class javaClass) {
        Method[] allMethods = javaClass.getMethods();
        for (int i = allMethods.length - 1; i >= 0; i--) {
            Method method = allMethods[i];
            HMExportMethod exportMethod = new HMExportMethod();
            exportMethod.javaMethod = method;
            if (method.toString().contains(" static ")) {
                //类方法
                exportMethod.methodType = HMExportMethod.HMMethodType.HMClassMethod;
            } else {
                //实例方法
                exportMethod.methodType = HMExportMethod.HMMethodType.HMInstanceMethod;
            }

            if (processExportMethod(method, exportMethod)) {
                continue;
            }

            if (processExportAttr(method, exportMethod)) {
                continue;
            }

            for (Map.Entry<String, HMExportProperty> entry : variables.entrySet()) {
                if (processSetterMethod(entry.getValue(), method)) {
                    break;
                }
                if (processGetterMethod(entry.getValue(), method)) {
                    break;
                }
            }

        }
    }

    private boolean processExportMethod(Method method, HMExportMethod exportMethod) {
        HM_EXPORT_METHOD annotation = method.getAnnotation(HM_EXPORT_METHOD.class);
        if (annotation != null) {
            exportMethod.funcName = annotation.value();
            methods.put(exportMethod.funcName, exportMethod);
            return true;
        }
        return false;
    }

    private boolean processExportAttr(Method method, HMExportMethod exportMethod) {
        HM_EXPORT_ATTR annotation = method.getAnnotation(HM_EXPORT_ATTR.class);
        if (annotation != null) {
            String[] values = annotation.value();
            for (String value : values) {
                HMExportMethod copy = exportMethod.copy();
                copy.funcName = value;
                attrs.put(copy.funcName, copy);
            }
            return true;
        }
        return false;
    }

    private boolean processSetterMethod(HMExportProperty property, Method method) {
        String setterName = "set" + StringUtil.uppercaseFirstChar(property.propName);
        if (setterName.equals(method.getName())) {
            property.setterMethod = method;
            return true;
        }
        return false;
    }

    private boolean processGetterMethod(HMExportProperty property, Method method) {
        String getterName = "get" + StringUtil.uppercaseFirstChar(property.propName);
        if (getterName.equals(method.getName())) {
            property.getterMethod = method;
            return true;
        }
        return false;
    }

    private void loadAllVariables(Class javaClass) {
        Field[] fields = javaClass.getFields();
        for (int i = fields.length - 1; i >= 0; i--) {
            Field field = fields[i];
            HM_EXPORT_PROPERTY annotation = field.getAnnotation(HM_EXPORT_PROPERTY.class);
            if (annotation == null) {
                continue;
            }

            HMExportProperty exportProperty = new HMExportProperty();
            exportProperty.propName = annotation.value();
            exportProperty.propField = field;
            variables.put(exportProperty.propName, exportProperty);
        }
    }
}
