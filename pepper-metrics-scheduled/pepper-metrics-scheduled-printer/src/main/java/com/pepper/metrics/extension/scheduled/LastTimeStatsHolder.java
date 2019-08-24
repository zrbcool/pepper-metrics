package com.pepper.metrics.extension.scheduled;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-10
 */
public class LastTimeStatsHolder {

    // 记录上一次的错误数，当前时间窗口的错误数 = 本次累计错误数 - 上次记录的错误数
    public volatile static ConcurrentMap<String, ConcurrentMap<List<String>, Double>> lastTimeErrCollector = new ConcurrentHashMap<>();
    // 记录上一次的请求总数，当前时间窗口的请求数 = 本次累计的请求数 - 上一次记录的请求数
    public volatile static ConcurrentMap<String, ConcurrentMap<List<String>, Long>> lastTimeSummaryCollector = new ConcurrentHashMap<>();

}
