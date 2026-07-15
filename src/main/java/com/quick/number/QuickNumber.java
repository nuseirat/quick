package com.quick.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class QuickNumber {

    private static final double MAX_SAFE_INTEGER = 9007199254740991d; // 2^53 - 1

    private final double value;

    public QuickNumber(int value) {
        this.value = value;
    }

    public QuickNumber(long value) {
        this.value = value;
    }

    public QuickNumber(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    public boolean isEven() {
        ensureSafeWholeNumber("isEven");
        return ((long) value) % 2 == 0;
    }

    public boolean isOdd() {
        ensureSafeWholeNumber("isOdd");
        return ((long) value) % 2 != 0;
    }

    public boolean isPositive() {
        return value > 0;
    }

    public boolean isNegative() {
        return value < 0;
    }

    public boolean isZero() {
        return value == 0.0d;
    }

    public boolean isInteger() {
        return Double.isFinite(value) && value == Math.floor(value);
    }

    public boolean isDecimal() {
        return Double.isFinite(value) && !isInteger();
    }

    public boolean isGreaterThan(double number) {
        return value > number;
    }

    public boolean isLessThan(double number) {
        return value < number;
    }

    public boolean isGreaterThanOrEqual(double number) {
        return value >= number;
    }

    public boolean isLessThanOrEqual(double number) {
        return value <= number;
    }

    public double min(double number) {
        return Math.min(value, number);
    }

    public double max(double number) {
        return Math.max(value, number);
    }

    public double square() {
        return value * value;
    }

    public double cube() {
        return value * value * value;
    }

    public double sqrt() {
        if (Double.isNaN(value)) {
            throw new QuickNumberException("QuickNumber sqrt error: value cannot be NaN");
        }

        if (value < 0) {
            throw new QuickNumberException("QuickNumber sqrt error: value cannot be negative");
        }

        return Math.sqrt(value);
    }

    public double pow(double exponent) {
        return Math.pow(value, exponent);
    }

    public double abs() {
        return Math.abs(value);
    }

    public double clamp(double min, double max) {
        if (Double.isNaN(min) || Double.isNaN(max)) {
            throw new QuickNumberException("QuickNumber clamp error: min and max cannot be NaN");
        }

        if (min > max) {
            throw new QuickNumberException("QuickNumber clamp error: min cannot be greater than max");
        }

        return Math.max(min, Math.min(value, max));
    }

    public boolean between(double min, double max) {
        if (Double.isNaN(min) || Double.isNaN(max)) {
            throw new QuickNumberException("QuickNumber between error: min and max cannot be NaN");
        }

        if (min > max) {
            throw new QuickNumberException("QuickNumber between error: min cannot be greater than max");
        }

        return value >= min && value <= max;
    }

    public long round() {
        return Math.round(value);
    }

    public double round(int places) {
        if (places < 0) {
            throw new QuickNumberException("QuickNumber round error: places cannot be negative");
        }

        ensureFinite("round");

        BigDecimal decimal = BigDecimal.valueOf(value);
        return decimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public double ceil() {
        return Math.ceil(value);
    }

    public double floor() {
        return Math.floor(value);
    }

    public double percentOf(double total) {
        if (Double.isNaN(total)) {
            throw new QuickNumberException("QuickNumber percentOf error: total cannot be NaN");
        }

        if (total == 0) {
            throw new QuickNumberException("QuickNumber percentOf error: total cannot be zero");
        }

        return (value / total) * 100.0;
    }

    public double increaseBy(double percent) {
        return value + (value * percent / 100.0);
    }

    public double decreaseBy(double percent) {
        return value - (value * percent / 100.0);
    }

    public int toInt() {
        return (int) value;
    }

    public long toLong() {
        return (long) value;
    }

    public double toDouble() {
        return value;
    }

    public QuickNumber copy() {
        return new QuickNumber(value);
    }

    @Override
    public String toString() {
        if (Double.isNaN(value)) {
            return "NaN";
        }

        if (Double.isInfinite(value)) {
            return value > 0 ? "Infinity" : "-Infinity";
        }

        if (value == 0.0d) {
            return "0";
        }

        if (isInteger() && Math.abs(value) <= Long.MAX_VALUE) {
            return String.valueOf((long) value);
        }

        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof QuickNumber other)) return false;

        if (Double.isNaN(this.value) && Double.isNaN(other.value)) {
            return true;
        }

        if (this.value == 0.0d && other.value == 0.0d) {
            return true;
        }

        return Double.compare(value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        if (Double.isNaN(value)) {
            return Objects.hash(Double.NaN);
        }

        if (value == 0.0d) {
            return Objects.hash(0.0d);
        }

        return Objects.hash(value);
    }

    private void ensureWholeNumber(String methodName) {
        if (!isInteger()) {
            throw new QuickNumberException("QuickNumber " + methodName + " error: value must be a whole number");
        }
    }

    private void ensureSafeWholeNumber(String methodName) {
        ensureWholeNumber(methodName);

        if (Math.abs(value) > MAX_SAFE_INTEGER) {
            throw new QuickNumberException(
                    "QuickNumber " + methodName + " error: value is outside the safe integer range for double"
            );
        }
    }

    private void ensureFinite(String methodName) {
        if (!Double.isFinite(value)) {
            throw new QuickNumberException(
                    "QuickNumber " + methodName + " error: value must be finite"
            );
        }
    }

    public static class QuickNumberException extends RuntimeException {
        public QuickNumberException(String message) {
            super(message);
        }

        public QuickNumberException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}