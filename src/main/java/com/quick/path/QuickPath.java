package com.quick.path;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class QuickPath {

    private final Path path;

    public QuickPath(String path) {
        this.path = Paths.get(path);
    }

    public QuickPath(String first, String... more) {
        this.path = Paths.get(first, more);
    }

    public QuickPath(Path path) {
        this.path = path;
    }

    public String fullName() {
        Path fileName = path.getFileName();
        return fileName == null ? "" : fileName.toString();
    }

    public String name() {
        String fullName = fullName();

        if (fullName.isEmpty() || !fullName.contains(".") || fullName.startsWith(".")) {
            return fullName;
        }

        int index = fullName.lastIndexOf('.');
        return index == -1 ? fullName : fullName.substring(0, index);
    }

    public String extension() {
        String fullName = fullName();

        if (fullName.isEmpty() || !fullName.contains(".") || fullName.startsWith(".")) {
            return "";
        }

        int index = fullName.lastIndexOf('.');
        return index == -1 ? "" : fullName.substring(index + 1);
    }

    public String fileNameWithoutExtension() {
        return name();
    }

    public String parent() {
        Path parent = path.getParent();
        return parent == null ? "" : parent.toString();
    }

    public QuickPath parentPath() {
        Path parent = path.getParent();
        return parent == null ? null : new QuickPath(parent);
    }

    public boolean exists() {
        return Files.exists(path);
    }

    public boolean notExists() {
        return Files.notExists(path);
    }

    public boolean isFile() {
        return Files.isRegularFile(path);
    }

    public boolean isFolder() {
        return Files.isDirectory(path);
    }

    public boolean isAbsolute() {
        return path.isAbsolute();
    }

    public QuickPath absolute() {
        return new QuickPath(path.toAbsolutePath());
    }

    public String absolutePath() {
        return path.toAbsolutePath().toString();
    }

    public QuickPath normalize() {
        return new QuickPath(path.normalize());
    }

    public QuickPath join(String other) {
        return new QuickPath(path.resolve(other));
    }

    public QuickPath join(String first, String... more) {
        return new QuickPath(path.resolve(Paths.get(first, more)));
    }

    public Path toPath() {
        return path;
    }

    public QuickPath copy() {
        return new QuickPath(path);
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof QuickPath other)) return false;
        return Objects.equals(path, other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}