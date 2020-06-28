package com.hummer.core.component.notification;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.core.event.HMEventManager;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("NotifyCenter")
public class NotifyCenter {
    private static HMEventManager mEventManager;

    static {
        mEventManager = new HMEventManager();
        mEventManager.onCreate();
    }

    public NotifyCenter(Context context, JSValue[] args) {}

    public static void onDestroy() {
        mEventManager.onDestroy();
    }

    /**
     * 内部类，装载时创建单例
     */
    private static class NJNotificationHolder {
        public static NotifyCenter instance = new NotifyCenter(null, null);
    }

    /**
     * 获取单例
     *
     * @return 实例对象
     */
    public static NotifyCenter defaultManager() {
        return NJNotificationHolder.instance;
    }

    @HM_EXPORT_METHOD("addEventListener")
    public static void addEventListener(JSValue key, JSValue callback) {
        mEventManager.addEventListener(
                key.toCharString(), callback);
    }

    @HM_EXPORT_METHOD("removeEventListener")
    public static void removeEventListener(JSValue key, JSValue callback) {
        mEventManager.removeEventListener(
                key.toCharString(), callback);
    }

    @HM_EXPORT_METHOD("triggerEvent")
    public static void triggerEvent(JSValue key, JSValue objc) {
        mEventManager.dispatchEvent(key.toCharString(), jsContext -> objc);
    }
}
