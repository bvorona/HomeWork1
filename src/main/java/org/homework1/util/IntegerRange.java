package org.homework1.util;

public record IntegerRange(int start, int end) {

    public IntegerRange {
        if (start < 0) {
            throw new IllegalArgumentException("Start must be greater than or equal to 0");
        }
        if (end < 0) {
            throw new IllegalArgumentException("End must be greater than or equal to 0");
        }
        if (start > end) {
            throw new IllegalArgumentException("Start must be less than or equal to end");
        }
    }

    public boolean contains(int value) {
        return value >= start && value <= end;
    }
}
