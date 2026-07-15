package com.quick;

import com.quick.file.QuickFile;
import com.quick.folder.QuickFolder;
import com.quick.list.QuickList;
import com.quick.loop.QuickLoop;
import com.quick.map.QuickMap;
import com.quick.number.QuickNumber;
import com.quick.path.QuickPath;
import com.quick.set.QuickSet;
import com.quick.string.QuickString;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final class Quick {

    private Quick() {
        throw new RuntimeException("Quick cannot be instantiated");
    }

    public static QuickFile file(String path) {
        return new QuickFile(path);
    }

    public static QuickFolder folder(String path) {
        return new QuickFolder(path);
    }

    public static QuickLoop loop(int end) {
        return new QuickLoop(end);
    }

    public static QuickLoop loop(int start, int end) {
        return new QuickLoop(start, end);
    }

    public static QuickString str(String text) {
        return new QuickString(text);
    }

    public static QuickNumber num(int value) {
        return new QuickNumber(value);
    }

    public static QuickNumber num(long value) {
        return new QuickNumber(value);
    }

    public static QuickNumber num(double value) {
        return new QuickNumber(value);
    }

    public static QuickPath path(String path) {
        return new QuickPath(path);
    }

    public static QuickPath path(String first, String... more) {
        return new QuickPath(first, more);
    }

    @SafeVarargs
    public static <T> QuickList<T> list(T... items) {
        return new QuickList<>(items);
    }

    public static <T> QuickList<T> list(List<T> items) {
        return new QuickList<>(items);
    }

    public static <K, V> QuickMap<K, V> map() {
        return new QuickMap<>();
    }

    public static <K, V> QuickMap<K, V> map(Map<K, V> items) {
        return new QuickMap<>(items);
    }

    public static <K, V> QuickMap<K, V> hashMap() {
        return QuickMap.hash();
    }

    public static <K, V> QuickMap<K, V> hashMap(Map<K, V> items) {
        return QuickMap.hash(items);
    }

    public static <K, V> QuickMap<K, V> linkedMap() {
        return QuickMap.linked();
    }

    public static <K, V> QuickMap<K, V> linkedMap(Map<K, V> items) {
        return QuickMap.linked(items);
    }

    public static <K, V> QuickMap<K, V> treeMap() {
        return QuickMap.tree();
    }

    public static <K, V> QuickMap<K, V> treeMap(Map<K, V> items) {
        return QuickMap.tree(items);
    }

    @SafeVarargs
    public static <T> QuickSet<T> set(T... items) {
        return new QuickSet<>(items);
    }

    public static <T> QuickSet<T> set(Set<T> items) {
        return new QuickSet<>(items);
    }

    public static <T> QuickSet<T> hashSet() {
        return QuickSet.hash();
    }

    public static <T> QuickSet<T> hashSet(Set<T> items) {
        return QuickSet.hash(items);
    }

    public static <T> QuickSet<T> linkedSet() {
        return QuickSet.linked();
    }

    public static <T> QuickSet<T> linkedSet(Set<T> items) {
        return QuickSet.linked(items);
    }

    public static <T> QuickSet<T> treeSet() {
        return QuickSet.tree();
    }

    public static <T> QuickSet<T> treeSet(Set<T> items) {
        return QuickSet.tree(items);
    }

    public static void print() {
        System.out.println();
    }

    public static void print(Object value) {
        System.out.println(value);
    }

    public static void print(boolean condition, Object ifValue, Object elseValue) {
        System.out.println(condition ? ifValue : elseValue);
    }

    public static void println(Object value) {
        System.out.println(value);
    }

    public static void out(Object value) {
        System.out.print(value);
    }

    public static void newLine() {
        System.out.println();
    }

    public static void repeat(int times, Runnable action) {
        if (times < 0) {
            throw new RuntimeException("Quick repeat error: times cannot be negative");
        }

        for (int i = 0; i < times; i++) {
            action.run();
        }
    }

    public static void times(int times, IntConsumer action) {
        if (times < 0) {
            throw new RuntimeException("Quick times error: times cannot be negative");
        }

        for (int i = 0; i < times; i++) {
            action.accept(i);
        }
    }

    public static <T> void each(Iterable<T> items, Consumer<T> action) {
        for (T item : items) {
            action.accept(item);
        }
    }

    public static <T> void each(T[] items, Consumer<T> action) {
        for (T item : items) {
            action.accept(item);
        }
    }

    public static void each(String text, Consumer<Character> action) {
        for (char c : text.toCharArray()) {
            action.accept(c);
        }
    }

    public static void each(QuickFile file, Consumer<String> action) {
        file.read().lines().forEach(action);
    }

    public static void each(QuickFolder folder, Consumer<Path> action) {
        folder.list().forEach(action);
    }

    public static final class File {
        private File() {
            throw new RuntimeException("Quick.File cannot be instantiated");
        }

        public static QuickFile open(String path) {
            return Quick.file(path);
        }
    }

    public static final class Folder {
        private Folder() {
            throw new RuntimeException("Quick.Folder cannot be instantiated");
        }

        public static QuickFolder open(String path) {
            return Quick.folder(path);
        }
    }

    public static final class Loop {
        private Loop() {
            throw new RuntimeException("Quick.Loop cannot be instantiated");
        }

        public static QuickLoop open(int end) {
            return Quick.loop(end);
        }

        public static QuickLoop open(int start, int end) {
            return Quick.loop(start, end);
        }
    }

    public static final class Str {
        private Str() {
            throw new RuntimeException("Quick.Str cannot be instantiated");
        }

        public static QuickString open(String text) {
            return Quick.str(text);
        }
    }

    public static final class Num {
        private Num() {
            throw new RuntimeException("Quick.Num cannot be instantiated");
        }

        public static QuickNumber of(int value) {
            return Quick.num(value);
        }

        public static QuickNumber of(long value) {
            return Quick.num(value);
        }

        public static QuickNumber of(double value) {
            return Quick.num(value);
        }
    }

    public static final class PathX {
        private PathX() {
            throw new RuntimeException("Quick.PathX cannot be instantiated");
        }

        public static QuickPath open(String path) {
            return Quick.path(path);
        }

        public static QuickPath open(String first, String... more) {
            return Quick.path(first, more);
        }
    }

    public static final class ListX {
        private ListX() {
            throw new RuntimeException("Quick.ListX cannot be instantiated");
        }

        @SafeVarargs
        public static <T> QuickList<T> open(T... items) {
            return Quick.list(items);
        }

        public static <T> QuickList<T> open(List<T> items) {
            return Quick.list(items);
        }
    }

    public static final class MapX {
        private MapX() {
            throw new RuntimeException("Quick.MapX cannot be instantiated");
        }

        public static <K, V> QuickMap<K, V> open() {
            return Quick.map();
        }

        public static <K, V> QuickMap<K, V> open(Map<K, V> items) {
            return Quick.map(items);
        }

        public static <K, V> QuickMap<K, V> hash() {
            return Quick.hashMap();
        }

        public static <K, V> QuickMap<K, V> linked() {
            return Quick.linkedMap();
        }

        public static <K, V> QuickMap<K, V> tree() {
            return Quick.treeMap();
        }
    }

    public static final class SetX {
        private SetX() {
            throw new RuntimeException("Quick.SetX cannot be instantiated");
        }

        @SafeVarargs
        public static <T> QuickSet<T> open(T... items) {
            return Quick.set(items);
        }

        public static <T> QuickSet<T> open(Set<T> items) {
            return Quick.set(items);
        }

        public static <T> QuickSet<T> hash() {
            return Quick.hashSet();
        }

        public static <T> QuickSet<T> linked() {
            return Quick.linkedSet();
        }

        public static <T> QuickSet<T> tree() {
            return Quick.treeSet();
        }
    }
}