package com.pepper.metrics.integration.custom;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;

import java.util.ArrayList;
import java.util.List;

public class AlertMetrics {
    private String name;
    private Double value;
    private String label1Name;
    private String label1Value;
    private String label2Name;
    private String label2Value;
    private String label3Name;
    private String label3Value;

    public static void main(String[] args) {
        Stats stats = Profiler.Builder.builder().type("alert").build();
    }

    public static class Label {
        private String name;
        private String value;

        public Label(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Builder {
        private String name;
        private List<Label> labels = new ArrayList<>();

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder label(String labelName, String labelValue) {
            if (labels.size() > 2)
                throw new IllegalArgumentException("only 3 labels are allowed!!!");
            labels.add(new Label(labelName, labelValue));
            return this;
        }

        public AlertMetrics create() {
            if (labels.size() != 3)
                throw new IllegalArgumentException("you must init exact 3 labels!!!");
            AlertMetrics alertMetrics = new AlertMetrics();
            alertMetrics.setName(name);
            alertMetrics.setLabel1Name(labels.get(0).getName());
            alertMetrics.setLabel1Value(labels.get(0).getValue());
            alertMetrics.setLabel2Name(labels.get(1).getName());
            alertMetrics.setLabel2Value(labels.get(1).getValue());
            alertMetrics.setLabel3Name(labels.get(2).getName());
            alertMetrics.setLabel3Value(labels.get(2).getValue());
            return alertMetrics;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getLabel1Name() {
        return label1Name;
    }

    public void setLabel1Name(String label1Name) {
        this.label1Name = label1Name;
    }

    public String getLabel1Value() {
        return label1Value;
    }

    public void setLabel1Value(String label1Value) {
        this.label1Value = label1Value;
    }

    public String getLabel2Name() {
        return label2Name;
    }

    public void setLabel2Name(String label2Name) {
        this.label2Name = label2Name;
    }

    public String getLabel2Value() {
        return label2Value;
    }

    public void setLabel2Value(String label2Value) {
        this.label2Value = label2Value;
    }

    public String getLabel3Name() {
        return label3Name;
    }

    public void setLabel3Name(String label3Name) {
        this.label3Name = label3Name;
    }

    public String getLabel3Value() {
        return label3Value;
    }

    public void setLabel3Value(String label3Value) {
        this.label3Value = label3Value;
    }
}
