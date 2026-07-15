package com.quick.model;

import java.util.Objects;

public final class FileStats {

    private final long size;
    private final long lines;
    private final long words;
    private final long chars;
    private final long charsWithoutSpaces;
    private final long emptyLines;
    private final long nonEmptyLines;
    private final double averageWordsPerLine;

    public FileStats(long size, long lines, long words, long chars) {
        this(size, lines, words, chars, 0, 0, 0, 0.0);
    }

    public FileStats(
            long size,
            long lines,
            long words,
            long chars,
            long charsWithoutSpaces,
            long emptyLines,
            long nonEmptyLines,
            double averageWordsPerLine
    ) {
        this.size = size;
        this.lines = lines;
        this.words = words;
        this.chars = chars;
        this.charsWithoutSpaces = charsWithoutSpaces;
        this.emptyLines = emptyLines;
        this.nonEmptyLines = nonEmptyLines;
        this.averageWordsPerLine = averageWordsPerLine;
    }

    public static FileStats of(long size, long lines, long words, long chars) {
        return new FileStats(size, lines, words, chars);
    }

    public static FileStats of(
            long size,
            long lines,
            long words,
            long chars,
            long charsWithoutSpaces,
            long emptyLines,
            long nonEmptyLines,
            double averageWordsPerLine
    ) {
        return new FileStats(
                size,
                lines,
                words,
                chars,
                charsWithoutSpaces,
                emptyLines,
                nonEmptyLines,
                averageWordsPerLine
        );
    }

    public long size() {
        return size;
    }

    public long lines() {
        return lines;
    }

    public long words() {
        return words;
    }

    public long chars() {
        return chars;
    }

    public long charsWithoutSpaces() {
        return charsWithoutSpaces;
    }

    public long emptyLines() {
        return emptyLines;
    }

    public long nonEmptyLines() {
        return nonEmptyLines;
    }

    public double averageWordsPerLine() {
        return averageWordsPerLine;
    }

    public boolean isEmpty() {
        return size == 0 && lines == 0 && words == 0 && chars == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public String readableSize() {
        if (size < 1024) {
            return size + " B";
        }

        if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        }

        if (size < 1024L * 1024L * 1024L) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }

        return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileStats that)) return false;

        return size == that.size
                && lines == that.lines
                && words == that.words
                && chars == that.chars
                && charsWithoutSpaces == that.charsWithoutSpaces
                && emptyLines == that.emptyLines
                && nonEmptyLines == that.nonEmptyLines
                && Double.compare(that.averageWordsPerLine, averageWordsPerLine) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                size,
                lines,
                words,
                chars,
                charsWithoutSpaces,
                emptyLines,
                nonEmptyLines,
                averageWordsPerLine
        );
    }

    @Override
    public String toString() {
        return "FileStats{" +
                "size=" + size +
                ", lines=" + lines +
                ", words=" + words +
                ", chars=" + chars +
                ", charsWithoutSpaces=" + charsWithoutSpaces +
                ", emptyLines=" + emptyLines +
                ", nonEmptyLines=" + nonEmptyLines +
                ", averageWordsPerLine=" + averageWordsPerLine +
                '}';
    }
}