package com.hummer.core.component.notification;

import com.hummer.core.jni.JSValue;

/**
 * @author: linjizong
 * @date: 2019/4/21
 * @desc:
 */
public interface NJCallback {
    void call(JSValue objc);
}
