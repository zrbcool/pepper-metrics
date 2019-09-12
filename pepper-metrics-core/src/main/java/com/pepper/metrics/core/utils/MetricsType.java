package com.pepper.metrics.core.utils;

/**
 * Description:
 *  指标类型
 * @author zhiminxu
 */
public enum MetricsType {
    GAUGE("gauge"),
    SUMMARY("summary"),
    COUNTER("counter");

    private String name;

    MetricsType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
