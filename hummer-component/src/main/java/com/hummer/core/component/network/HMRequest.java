package com.hummer.core.component.network;

import android.content.Context;
import android.util.Log;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.base.NativeModule;
import com.hummer.core.component.network.delegate.IRequestDelegate;
import com.hummer.core.component.network.delegate.RequestConnectionDelegate;
import com.hummer.core.jni.JSContext;
import com.hummer.core.jni.JSValue;
import com.google.gson.Gson;

import java.util.HashMap;

@HM_EXPORT_CLASS("Request")
public class HMRequest implements NativeModule {
    public static String METHOD_GET = "GET";
    public static String METHOD_POST = "POST";

    private int responseCode = 200;

    private static IRequestDelegate mRequestDelegate = new RequestConnectionDelegate();

    public HMRequest(Context context, JSValue[] jsValues) {
        this.method = "POST";
        this.timeout = 10000;//单位：毫秒
        this.url = "";
    }

    @Override
    public String getName() {
        return "HMRequest";
    }

    @Override
    public void destroy() {
    }

    public static void setRequestDelegate(IRequestDelegate requestDelegate) {
        mRequestDelegate = requestDelegate;
    }

    @HM_EXPORT_PROPERTY("url")
    public String url;
    public void setUrl(JSValue api) {
        this.url = api.toCharString();
    }

    /**
     * 请求方式：POST或者GET(不区分大小写)
     */
    @HM_EXPORT_PROPERTY("method")
    public String method;
    public void setMethod(JSValue method) {
        this.method = method.toCharString().toUpperCase();
    }

    /**
     * 超时时间 default 10s
     */
    @HM_EXPORT_PROPERTY("timeout")
    public int timeout;
    public void setTimeout(JSValue timeout) {
        this.timeout = (int)timeout.toNumber()*1000;
    }

    /**
     * 网络请求头部
     */
    @HM_EXPORT_PROPERTY("header")
    public HashMap requestHeader;
    public void setRequestHeader(JSValue requestHeader) {
        this.requestHeader = (HashMap) requestHeader.toObject();
    }

    /**
     * 网络请求的参数
     */
    @HM_EXPORT_PROPERTY("param")
    public HashMap<String, Object> param;
    public void setParam(JSValue param) {
        this.param = (HashMap) param.toObject();
    }

    /**
     * 发起网络请求
     * @param func 回调
     */
    @HM_EXPORT_METHOD("send")
    public void send(JSValue func){
        mRequestDelegate.send(url, method, timeout, requestHeader, param,
            new IRequestCallback() {
                @Override
                public void onComplete(String result) {
                    Log.i("HMRequest","++++++++>>>>POST: " + result);
                    JSValue retJSVal = JSValue.makeFromJSON(result, func.getContext());
                    JSValue[] ret = {retJSVal};
                    func.call(ret);
                }

                @Override
                public void onError(Exception e) {
                    HashMap<String, String> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    JSValue retJSVal = JSValue.makeObject(0, error, func.getContext());
                    JSValue[] ret = {retJSVal};
                    func.call(ret);
                }
            }
        );
    }

    /**
     * 网络请求完成后的http状态码，code参考https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
     * @return http状态码
     */
    @HM_EXPORT_METHOD("getStatus")
    public JSValue getStatus(){
        Gson gson = new Gson();
        return JSValue.makeFromJSON(gson.toJson(responseCode),JSContext.create());
    }

    /**
     * 网络请求的链接
     * @return 返回网络请求链接
     */
    @HM_EXPORT_METHOD("getRequestURL")
    public String getRequestURL() {
        return this.url;
    }
}
