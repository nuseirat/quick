package com.quick.folder;

import com.quick.file.QuickFile;
import com.quick.model.FolderStats;
import com.quick.path.QuickPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuickFolder {

    private Path path;

    public QuickFolder(String path) {
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

    public boolean exists() {
        return Files.exists(path);
    }

    public boolean notExists() {
        return Files.notExists(path);
    }

    public QuickFolder create() {
        try {
            if (Files.exists(path)) {
                throw new QuickFolderException("QuickFolder create error: folder already exists: " + path);
            }

            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.createDirectory(path);
            return this;
        } catch (QuickFolderException e) {
            throw e;
        } catch (Exception e) {
            throw new QuickFolderException("QuickFolder create error: " + e.getMessage(), e);
        }
    }

    public QuickFolder createIfMissing() {
        try {
            Files.createDirectories(path);
            return this;
        } catch (Exception e) {
            throw new QuickFolderException("QuickFolder create error: " + e.getMessage(), e);
        }
    }

    public boolean isEmpty() {
        return count() == 0;
    }

    public long count() {
        return list().size();
    }

    public long countFiles() {
        return filesOnly().size();
    }

    public long countFolders() {
        return foldersOnly().size();
    }

    public List<Path> list() {
        try {
            ensureFolderExists("list");
            try (Stream<Path> stream = Files.list(path)) {
                return stream.collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new QuickFolderException("QuickFolder list error: " + e.getMessage(), e);
        }
    }

    public List<Path> walk() {
        try {
            ensureFolderExists("walk");
            try (Stream<Path> stream = Files.walk(path)) {
                return stream.collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new QuickFolderException("QuickFolder walk error: " + e.getMessage(), e);
        }
    }

    public List<String> names() {
        return list()
                .stream()
                .map(item -> item.getFileName().toString())
                .collect(Collectors.toList());
    }

    public String firstName() {
        List<String> names = names();
        return names.isEmpty() ? "" : names.get(0);
    }

    public String lastName() {
        List<String> names = names();
        return names.isEmpty() ? "" : names.get(names.size() - 1);
    }

    public void printNames() {
        names().forEach(System.out::println);
    }

    public void each(Consumer<Path> action) {
        list().forEach(action);
    }

    public List<Path> files() {
        return filesOnly();
    }

    public List<Path> filesOnly() {
        return list()
                .stream()
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
    }

    public List<Path> folders() {
        return foldersOnly();
    }

    public List<Path> foldersOnly() {
        return list()
                .stream()
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
    }

    public Path first() {
        List<Path> items = list();
        return items.isEmpty() ? null : items.get(0);
    }

    public Path last() {
        List<Path> items = list();
        return items.isEmpty() ? null : items.get(items.size() - 1);
    }

    public boolean contains(String name) {
        return names().contains(name);
    }

    public boolean containsFile(String name) {
        return filesOnly()
                .stream()
                .anyMatch(file -> file.getFileName().toString().equals(name));
    }

    public boolean containsFolder(String name) {
        return foldersOnly()
                .stream()
                .anyMatch(folder -> folder.getFileName().toString().equals(name));
    }

    public QuickFile file(String name) {
        return new QuickFile(path.resolve(name).toString());
    }

    public QuickFolder folder(String name) {
        return new QuickFolder(path.resolve(name).toString());
    }

    public List<Path> filterByExtension(String extension) {
        if (extension == null) {
            throw new QuickFolderException("QuickFolder filterByExtension error: extension cannot be null");
        }

        String cleanExtension = extension.trim();
        if (cleanExtension.startsWith(".")) {
            cleanExtension = cleanExtension.substring(1);
        }

        if (cleanExtension.isEmpty()) {
            return List.of();
        }

        String finalExtension = cleanExtension;

        return filesOnly()
                .stream()
                .filter(file -> {
                    String fileName = file.getFileName().toString();
                    int dotIndex = fileName.lastIndexOf('.');
                    return dotIndex > 0 &&
                            fileName.substring(dotIndex + 1).equalsIgnoreCase(finalExtension);
                })
                .collect(Collectors.toList());
    }

    public List<Path> filterByName(String text) {
        if (text == null) {
            throw new QuickFolderException("QuickFolder filterByName error: text cannot be null");
        }

        return list()
                .stream()
                .filter(item -> item.getFileName().toString().contains(text))
                .collect(Collectors.toList());
    }

    public List<Path> sortByName() {
        return list()
                .stream()
                .sorted(Comparator.comparing(item -> item.getFileName().toString()))
                .collect(Collectors.toList());
    }

    public List<Path> sortBySize() {
        return list()
                .stream()
                .sorted(Comparator.comparingLong(this::safeSize))
                .collect(Collectors.toList());
    }

    public long size() {
        return walk()
                .stream()
                .filter(Files::isRegularFile)
                .mapToLong(this::safeSize)
                .sum();
    }

    public FolderStats stats() {
        return new FolderStats(count(), countFiles(), countFolders(), size());
    }

    public QuickFolder copy(String target) {
        return copyTo(target);
    }

    public QuickFolder copy(QuickPath target) {
        return copyTo(target.toString());
    }

    public QuickFolder copyTo(String target) {
        try {
            ensureFolderExists("copy");

            Path targetPath = Paths.get(target).toAbsolutePath().normalize();
            Path sourcePath = path.toAbsolutePath().normalize();

            if (targetPath.equals(sourcePath)) {
                throw new QuickFolderException("QuickFolder copy error: source and target cannot be the same");
            }

            if (targetPath.startsWith(sourcePath)) {
                throw new QuickFolderException("QuickFolder copy error: target cannot be inside source folder");
            }

            Files.createDirectories(targetPath);

            for (Path source : walk()) {
                Path relative = sourcePath.relativize(source.toAbsolutePath().normalize());
                Path destination = targetPath.resolve(relative);

                if (Files.isDirectory(source)) {
                    Files.createDirectories(destination);
                } else {
                    Path parent = destination.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            return this;
        } catch (QuickFolderException e) {
            throw e;
        } catch (Exception e) {
            throw new QuickFolderException("QuickFolder copy error: " + e.getMessage(), e);
        }
    }

    public QuickFolder move(String target) {
        return moveTo(target);
    }

    public QuickFolder move(QuickPath target) {
        return moveTo(target.toString());
    }

    public QuickFolder moveTo(String target) {
        try {
            ensureFolderExists("move");

            Path targetPath = Paths.get(target).toAbsolutePath().normalize();
            Path sourcePath = path.toAbsolutePath().normalize();

            if (targetPath.equals(sourcePath)) {
                throw new QuickFolderException("QuickFolder move error: source and target cannot be the same");
            }

            if (targetPath.startsWith(sourcePath)) {
                throw new QuickFolderException("QuickFolder move error: target cannot be inside source folder");
            }

            Path parent = targetPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
            this.path = targetPath;
            return this;
        } catch (QuickFolderException e) {
            throw e;
        } catch (Exception e) {
            throw new QuickFolderException("QuickFolder move error: " + e.getMessage(), e);
        }
    }

    public QuickFolder rename(String newName) {
        try {
            ensureFolderExists("rename");

            if (newName == null || newName.isBlank()) {
                throw new QuickFolderException("QuickFolder rename error: new name cannot be blank");
            }

            Path targetPath = path.resolveSibling(newName);
            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
            this.path = targetPath;
            return this;
        } catch (QuickFolderException e) {
            throw e;
        } catch (Exception e) {
            throw new QuickFolderException("QuickFolder rename error: " + e.getMessage(), e);
        }
    }

    public QuickFolder delete() {
        return deleteContents();
    }

    public QuickFolder deleteContents() {
        list().forEach(this::deletePath);
        return this;
    }

    public QuickFolder deleteBefore(int index) {
        List<Path> items = new ArrayList<>(list());

        for (int i = 0; i < Math.min(index, items.size()); i++) {
            deletePath(items.get(i));
        }

        return this;
    }

    public QuickFolder deleteAfter(int index) {
        List<Path> items = new ArrayList<>(list());

        for (int i = index + 1; i < items.size(); i++) {
            deletePath(items.get(i));
        }

        return this;
    }

    public QuickFolder deleteBetween(int start, int end) {
        if (start < 0 || end < start) {
            throw new QuickFolderException("QuickFolder deleteBetween error: invalid range");
        }

        List<Path> items = new ArrayList<>(list());

        for (int i = start; i <= end && i < items.size(); i++) {
            deletePath(items.get(i));
        }

        return this;
    }

    public QuickFolder deleteRecursively() {
        try {
            if (Files.notExists(path)) {
                return this;
            }

            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder()).forEach(this::deletePath);
            }

            return this;
        } catch (IOException e) {
            throw new QuickFolderException("QuickFolder delete error: " + e.getMessage(), e);
        }
    }

    public String name() {
        Path fileName = path.getFileName();
        return fileName == null ? "" : fileName.toString();
    }

    public String parent() {
        Path parent = path.getParent();
        return parent == null ? "" : parent.toString();
    }

    private void ensureFolderExists(String operation) {
        if (Files.notExists(path)) {
            throw new QuickFolderException("QuickFolder " + operation + " error: folder does not exist: " + path);
        }

        if (!Files.isDirectory(path)) {
            throw new QuickFolderException("QuickFolder " + operation + " error: path is not a folder: " + path);
        }
    }

    private long safeSize(Path item) {
        try {
            return Files.isRegularFile(item) ? Files.size(item) : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    private void deletePath(Path item) {
        try {
            if (Files.isDirectory(item)) {
                try (Stream<Path> stream = Files.list(item)) {
                    if (stream.findAny().isPresent()) {
                        deleteRecursively(item);
                        return;
                    }
                }
            }

            Files.deleteIfExists(item);
        } catch (IOException e) {
            throw new QuickFolderException("QuickFolder delete error: " + e.getMessage(), e);
        }
    }

    private void deleteRecursively(Path target) {
        try (Stream<Path> stream = Files.walk(target)) {
            stream.sorted(Comparator.reverseOrder()).forEach(current -> {
                try {
                    Files.deleteIfExists(current);
                } catch (IOException e) {
                    throw new QuickFolderException("QuickFolder delete error: " + e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new QuickFolderException("QuickFolder delete error: " + e.getMessage(), e);
        }
    }

    public static class QuickFolderException extends RuntimeException {
        public QuickFolderException(String message) {
            super(message);
        }

        public QuickFolderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}