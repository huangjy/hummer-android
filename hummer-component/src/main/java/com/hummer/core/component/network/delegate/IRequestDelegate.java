package com.hummer.core.component.network.delegate;

import com.hummer.core.component.network.IRequestCallback;

import java.util.HashMap;

/**
 * HMRequest 模块请求代理，用于实际执行网络请求
 */
public interface IRequestDelegate {
    /**
     * 发起网络请求
     * @param url       API请求路径
     * @param method    请求方式：POST或者GET(不区分大小写)
     * @param timeout   超时时间
     * @param header    网络请求头部
     * @param param     网络请求的参数
     * @param callback  网络请求回调
     */
    void send(String url,
              String method,
              int timeout,
              HashMap<String, Object> header,
              HashMap<String, Object> param,
              IRequestCallback callback);
}
