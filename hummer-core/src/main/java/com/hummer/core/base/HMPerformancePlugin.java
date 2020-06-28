package com.hummer.core.base;

public interface HMPerformancePlugin {
    /**
     * 输出性能统计信息
     *
     * @param key 性能类型，TimeCostType.*
     * @param value 性能值，单位ms
     */
    void printPerformance(String key, int value);
}
