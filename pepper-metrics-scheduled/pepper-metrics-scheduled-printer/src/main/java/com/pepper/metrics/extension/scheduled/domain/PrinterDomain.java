package com.pepper.metrics.extension.scheduled.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 *
 * @author zhiminxu
 * @version 2019-08-07
 */
public class PrinterDomain implements Comparable<PrinterDomain> {

    // 具体监控项 名称
    private String tag;

    // 最大响应时间
    private String max;

    // 当前并发数
    private String concurrent;

    // 60秒内的错误数
    private String err;

    // 60秒内请求总数
    private String sum;

    // 60秒内P90响应延迟
    private String p90;

    // 60秒内P99响应延迟
    private String p99;

    // 60秒内P999响应延迟
    private String p999;

    // 60秒的平均qps
    private String qps;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(String concurrent) {
        this.concurrent = concurrent;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getP90() {
        return p90;
    }

    public void setP90(String p90) {
        this.p90 = p90;
    }

    public String getP99() {
        return p99;
    }

    public void setP99(String p99) {
        this.p99 = p99;
    }

    public String getP999() {
        return p999;
    }

    public void setP999(String p999) {
        this.p999 = p999;
    }

    public String getQps() {
        return qps;
    }

    public void setQps(String qps) {
        this.qps = qps;
    }

    @Override
    public String toString() {
        return "PrinterDomain{" +
                "tag='" + tag + '\'' +
                ", max='" + max + '\'' +
                ", concurrent='" + concurrent + '\'' +
                ", err='" + err + '\'' +
                ", sum='" + sum + '\'' +
                ", p90='" + p90 + '\'' +
                ", p99='" + p99 + '\'' +
                ", p999='" + p999 + '\'' +
                ", qps='" + qps + '\'' +
                '}';
    }

    @Override
    public int compareTo(PrinterDomain other) {
        int thisSum = StringUtils.isNotEmpty(this.sum) ? Integer.parseInt(this.sum) : 0;
        int otherSum = StringUtils.isNotEmpty(other.sum) ? Integer.parseInt(other.sum) : 0;
        return Integer.compare(thisSum, otherSum);
    }
}
