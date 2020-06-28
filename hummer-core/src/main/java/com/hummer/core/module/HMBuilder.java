package com.hummer.core.module;

import android.support.annotation.NonNull;

import com.hummer.core.module.handler.WebImageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: linjizong
 * @date: 2019/4/11
 * @desc:
 */
public class HMBuilder {
    private static final String TAG="NJModule";
    private WebImageHandler mWebImageHandler;

    public HMBuilder(){
    }

    public void setWebImageHandler(@NonNull WebImageHandler handler) {
        mWebImageHandler = handler;
    }

    public Map<Class,Object> build(){
         Map<Class,Object> map=new HashMap<>();
         map.put(WebImageHandler.class,mWebImageHandler);
         return map;
    }


}
