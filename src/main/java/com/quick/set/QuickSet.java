package com.quick.set;

import com.quick.list.QuickList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class QuickSet<T> {

    private final Set<T> items;

    public QuickSet() {
        this.items = new LinkedHashSet<>();
    }

    public QuickSet(Set<T> items) {
        this.items = new LinkedHashSet<>(requireSet(items, "QuickSet constructor error: items cannot be null"));
    }

    @SafeVarargs
    public QuickSet(T... items) {
        this.items = new LinkedHashSet<>();
        if (items != null) {
            this.items.addAll(Arrays.asList(items));
        }
    }

    private QuickSet(Set<T> items, boolean direct) {
        this.items = requireSet(items, "QuickSet constructor error: items cannot be null");
    }

    public static <T> QuickSet<T> hash() {
        return new QuickSet<>(new HashSet<>(), true);
    }

    public static <T> QuickSet<T> linked() {
        return new QuickSet<>(new LinkedHashSet<>(), true);
    }

    public static <T> QuickSet<T> tree() {
        return new QuickSet<>(new TreeSet<>(), true);
    }

    public static <T> QuickSet<T> hash(Set<T> items) {
        return new QuickSet<>(new HashSet<>(requireSet(items, "QuickSet hash error: items cannot be null")), true);
    }

    public static <T> QuickSet<T> linked(Set<T> items) {
        return new QuickSet<>(new LinkedHashSet<>(requireSet(items, "QuickSet linked error: items cannot be null")), true);
    }

    public static <T> QuickSet<T> tree(Set<T> items) {
        try {
            return new QuickSet<>(new TreeSet<>(requireSet(items, "QuickSet tree error: items cannot be null")), true);
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickSetException(
                    "QuickSet tree error: TreeSet requires non-null items that are mutually comparable",
                    e
            );
        }
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isNotEmpty() {
        return !items.isEmpty();
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    public boolean has(T item) {
        return items.contains(item);
    }

    public boolean containsAll(Set<T> other) {
        return items.containsAll(requireSet(other, "QuickSet containsAll error: other cannot be null"));
    }

    public boolean containsAll(QuickSet<T> other) {
        requireQuickSet(other, "QuickSet containsAll error: other cannot be null");
        return items.containsAll(other.items);
    }

    public QuickSet<T> add(T item) {
        try {
            items.add(item);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickSetException(
                    "QuickSet add error: invalid item for this set type",
                    e
            );
        }
    }

    @SafeVarargs
    public final QuickSet<T> addAll(T... items) {
        if (items == null) {
            throw new QuickSetException("QuickSet addAll error: items cannot be null");
        }

        for (T item : items) {
            add(item);
        }

        return this;
    }

    public QuickSet<T> addAll(Set<T> items) {
        Set<T> safeItems = requireSet(items, "QuickSet addAll error: items cannot be null");

        for (T item : safeItems) {
            add(item);
        }

        return this;
    }

    public QuickSet<T> addAll(QuickSet<T> other) {
        requireQuickSet(other, "QuickSet addAll error: other cannot be null");
        return addAll(other.items);
    }

    public QuickSet<T> remove(T item) {
        items.remove(item);
        return this;
    }

    public QuickSet<T> removeAll(Set<T> other) {
        items.removeAll(requireSet(other, "QuickSet removeAll error: other cannot be null"));
        return this;
    }

    public QuickSet<T> removeAll(QuickSet<T> other) {
        requireQuickSet(other, "QuickSet removeAll error: other cannot be null");
        items.removeAll(other.items);
        return this;
    }

    public QuickSet<T> clear() {
        items.clear();
        return this;
    }

    public T first() {
        if (items.isEmpty()) {
            return null;
        }
        return items.iterator().next();
    }

    public T random() {
        if (items.isEmpty()) {
            return null;
        }

        var list = new QuickList<>(items.stream().toList());
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(index);
    }

    public QuickSet<T> shuffle() {
        var list = new QuickList<>(items.stream().toList()).shuffle();
        return new QuickSet<>(new LinkedHashSet<>(list.toList()), true);
    }

    public QuickSet<T> union(QuickSet<T> other) {
        requireQuickSet(other, "QuickSet union error: other cannot be null");
        Set<T> result = copySet();
        result.addAll(other.items);
        return new QuickSet<>(result, true);
    }

    public QuickSet<T> union(Set<T> other) {
        Set<T> result = copySet();
        result.addAll(requireSet(other, "QuickSet union error: other cannot be null"));
        return new QuickSet<>(result, true);
    }

    public QuickSet<T> intersect(QuickSet<T> other) {
        requireQuickSet(other, "QuickSet intersect error: other cannot be null");
        Set<T> result = copySet();
        result.retainAll(other.items);
        return new QuickSet<>(result, true);
    }

    public QuickSet<T> intersect(Set<T> other) {
        Set<T> result = copySet();
        result.retainAll(requireSet(other, "QuickSet intersect error: other cannot be null"));
        return new QuickSet<>(result, true);
    }

    public QuickSet<T> difference(QuickSet<T> other) {
        requireQuickSet(other, "QuickSet difference error: other cannot be null");
        Set<T> result = copySet();
        result.removeAll(other.items);
        return new QuickSet<>(result, true);
    }

    public QuickSet<T> difference(Set<T> other) {
        Set<T> result = copySet();
        result.removeAll(requireSet(other, "QuickSet difference error: other cannot be null"));
        return new QuickSet<>(result, true);
    }

    public QuickSet<T> filter(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickSet filter error: condition cannot be null");

        return new QuickSet<>(
                items.stream()
                        .filter(condition)
                        .collect(Collectors.toCollection(this::createEmptySameTypeSet)),
                true
        );
    }

    public T find(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickSet find error: condition cannot be null");

        return items.stream()
                .filter(condition)
                .findFirst()
                .orElse(null);
    }

    public boolean any(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickSet any error: condition cannot be null");
        return items.stream().anyMatch(condition);
    }

    public boolean all(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickSet all error: condition cannot be null");
        return items.stream().allMatch(condition);
    }

    public boolean none(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickSet none error: condition cannot be null");
        return items.stream().noneMatch(condition);
    }

    public T min() {
        if (items.isEmpty()) {
            return null;
        }

        T minItem = null;

        for (T item : items) {
            if (item == null) {
                continue;
            }

            if (minItem == null || compareNaturallySafely(item, minItem) < 0) {
                minItem = item;
            }
        }

        return minItem;
    }

    public T max() {
        if (items.isEmpty()) {
            return null;
        }

        T maxItem = null;

        for (T item : items) {
            if (item == null) {
                continue;
            }

            if (maxItem == null || compareNaturallySafely(item, maxItem) > 0) {
                maxItem = item;
            }
        }

        return maxItem;
    }

    public QuickSet<T> forEach(Consumer<T> action) {
        Objects.requireNonNull(action, "QuickSet forEach error: action cannot be null");
        items.forEach(action);
        return this;
    }

    public QuickSet<T> copy() {
        return new QuickSet<>(copySet(), true);
    }

    public QuickList<T> toList() {
        return new QuickList<>(items.stream().toList());
    }

    public Set<T> toSet() {
        return copySet();
    }

    private Set<T> copySet() {
        if (items instanceof TreeSet) {
            return new TreeSet<>(items);
        }
        if (items instanceof HashSet && !(items instanceof LinkedHashSet)) {
            return new HashSet<>(items);
        }
        return new LinkedHashSet<>(items);
    }

    private Set<T> createEmptySameTypeSet() {
        if (items instanceof TreeSet) {
            return new TreeSet<>();
        }
        if (items instanceof HashSet && !(items instanceof LinkedHashSet)) {
            return new HashSet<>();
        }
        return new LinkedHashSet<>();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compareNaturallySafely(T a, T b) {
        if (a == b) {
            return 0;
        }

        if (a == null) {
            return -1;
        }

        if (b == null) {
            return 1;
        }

        if (!(a instanceof Comparable)) {
            throw new QuickSetException("QuickSet compare error: item is not Comparable: " + a);
        }

        try {
            return ((Comparable) a).compareTo(b);
        } catch (ClassCastException e) {
            throw new QuickSetException(
                    "QuickSet compare error: items are not mutually comparable: "
                            + a.getClass().getName() + " and " + b.getClass().getName(),
                    e
            );
        }
    }

    private static <T> Set<T> requireSet(Set<T> set, String message) {
        if (set == null) {
            throw new QuickSetException(message);
        }
        return set;
    }

    private static <T> QuickSet<T> requireQuickSet(QuickSet<T> set, String message) {
        if (set == null) {
            throw new QuickSetException(message);
        }
        return set;
    }

    @Override
    public String toString() {
        return items.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuickSet<?> other)) {
            return false;
        }
        return Objects.equals(items, other.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    public static class QuickSetException extends RuntimeException {
        public QuickSetException(String message) {
            super(message);
        }

        public QuickSetException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}