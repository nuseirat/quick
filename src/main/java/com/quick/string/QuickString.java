package com.quick.string;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class QuickString {

    private final String value;

    public QuickString(String value) {
        this.value = value == null ? "" : value;
    }

    public int length() {
        return value.length();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public boolean isBlank() {
        return value.isBlank();
    }

    public QuickString trim() {
        return new QuickString(value.trim());
    }

    public QuickString lower() {
        return new QuickString(value.toLowerCase(Locale.ROOT));
    }

    public QuickString upper() {
        return new QuickString(value.toUpperCase(Locale.ROOT));
    }

    public QuickString capitalize() {
        if (value.isEmpty()) {
            return new QuickString(value);
        }

        return new QuickString(
                Character.toUpperCase(value.charAt(0)) + value.substring(1)
        );
    }

    public boolean contains(String text) {
        requireText(text, "QuickString contains error: text cannot be null");
        return value.contains(text);
    }

    public boolean startsWith(String text) {
        requireText(text, "QuickString startsWith error: text cannot be null");
        return value.startsWith(text);
    }

    public boolean endsWith(String text) {
        requireText(text, "QuickString endsWith error: text cannot be null");
        return value.endsWith(text);
    }

    public boolean equalsTo(String text) {
        return value.equals(text);
    }

    public boolean equalsIgnoreCase(String text) {
        return value.equalsIgnoreCase(text);
    }

    public boolean matches(String regex) {
        requireText(regex, "QuickString matches error: regex cannot be null");
        return value.matches(regex);
    }

    public int indexOf(String text) {
        requireText(text, "QuickString indexOf error: text cannot be null");
        return value.indexOf(text);
    }

    public int lastIndexOf(String text) {
        requireText(text, "QuickString lastIndexOf error: text cannot be null");
        return value.lastIndexOf(text);
    }

    public QuickString replace(String oldText, String newText) {
        requireText(oldText, "QuickString replace error: oldText cannot be null");
        requireText(newText, "QuickString replace error: newText cannot be null");
        return new QuickString(value.replace(oldText, newText));
    }

    public QuickString replaceFirst(String target, String replacement) {
        requireText(target, "QuickString replaceFirst error: target cannot be null");
        requireText(replacement, "QuickString replaceFirst error: replacement cannot be null");

        return new QuickString(
                value.replaceFirst(
                        Pattern.quote(target),
                        Matcher.quoteReplacement(replacement)
                )
        );
    }

    public QuickString replaceLast(String target, String replacement) {
        requireText(target, "QuickString replaceLast error: target cannot be null");
        requireText(replacement, "QuickString replaceLast error: replacement cannot be null");

        int index = value.lastIndexOf(target);

        if (index == -1) {
            return new QuickString(value);
        }

        String result = value.substring(0, index)
                + replacement
                + value.substring(index + target.length());

        return new QuickString(result);
    }

    public QuickString remove(String text) {
        requireText(text, "QuickString remove error: text cannot be null");
        return new QuickString(value.replace(text, ""));
    }

    public QuickString removeSpaces() {
        return new QuickString(value.replace(" ", ""));
    }

    public long count(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        long total = 0;
        int index = 0;

        while ((index = value.indexOf(text, index)) != -1) {
            total++;
            index += text.length();
        }

        return total;
    }

    public QuickString repeat(int times) {
        if (times < 0) {
            throw new QuickStringException("QuickString repeat error: times cannot be negative");
        }

        return new QuickString(value.repeat(times));
    }

    public QuickString reverse() {
        return new QuickString(new StringBuilder(value).reverse().toString());
    }

    public QuickString before(String text) {
        requireText(text, "QuickString before error: text cannot be null");

        int index = value.indexOf(text);
        if (index == -1) {
            return new QuickString("");
        }

        return new QuickString(value.substring(0, index));
    }

    public QuickString after(String text) {
        requireText(text, "QuickString after error: text cannot be null");

        int index = value.indexOf(text);
        if (index == -1) {
            return new QuickString("");
        }

        return new QuickString(value.substring(index + text.length()));
    }

    public QuickString between(String start, String end) {
        requireText(start, "QuickString between error: start cannot be null");
        requireText(end, "QuickString between error: end cannot be null");

        int startIndex = value.indexOf(start);
        if (startIndex == -1) {
            return new QuickString("");
        }

        startIndex += start.length();

        int endIndex = value.indexOf(end, startIndex);
        if (endIndex == -1) {
            return new QuickString("");
        }

        return new QuickString(value.substring(startIndex, endIndex));
    }

    public QuickString substring(int start) {
        return new QuickString(value.substring(start));
    }

    public QuickString substring(int start, int end) {
        return new QuickString(value.substring(start, end));
    }

    public char charAt(int index) {
        return value.charAt(index);
    }

    public Character first() {
        return value.isEmpty() ? null : value.charAt(0);
    }

    public Character last() {
        return value.isEmpty() ? null : value.charAt(value.length() - 1);
    }

    public List<String> split(String regex) {
        requireText(regex, "QuickString split error: regex cannot be null");
        return Arrays.asList(value.split(regex, -1));
    }

    public List<String> splitLines() {
        return value.lines().collect(Collectors.toList());
    }

    public long linesCount() {
        return value.lines().count();
    }

    public List<String> words() {
        if (value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

    public List<Character> chars() {
        return value.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
    }

    public char[] toCharArray() {
        return value.toCharArray();
    }

    public char[] toArray() {
        return value.toCharArray();
    }

    public boolean isNumber() {
        if (isBlank()) {
            return false;
        }

        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int toInt() {
        return Integer.parseInt(value.trim());
    }

    public long toLong() {
        return Long.parseLong(value.trim());
    }

    public float toFloat() {
        return Float.parseFloat(value.trim());
    }

    public double toDouble() {
        return Double.parseDouble(value.trim());
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(value.trim());
    }

    public String orElse(String fallback) {
        return isBlank() ? fallback : value;
    }

    @Override
    public String toString() {
        return value;
    }

    private static void requireText(String text, String message) {
        if (text == null) {
            throw new QuickStringException(message);
        }
    }

    public static class QuickStringException extends RuntimeException {
        public QuickStringException(String message) {
            super(message);
        }

        public QuickStringException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}