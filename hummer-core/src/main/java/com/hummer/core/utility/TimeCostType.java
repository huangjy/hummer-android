package com.hummer.core.utility;

/**
 * NOTE: 前缀TC:time cost  TTC:total time cost
 */
public class TimeCostType {

    /** 未知类型 */
    public static final String NJ_TC_UNKOWN                             = "NJ_TC_UNKOWN";
    /** JS执行和页面渲染总耗时 */
    public static final String NJ_TTC_JSEXECUTE_RENDER_AND_COMPLETE     = "NJ_TTC_JSEXECUTE_RENDER_AND_COMPLETE";
    /** JS执行总时长耗时 */
    public static final String NJ_TTC_JSEXECUTE_COMPLETE                = "NJ_TTC_JSEXECUTE_COMPLETE";
    /** 导出类收集耗时 */
    public static final String NJ_TC_EXPORT_CLASSES                     = "NJ_TC_EXPORT_CLASSES";
    /** 导出Class个数 */
    public static final String HM_EXPORT_CLASSES_COUNT                  = "HM_EXPORT_CLASSES_COUNT";
    /** 模块初始化总时间 */
    public static final String NJ_TTC_NATIVEJS_MODULE_INIT              = "NJ_TTC_NATIVEJS_MODULE_INIT";
    /** 拼接和执行JS脚本耗时 */
    public static final String NJ_TC_JS_SPLIT_AND_EXECUTE               = "NJ_TC_JS_SPLIT_AND_EXECUTE";
    /** 业务JS下载耗时 */
    public static final String NJ_TC_BIZ_JS_DOWNLOAD                    = "NJ_TC_BIZ_JS_DOWNLOAD";
    /** 从缓存中寻找业务JS所在路径耗时 */
    public static final String NJ_TC_BIZ_JS_SCAN_PATH_IN_CACHE          = "NJ_TC_BIZ_JS_SCAN_PATH_IN_CACHE";
    /** 获取业务JS耗时 */
    public static final String NJ_TC_LOAD_BIZ_JS                        = "NJ_TC_LOAD_BIZ_JS";
    /** 业务js执行耗时 */
    public static final String NJ_TC_BIZ_JS_EXECUTE                     = "NJ_TC_BIZ_JS_EXECUTE";
    /** 本地运行耗时 */
    public static final String NJ_TC_NATIVE_EXECUTE_COMPLETE            = "NJ_TC_NATIVE_EXECUTE_COMPLETE";
}
