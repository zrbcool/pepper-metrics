package com.pepper.metrics.core.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class MetricsNameBuilder {

    private static final String PREFIX = "pepper.";

    private static final String SPLIT_SYMBOL = ".";

    private MetricsType metricsType;

    private String type;

    private String subType;

    private List<String> nameList = new CopyOnWriteArrayList<>();

    public static MetricsNameBuilder builder() {
        return new MetricsNameBuilder();
    }

    public MetricsNameBuilder setMetricsType(MetricsType metricsType) {
        this.metricsType = metricsType;
        return this;
    }

    public MetricsNameBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public MetricsNameBuilder setSubType(String subType) {
        this.subType = subType;
        return this;
    }

    public MetricsNameBuilder setName(String name) {
        this.nameList.add(name);
        return this;
    }

    public String build() {
        StringBuilder metricsName = new StringBuilder(PREFIX);
        metricsName.append(metricsType.getName()).append(SPLIT_SYMBOL)
                .append(type).append(SPLIT_SYMBOL)
                .append(subType).append(SPLIT_SYMBOL);

        for (int index = 0; index < nameList.size(); index++) {
            if (index == nameList.size() - 1) {
                metricsName.append(nameList.get(index));
            } else {
                metricsName.append(nameList.get(index)).append(SPLIT_SYMBOL);
            }
        }

        return metricsName.toString();
    }


}
