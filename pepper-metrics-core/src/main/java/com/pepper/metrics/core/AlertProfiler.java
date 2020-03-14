package com.pepper.metrics.core;

import java.util.ArrayList;
import java.util.List;

public class AlertProfiler extends Profiler {
    public static class Builder {
        private List<String> rules = new ArrayList<>();
        private String name;

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

//        public AlertMetrics.Builder label(String labelName, String labelValue) {
//            if (labels.size() > 2)
//                throw new IllegalArgumentException("only 3 labels are allowed!!!");
//            labels.add(new AlertMetrics.Label(labelName, labelValue));
//            return this;
//        }

        public Builder rule(String rule) {
            rules.add(rule);
            rules.add("true");
            return this;
        }

        public AlertStats create() {
            AlertStats stats = new AlertStats(MetricsRegistry.getREGISTRY(), "alert", "default", "default", rules, name);
            PROFILER_STAT_SET.add(stats);
            return stats;
        }
    }

    public static void main(String[] args) {
        AlertStats alertStats = Builder.builder()
                .rule("rule_1")
                .rule("rule_2")
                .rule("rule_3").create();
        alertStats.set("label1", "lv1", "label2", "lv2", "label3", "lv3", 1L);
    }
}
