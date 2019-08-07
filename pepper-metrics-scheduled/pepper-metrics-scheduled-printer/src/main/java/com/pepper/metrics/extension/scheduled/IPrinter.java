package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.extension.scheduled.domain.PrinterDomain;

import java.util.List;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.extension.scheduled
 * @create_time 2019-08-07
 */
public interface IPrinter {

    /**
     * 定义日志前缀
     */
    void setPrefix(Stats stats);

    /**
     *
     * @param stats
     */
    List<PrinterDomain> collector(Stats stats);

    void print(Stats stats);
}
