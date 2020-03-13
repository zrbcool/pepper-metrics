package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.core.extension.Scope;
import com.pepper.metrics.core.extension.Spi;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Description:
 *  Performance信息日志打印，可通过SPI方式进行扩展。
 *
 * @author zhiminxu
 * @version 2019-08-07
 */
@Spi(scope = Scope.SINGLETON)
public interface PerfPrinter {

    /**
     * 选取需要打印的Stats，通常根据name选取，由于同一个name下可能有多个namespace，所以这里会返回一个数组。
     * 数组中的元素通常具有相同的name属性，但具备不同的namespace属性。
     *
     * @param statsSet 统计信息
     * @return 筛选后的集合
     */
    List<Stats> chooseStats(Set<Stats> statsSet);

    /**
     *
     * 打印日志
     * statsSet 统计信息
     */
    void print(Set<Stats> statsSet, String timestamp, ConcurrentMap<String, ConcurrentMap<List<String>, Double>> currentErrCollector, ConcurrentMap<String, ConcurrentMap<List<String>, Long>> currentSummaryCollector);

    /**
     * <pre>
     * 定义日志前缀，继承AbstractPerfPrinter后，具备默认实现
     *
     * 默认实现前缀格式：
     *      perf-[type]-[namespace]
     * </pre>
     */
    String setPrefix(Stats stats);

    /**
     * 定义日志第一列【Metrics】的名称格式
     * @param stats 统计信息
     * @param tags  当前数据信息
     * @return Metrics名称
     */
    String setMetricsName(Stats stats, List<String> tags);

}
