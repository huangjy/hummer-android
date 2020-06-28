package com.hummer.core.component;

import com.hummer.core.jni.JSValue;

public interface ComInterface {

    /**
     * 设置style属性
     *
     * @param value
     */
    void setStyle(JSValue value);

    void setViewID(String viewID);

    void addEventListener(JSValue eventListener);

    void addAnimation(JSValue anim, JSValue id);

    void removeAnimationForKey(JSValue id);

    void removeAllAnimation();

    void setHidden(boolean hidden);
}
