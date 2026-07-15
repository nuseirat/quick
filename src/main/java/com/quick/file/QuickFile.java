package com.quick.file;

import com.quick.model.FileStats;
import com.quick.path.QuickPath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuickFile {

    private Path path;
    private String cachedContent;
    private Instant cachedLastModified;

    public QuickFile(String path) {
        this.path = Paths.get(path);
    }

    public QuickPath path() {
        return new QuickPath(path);
    }

    public Path toPath() {
        return path;
    }

    public QuickPath absolute() {
        return new QuickPath(path.toAbsolutePath());
    }

    public String absolutePath() {
        return path.toAbsolutePath().toString();
    }

    public QuickFile create() {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.exists(path)) {
                throw new QuickFileException("QuickFile create error: file already exists: " + path);
            }

            Files.createFile(path);
            cachedContent = "";
            cachedLastModified = lastModifiedOrNull();
            return this;
        } catch (QuickFileException e) {
            throw e;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile create error: " + e.getMessage(), e);
        }
    }

    public QuickFile createIfMissing() {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            refreshCache();
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile create error: " + e.getMessage(), e);
        }
    }

    public QuickFile touch() {
        try {
            createIfMissing();
            Files.setLastModifiedTime(
                    path,
                    java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis())
            );
            cachedLastModified = lastModifiedOrNull();
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile touch error: " + e.getMessage(), e);
        }
    }

    public QuickFile write(String text) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    path,
                    text,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            cachedContent = text;
            cachedLastModified = lastModifiedOrNull();
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile write error: " + e.getMessage(), e);
        }
    }

    public QuickFile writeLines(List<String> lines) {
        return write(String.join(System.lineSeparator(), lines));
    }

    public QuickFile append(String text) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    path,
                    text,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            if (cachedContent == null || isCacheStale()) {
                refreshCache();
            } else {
                cachedContent += text;
                cachedLastModified = lastModifiedOrNull();
            }

            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile append error: " + e.getMessage(), e);
        }
    }

    public QuickFile appendLine(String text) {
        return append(text + System.lineSeparator());
    }

    public QuickFile appendLines(List<String> lines) {
        String text = String.join(System.lineSeparator(), lines);
        if (!text.isEmpty()) {
            text += System.lineSeparator();
        }
        return append(text);
    }

    public String read() {
        try {
            refreshCache();
            return cachedContent;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile read error: " + e.getMessage(), e);
        }
    }

    public List<String> readLines() {
        try {
            return Files.readAllLines(path);
        } catch (Exception e) {
            throw new QuickFileException("QuickFile readLines error: " + e.getMessage(), e);
        }
    }

    public String readLine(int index) {
        List<String> lines = readLines();

        if (index < 0 || index >= lines.size()) {
            throw new QuickFileException("QuickFile readLine error: invalid line index " + index);
        }

        return lines.get(index);
    }

    public String firstLine() {
        List<String> lines = readLines();
        return lines.isEmpty() ? "" : lines.get(0);
    }

    public String lastLine() {
        List<String> lines = readLines();
        return lines.isEmpty() ? "" : lines.get(lines.size() - 1);
    }

    public List<String> readWords() {
        String content = content().trim();

        if (content.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(content.split("\\s+"))
                .filter(word -> !word.isBlank())
                .collect(Collectors.toList());
    }

    public String readWord(int index) {
        List<String> words = readWords();

        if (index < 0 || index >= words.size()) {
            throw new QuickFileException("QuickFile readWord error: invalid word index " + index);
        }

        return words.get(index);
    }

    public String firstWord() {
        List<String> words = readWords();
        return words.isEmpty() ? "" : words.get(0);
    }

    public String lastWord() {
        List<String> words = readWords();
        return words.isEmpty() ? "" : words.get(words.size() - 1);
    }

    public List<Character> readChars() {
        return content().chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
    }

    public char readChar(int index) {
        String content = content();

        if (index < 0 || index >= content.length()) {
            throw new QuickFileException("QuickFile readChar error: invalid char index " + index);
        }

        return content.charAt(index);
    }

    public char firstChar() {
        String content = content();
        return content.isEmpty() ? '\0' : content.charAt(0);
    }

    public char lastChar() {
        String content = content();
        return content.isEmpty() ? '\0' : content.charAt(content.length() - 1);
    }

    public void forEachLine(Consumer<String> action) {
        readLines().forEach(action);
    }

    public void print() {
        System.out.println(content());
    }

    public boolean exists() {
        return Files.exists(path);
    }

    public boolean notExists() {
        return Files.notExists(path);
    }

    public boolean isEmpty() {
        return chars() == 0;
    }

    public boolean canRead() {
        return Files.isReadable(path);
    }

    public boolean canWrite() {
        return Files.isWritable(path);
    }

    public boolean isHidden() {
        try {
            return Files.isHidden(path);
        } catch (Exception e) {
            throw new QuickFileException("QuickFile hidden error: " + e.getMessage(), e);
        }
    }

    public long size() {
        try {
            return Files.exists(path) ? Files.size(path) : 0;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile size error: " + e.getMessage(), e);
        }
    }

    public long lines() {
        return content().lines().count();
    }

    public long words() {
        return readWords().size();
    }

    public long chars() {
        return content().length();
    }

    public Instant createdAt() {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            return attrs.creationTime().toInstant();
        } catch (Exception e) {
            throw new QuickFileException("QuickFile createdAt error: " + e.getMessage(), e);
        }
    }

    public Instant lastModified() {
        try {
            return Files.getLastModifiedTime(path).toInstant();
        } catch (Exception e) {
            throw new QuickFileException("QuickFile lastModified error: " + e.getMessage(), e);
        }
    }

    public FileStats stats() {
        String content = content();
        long lines = content.lines().count();
        long words = readWords().size();
        long chars = content.length();
        long charsWithoutSpaces = content.replaceAll("\\s+", "").length();
        long emptyLines = content.lines().filter(String::isBlank).count();
        long nonEmptyLines = lines - emptyLines;
        double averageWordsPerLine = lines == 0 ? 0.0 : (double) words / lines;

        return new FileStats(
                size(),
                lines,
                words,
                chars,
                charsWithoutSpaces,
                emptyLines,
                nonEmptyLines,
                averageWordsPerLine
        );
    }

    public boolean contains(String text) {
        return content().contains(text);
    }

    public boolean startsWith(String text) {
        return content().startsWith(text);
    }

    public boolean endsWith(String text) {
        return content().endsWith(text);
    }

    public boolean matches(String regex) {
        return Pattern.compile(regex, Pattern.DOTALL)
                .matcher(content())
                .matches();
    }

    public List<String> findByRegex(String regex) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content());
            List<String> matches = new java.util.ArrayList<>();

            while (matcher.find()) {
                matches.add(matcher.group());
            }

            return matches;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile regex error: " + e.getMessage(), e);
        }
    }

    public long count(String text) {
        if (text == null) {
            throw new QuickFileException("QuickFile count error: text cannot be null");
        }

        if (text.isEmpty()) {
            return 0;
        }

        String content = content();
        long count = 0;
        int index = 0;

        while ((index = content.indexOf(text, index)) != -1) {
            count++;
            index += text.length();
        }

        return count;
    }

    public List<String> linesContaining(String text) {
        return content()
                .lines()
                .filter(line -> line.contains(text))
                .collect(Collectors.toList());
    }

    public QuickFile replace(String oldText, String newText) {
        return write(content().replace(oldText, newText));
    }

    public QuickFile replaceAll(String regex, String replacement) {
        return write(content().replaceAll(regex, replacement));
    }

    public QuickFile clear() {
        return write("");
    }

    public QuickFile changeExtension(String newExtension) {
        String cleanExtension = newExtension == null ? "" : newExtension.trim();

        if (cleanExtension.startsWith(".")) {
            cleanExtension = cleanExtension.substring(1);
        }

        String fullName = fullName();

        if (fullName.isEmpty()) {
            throw new QuickFileException("QuickFile changeExtension error: file name cannot be empty");
        }

        if (fullName.startsWith(".") && fullName.indexOf('.', 1) == -1) {
            throw new QuickFileException("QuickFile changeExtension error: cannot safely change extension of hidden file: " + fullName);
        }

        String baseName = name();

        if (baseName.isEmpty()) {
            throw new QuickFileException("QuickFile changeExtension error: file name cannot be empty");
        }

        if (cleanExtension.isEmpty()) {
            return rename(baseName);
        }

        return rename(baseName + "." + cleanExtension);
    }

    public QuickFile delete() {
        try {
            Files.deleteIfExists(path);
            cachedContent = null;
            cachedLastModified = null;
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile delete error: " + e.getMessage(), e);
        }
    }

    public QuickFile copy(String target) {
        return copyTo(target);
    }

    public QuickFile copy(QuickPath target) {
        return copyTo(target.toString());
    }

    public QuickFile copyTo(String target) {
        try {
            Path targetPath = Paths.get(target);
            Path parent = targetPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile copy error: " + e.getMessage(), e);
        }
    }

    public QuickFile move(String target) {
        return moveTo(target);
    }

    public QuickFile move(QuickPath target) {
        return moveTo(target.toString());
    }

    public QuickFile moveTo(String target) {
        try {
            Path targetPath = Paths.get(target);
            Path parent = targetPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
            this.path = targetPath;
            cachedContent = null;
            cachedLastModified = null;
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile move error: " + e.getMessage(), e);
        }
    }

    public QuickFile rename(String newName) {
        try {
            Path targetPath = path.resolveSibling(newName);
            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
            this.path = targetPath;
            cachedContent = null;
            cachedLastModified = null;
            return this;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile rename error: " + e.getMessage(), e);
        }
    }

    public String name() {
        String fullName = fullName();
        int dotIndex = fullName.lastIndexOf('.');

        if (fullName.startsWith(".") && dotIndex == 0) {
            return fullName;
        }

        return dotIndex > 0 ? fullName.substring(0, dotIndex) : fullName;
    }

    public String fullName() {
        Path fileName = path.getFileName();
        return fileName == null ? "" : fileName.toString();
    }

    public String extension() {
        String fullName = fullName();
        int dotIndex = fullName.lastIndexOf('.');

        if (fullName.startsWith(".") && dotIndex == 0) {
            return "";
        }

        return dotIndex > 0 ? fullName.substring(dotIndex + 1) : "";
    }

    public String parent() {
        Path parent = path.getParent();
        return parent == null ? "" : parent.toString();
    }

    private String content() {
        try {
            if (cachedContent == null || isCacheStale()) {
                refreshCache();
            }
            return cachedContent;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile content error: " + e.getMessage(), e);
        }
    }

    private void refreshCache() {
        try {
            cachedContent = Files.readString(path);
            cachedLastModified = lastModifiedOrNull();
        } catch (Exception e) {
            throw new QuickFileException("QuickFile cache error: " + e.getMessage(), e);
        }
    }

    private boolean isCacheStale() {
        Instant currentLastModified = lastModifiedOrNull();
        return cachedLastModified == null || !cachedLastModified.equals(currentLastModified);
    }

    private Instant lastModifiedOrNull() {
        try {
            return Files.exists(path) ? Files.getLastModifiedTime(path).toInstant() : null;
        } catch (Exception e) {
            throw new QuickFileException("QuickFile lastModified error: " + e.getMessage(), e);
        }
    }

    public static class QuickFileException extends RuntimeException {
        public QuickFileException(String message) {
            super(message);
        }

        public QuickFileException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}