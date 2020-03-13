package com.pepper.metrics.core.extension;

import java.util.Comparator;

public class ExtensionOrderComparator<T> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        ExtensionOrder eo1 = o1.getClass().getAnnotation(ExtensionOrder.class);
        ExtensionOrder eo2 = o2.getClass().getAnnotation(ExtensionOrder.class);
        if (eo1 == null) {
            return 127;
        } else if (eo2 == null) {
            return -128;
        } else {
            return eo1.value() - eo2.value();
        }
    }
}
