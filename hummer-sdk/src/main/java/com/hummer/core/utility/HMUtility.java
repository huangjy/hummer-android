package com.hummer.core.utility;

import android.content.Context;
import android.util.Log;

import com.hummer.core.Hummer;
import com.hummer.core.base.HMExportClass;
import com.hummer.core.base.HMExportMethod;
import com.hummer.core.manager.HMExportManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class HMUtility {
    /**
     * 内部类，获取单例使用
     */
    private static class HMUtilityHold {
        public static HMUtility instance = new HMUtility();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HMUtility sharedInstance() {
        return HMUtilityHold.instance;
    }

    /**
     * JSON反序列化
     *
     * @param object 待反序列化对象
     * @return
     */
    private static String _NJJSONStringWithObject(Object object) {
        if (object == null) return null;

        if (object instanceof ArrayList) {//JSONArray
            JSONArray jsonArray = HMUtility.sharedInstance().getJsonFromArray((ArrayList) object);
            return jsonArray.toString();
        } else if (object instanceof Map) {//JSONObject
            JSONObject jsonObject = HMUtility.sharedInstance().getJsonFromMap((Map) object);
            return jsonObject.toString();
        }

        return null;
    }

    public static String HMJSONEncode(Object object) {
        return _NJJSONStringWithObject(object);
    }

    /**
     * Map转换成JSONObject
     *
     * @param map Map类型
     * @return JSONObject对象
     */
    private JSONObject getJsonFromMap(Map<String, Object> map) {
        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                if (value instanceof Map<?, ?>) {
                    value = getJsonFromMap((Map<String, Object>) value);
                } else if (value instanceof ArrayList) {
                    value = getJsonFromArray((ArrayList<String>) value);
                }
                jsonData.put(key, value);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonData;
    }

    /**
     * ArrayList转换成JSONArray
     *
     * @param array ArrayList类型
     * @return JSONArray对象
     */
    private JSONArray getJsonFromArray(ArrayList array) {
        return new JSONArray(array);
    }

//    /**
//     * JSValue 转换成 Object
//     * @param value JSValue对象
//     * @return Object对象
//     */
//    public static  Object JSValue2Object(JSValue value) {
//        if (value.isObject()) {
//            JSValue jsValue = value.get("private");
//
//            return jsValue.privateData();
//        }
//        return null;
//    }
//
//    /**
//     * Object 转换成 JSValue
//     * @param obj 转换Object对象
//     * @param ctx js上下文
//     * @return JSValue对象
//     */
//    public static JSValue Object2JSValue(Object obj, JSContext ctx) {
//        return  new JSValue(null,obj,ctx);
//    }

    /**
     * 查找给定对象的setter方法，并执行setter方法
     *
     * @param obj   给定对象
     * @param attr  属性名称
     * @param value 属性值
     * @return
     */
    public static boolean callSetter(Object obj, String attr, Object value) {
        String javaClass = obj.getClass().getName();
        HMExportManager exportManager = Hummer.getInstance().getHMExportManager();
        HMExportClass exportClass = exportManager.exportClassForJava(javaClass);
        if (exportClass == null) {
            Log.e("HMUtility", "Java class [" + javaClass + "] which export can not be found!");
            return false;
        }

        HMExportMethod exportMethod = exportClass.methodForAttr(attr);
        if (exportMethod == null) {
            Log.e("HMUtility", "JS attr [" + attr + "] which export method can not be found!");
            return false;
        }
        Method m = exportMethod.javaMethod;
        if (m != null) {
            try {
                Class[] parameterTypes = m.getParameterTypes();
                Object ret = HMConverter.doConverter(value, parameterTypes[0]);
                m.invoke(obj, ret);
                return true;
            } catch (IllegalAccessException ex) {
                Log.e("HMUtility", "----->>>>", ex);
            } catch (IllegalArgumentException ex) {
                Log.e("HMUtility", "----->>>>", ex);
            } catch (InvocationTargetException ex) {
                Log.e("HMUtility", "----->>>>", ex);
            }
        }

        return false;
    }
//
//    private static synchronized Method findMethod(Object obj, String property, Object value){
//        Method m = null;
//        Class<?> theClass = obj.getClass();
//        String setter = String.format("set%C%s", property.charAt(0), property.substring(1));
//        Class paramType = value.getClass();
//        if (paramType != null) {
//            try {
//                m = theClass.getMethod(setter,paramType);
//            } catch (NoSuchMethodException ex) {
//                // try on the interfaces of this class
//                for (Class iface : paramType.getInterfaces()) {
//                    try {
//                        m = theClass.getMethod(setter, iface);
//                        return m;
//                    } catch (NoSuchMethodException ex1) {
//                    }
//                }
//                paramType = paramType.getSuperclass();
//            }
//        }
//
//        return m;
//    }
//
//    private static synchronized Method findMethod(Object obj, String property){
//        Method m = null;
//        Class<?> theClass = obj.getClass();
//        String setter = String.format("set%C%s", property.charAt(0), property.substring(1));
//        Method[] methods = theClass.getMethods();
//        for (Method method:methods) {
//            String methodName = method.getName();
//            if (methodName.equals(setter)) {
//                m = method;
//                break;
//            }
//        }
//
//        return m;
//    }

    /**
     * 通过包名、资源名、文件名 查找资源Id
     *
     * @param context       Context
     * @param pVariableName 文件名
     * @param pResourceName 资源名 e.g. drawable
     * @param pPackageName  包名
     * @return
     * @throws RuntimeException
     */
    public static int getResourceId(Context context, String pVariableName, String pResourceName, String pPackageName) throws RuntimeException {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourceName, pPackageName);
        } catch (Exception ex) {
            throw new RuntimeException("Error getResourceId", ex);
        }
    }

    /**
     * 通过包名、资源名、文件名 查找资源Id
     *
     * @param pVariableName 文件名
     * @param pResourceName 资源名 e.g. drawable
     * @param pPackageName  包名
     * @return
     * @throws RuntimeException
     */
    public static int getResourceId(String pVariableName, String pResourceName, String pPackageName) throws RuntimeException {
        try {
            if (pPackageName == null)
                pPackageName = HMContextUtil.getContext().getPackageName();
            return HMContextUtil.getContext().getResources().getIdentifier(pVariableName, pResourceName, pPackageName);
        } catch (Exception ex) {
            throw new RuntimeException("Error getResourceId by HMContextUtil.getContext()", ex);
        }
    }

}
