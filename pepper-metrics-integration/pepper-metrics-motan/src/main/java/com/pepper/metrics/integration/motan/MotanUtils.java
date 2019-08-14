package com.pepper.metrics.integration.motan;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhangrongbin on 2018/11/08.
 */
public class MotanUtils {

    public static String getShortName(String str) {
        if (StringUtils.isNotEmpty(str)) {
            final int i1 = StringUtils.lastIndexOf(str, '.');
            final int i2 = StringUtils.lastIndexOf(str, '.', i1 - 1);
            return StringUtils.substring(str, i2 + 1);
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println(getShortName("com.coohua.app.sample.rpc.api.IDemoMotanComplexService"));
        System.out.println(getShortName("java.lang.String"));
        System.out.println(getShortName("Void"));
        System.out.println(getShortName("IAddService"));
    }
}
