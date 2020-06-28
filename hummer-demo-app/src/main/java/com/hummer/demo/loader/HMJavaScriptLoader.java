package com.hummer.demo.loader;

import com.hummer.core.component.network.IRequestCallback;
import com.hummer.core.component.network.HMNetworkMethod;
import com.hummer.core.component.network.HMNetworking;

public class HMJavaScriptLoader {
    public static void loadBundleWithURL(String urlPath, IRequestCallback callback) {
//        if (0) {
//            本地读取
//        }
        HMNetworking.sendHttpRequest(HMNetworkMethod.GET,urlPath,callback);
    }
}
