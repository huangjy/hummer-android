package com.hummer.core.component.network;

/**
 * 网络请求回调 主线程中使用
 */
public interface IRequestCallback {
    /**
     * 完成时调用
     * @param result
     */
    void onComplete(String result);

    /**
     * 出错时调用
     */
    void onError(Exception e);
}
