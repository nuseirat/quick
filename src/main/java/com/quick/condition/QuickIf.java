package com.quick.condition; // Declares the package where this class belongs

/**
 * Utility class that provides simple conditional helper methods.
 * This class is designed to make common boolean checks shorter and clearer.
 */
public final class QuickIf { // Declares a final utility class so it cannot be extended

    private QuickIf() { // Private constructor prevents creating instances of this utility class
    } // Empty constructor because the class only contains static methods

    public static boolean isTrue(boolean value) { // Returns whether the given boolean value is true
        return value; // Returns the value directly
    } // End of isTrue

    public static boolean isFalse(boolean value) { // Returns whether the given boolean value is false
        return !value; // Inverts the value and returns the result
    } // End of isFalse

    public static boolean isNull(Object value) { // Checks whether the given object is null
        return value == null; // Returns true if the object is null
    } // End of isNull

    public static boolean isNotNull(Object value) { // Checks whether the given object is not null
        return value != null; // Returns true if the object is not null
    } // End of isNotNull

    public static boolean isEmpty(String text) { // Checks whether the given string is empty
        return text != null && text.isEmpty(); // Returns true only if the string is not null and has no characters
    } // End of isEmpty

    public static boolean isNotEmpty(String text) { // Checks whether the given string is not empty
        return text != null && !text.isEmpty(); // Returns true only if the string is not null and has at least one character
    } // End of isNotEmpty

    public static boolean isBlank(String text) { // Checks whether the given string is null, empty, or contains only spaces
        return text == null || text.trim().isEmpty(); // Trims whitespace and checks if the result is empty
    } // End of isBlank

    public static boolean isNotBlank(String text) { // Checks whether the given string is not blank
        return !isBlank(text); // Reuses isBlank and inverts the result
    } // End of isNotBlank

    public static boolean isZero(int value) { // Checks whether the given number is zero
        return value == 0; // Returns true if the value equals zero
    } // End of isZero

    public static boolean isPositive(int value) { // Checks whether the given number is positive
        return value > 0; // Returns true if the value is greater than zero
    } // End of isPositive

    public static boolean isNegative(int value) { // Checks whether the given number is negative
        return value < 0; // Returns true if the value is less than zero
    } // End of isNegative

    public static boolean isEven(int value) { // Checks whether the given number is even
        return value % 2 == 0; // Returns true if the remainder of division by 2 is zero
    } // End of isEven

    public static boolean isOdd(int value) { // Checks whether the given number is odd
        return value % 2 != 0; // Returns true if the remainder of division by 2 is not zero
    } // End of isOdd

    public static boolean isGreater(int value, int other) { // Checks whether the first number is greater than the second
        return value > other; // Returns true if value is greater than other
    } // End of isGreater

    public static boolean isLess(int value, int other) { // Checks whether the first number is less than the second
        return value < other; // Returns true if value is less than other
    } // End of isLess

    public static boolean isEqual(int value, int other) { // Checks whether both numbers are equal
        return value == other; // Returns true if both values are the same
    } // End of isEqual

    public static boolean isAdult(int age) { // Checks whether the age is considered adult
        return age >= 18; // Returns true if age is 18 or above
    } // End of isAdult

    public static boolean isMinor(int age) { // Checks whether the age is considered minor
        return age < 18; // Returns true if age is below 18
    } // End of isMinor

    public static boolean between(int value, int min, int max) { // Checks whether the value is within the inclusive range
        return value >= min && value <= max; // Returns true if value is between min and max, including both bounds
    } // End of between

    public static boolean notBetween(int value, int min, int max) { // Checks whether the value is outside the inclusive range
        return !between(value, min, max); // Reuses between and inverts the result
    } // End of notBetween
} // End of QuickIf class
