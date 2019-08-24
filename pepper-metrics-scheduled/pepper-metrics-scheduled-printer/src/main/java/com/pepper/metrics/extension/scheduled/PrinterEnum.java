package com.pepper.metrics.extension.scheduled;

/**
 * Description:
 *  可打印的维度
 * @author zhiminxu
 * @version 2019-08-07
 */
public enum PrinterEnum {

    ALL("全部（默认）"),
    MAX("最大响应时间"),
    CONCURRENT("当前并发数"),
    ERROR("错误数"),
    SUM("请求总数"),
    P90("p90响应延迟时间"),
    P99("p99响应延迟时间"),
    P999("p999响应延迟时间"),
    QPS("平均QPS");

    private String desc;

    PrinterEnum(String desc) {
        this.desc = desc;
    }

}
