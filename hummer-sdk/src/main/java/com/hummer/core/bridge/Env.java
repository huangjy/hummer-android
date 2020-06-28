package com.hummer.core.bridge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

import com.hummer.core.jni.JSValue;
import com.hummer.core.utility.HMConverter;

import java.util.HashMap;

public class Env {

    public static HashMap<String, Object> sContextInfo = new HashMap<>();
    public static HMEnvListener mEnvListener;

    public Env(Context context, JSValue[] args) {}

    public static void put(String key, Object value) {
        sContextInfo.put(key, value);
        if (mEnvListener != null) {
            mEnvListener.onUpdate(sContextInfo);
        }
    }

    /**
     * 获取APP环境参数
     * @return 返回map
     */
    public static void initEnvironment(Context ctx) {
        Context context = ctx;
        String platform = "Android";
        String appName = getApplicationName(context);
        String appVersion = "";
        try {
            appVersion = getApplicationVersion(context);
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String sysVersion = Build.VERSION.RELEASE;
        int deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int deviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        float scale = context.getResources().getDisplayMetrics().density;

        deviceWidth = (int) HMConverter.px2dp(context,deviceWidth);
        deviceHeight = (int) HMConverter.px2dp(context,deviceHeight);

        sContextInfo.put("platform",platform);
        sContextInfo.put("appName",appName);
        sContextInfo.put("appVersion",appVersion);
        sContextInfo.put("sysVersion",sysVersion);
        sContextInfo.put("deviceWidth",deviceWidth);
        sContextInfo.put("deviceHeight",deviceHeight);
        sContextInfo.put("availableWidth",deviceWidth);
        sContextInfo.put("availableHeight",deviceHeight);
        sContextInfo.put("scale",scale);
    }

    /**
     * 获取app名称
     * @param context Context对象
     * @return
     */
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    /**
     * 获取app版本号
     * @param context Context对象
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getApplicationVersion(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
    }

    public interface HMEnvListener {
        void onUpdate(HashMap<String, Object> newEnv);
    }

}
