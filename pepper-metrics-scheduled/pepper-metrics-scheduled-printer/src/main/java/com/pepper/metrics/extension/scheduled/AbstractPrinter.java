package com.pepper.metrics.extension.scheduled;

import com.pepper.metrics.core.Stats;
import com.pepper.metrics.extension.scheduled.domain.PrinterDomain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 *
 * @author zhiminxu
 * @package com.pepper.metrics.extension.scheduled
 * @create_time 2019-08-07
 */
public abstract class AbstractPrinter implements IPrinter {

    private static final Logger pLogger = LoggerFactory.getLogger("performance");

    protected static String PREFIX = "";

    @Override
    public void print(Stats stats) {
        setPrefix(stats);

        List<PrinterDomain> printerDomain = collector(stats);

        doPrint(printerDomain);
    }

    private void doPrint(List<PrinterDomain> printerDomain) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(PREFIX).append("]");
        sb.append(StringUtils.repeat("-", 50));
        pLogger.info(sb.toString());



        for (PrinterDomain domain : printerDomain) {
            StringBuilder s = new StringBuilder();
            s.append("[").append(PREFIX).append("]");

            s.append(" | ").append(domain.getTag());
            s.append(" | ").append(domain.getMax());
            s.append(" | ").append(domain.getConcurrent());
            s.append(" | ").append(domain.getErr());
            s.append(" | ").append(domain.getSum());
            s.append(" | ").append(domain.getP90());
            s.append(" | ").append(domain.getP99());
            s.append(" | ").append(domain.getP999());
            s.append(" | ").append(domain.getQps());
            pLogger.info(s.toString());
        }
        pLogger.info(sb.toString());

    }
}
