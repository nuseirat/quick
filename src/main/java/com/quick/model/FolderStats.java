package com.quick.model;

import java.util.Objects;

public final class FolderStats {

    private final long items;
    private final long files;
    private final long folders;
    private final long size;

    public FolderStats(long items, long files, long folders, long size) {
        this.items = items;
        this.files = files;
        this.folders = folders;
        this.size = size;
    }

    public static FolderStats of(long items, long files, long folders, long size) {
        return new FolderStats(items, files, folders, size);
    }

    public long items() {
        return items;
    }

    public long files() {
        return files;
    }

    public long folders() {
        return folders;
    }

    public long size() {
        return size;
    }

    public long getItems() {
        return items;
    }

    public long getFiles() {
        return files;
    }

    public long getFolders() {
        return folders;
    }

    public long getSize() {
        return size;
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
        if (!(o instanceof FolderStats that)) return false;

        return items == that.items
                && files == that.files
                && folders == that.folders
                && size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, files, folders, size);
    }

    @Override
    public String toString() {
        return "FolderStats{" +
                "items=" + items +
                ", files=" + files +
                ", folders=" + folders +
                ", size=" + size +
                '}';
    }
}