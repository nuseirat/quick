package com.quick.condition;

public final class QuickIf {

    private QuickIf() {
    }

    public static boolean isTrue(boolean value) {
        return value;
    }

    public static boolean isFalse(boolean value) {
        return !value;
    }

    public static boolean isNull(Object value) {
        return value == null;
    }

    public static boolean isNotNull(Object value) {
        return value != null;
    }

    public static boolean isEmpty(String text) {
        return text != null && text.isEmpty();
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    public static boolean isZero(int value) {
        return value == 0;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean isNegative(int value) {
        return value < 0;
    }

    public static boolean isEven(int value) {
        return value % 2 == 0;
    }

    public static boolean isOdd(int value) {
        return value % 2 != 0;
    }

    public static boolean isGreater(int value, int other) {
        return value > other;
    }

    public static boolean isLess(int value, int other) {
        return value < other;
    }

    public static boolean isEqual(int value, int other) {
        return value == other;
    }

    public static boolean isAdult(int age) {
        return age >= 18;
    }

    public static boolean isMinor(int age) {
        return age < 18;
    }

    public static boolean between(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean notBetween(int value, int min, int max) {
        return !between(value, min, max);
    }
}