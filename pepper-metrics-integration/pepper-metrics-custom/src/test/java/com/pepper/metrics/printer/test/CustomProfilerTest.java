package com.pepper.metrics.printer.test;

import com.pepper.metrics.integration.custom.CustomProfiler;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangrongbincool@163.com
 * @version 19-11-1
 */
public class CustomProfilerTest {

    @Test
    public void test()  {
        final CustomProfiler.Procedure procedure = CustomProfiler.beginProcedure();
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (Throwable e) {
            try {
                procedure.exception(e);
            } catch (Throwable ignored) {
            }
        } finally {
            procedure.complete();
        }
    }
}
