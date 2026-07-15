package com.quick.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class QuickList<T> {

    private final List<T> items;

    public QuickList(List<T> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    @SafeVarargs
    public QuickList(T... items) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(Arrays.asList(items));
        }
    }

    public int size() {
        return items.size();
    }

    public long count() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isNotEmpty() {
        return !items.isEmpty();
    }

    public T first() {
        return items.isEmpty() ? null : items.get(0);
    }

    public T last() {
        return items.isEmpty() ? null : items.get(items.size() - 1);
    }

    public T get(int index) {
        return items.get(index);
    }

    public QuickList<T> set(int index, T value) {
        items.set(index, value);
        return this;
    }

    public int indexOf(T item) {
        return items.indexOf(item);
    }

    public int lastIndexOf(T item) {
        return items.lastIndexOf(item);
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    public QuickList<T> add(T item) {
        items.add(item);
        return this;
    }

    public QuickList<T> add(int index, T item) {
        items.add(index, item);
        return this;
    }

    public QuickList<T> addAll(List<T> items) {
        if (items == null) {
            throw new QuickListException("QuickList addAll error: items cannot be null");
        }

        this.items.addAll(items);
        return this;
    }

    @SafeVarargs
    public final QuickList<T> addAll(T... items) {
        if (items == null) {
            throw new QuickListException("QuickList addAll error: items cannot be null");
        }

        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public QuickList<T> remove(T item) {
        items.remove(item);
        return this;
    }

    public QuickList<T> removeAt(int index) {
        items.remove(index);
        return this;
    }

    public QuickList<T> removeFirst() {
        if (!items.isEmpty()) {
            items.remove(0);
        }
        return this;
    }

    public QuickList<T> removeLast() {
        if (!items.isEmpty()) {
            items.remove(items.size() - 1);
        }
        return this;
    }

    public QuickList<T> clear() {
        items.clear();
        return this;
    }

    public QuickList<T> reverse() {
        List<T> result = new ArrayList<>(items);
        Collections.reverse(result);
        return new QuickList<>(result);
    }

    public QuickList<T> shuffle() {
        List<T> result = new ArrayList<>(items);
        Collections.shuffle(result);
        return new QuickList<>(result);
    }

    public QuickList<T> sort() {
        if (items.isEmpty()) {
            return new QuickList<>(items);
        }

        List<T> result = new ArrayList<>(items);
        result.sort(this::compareNaturallySafely);
        return new QuickList<>(result);
    }

    public QuickList<T> sort(Comparator<T> comparator) {
        if (comparator == null) {
            throw new QuickListException("QuickList sort error: comparator cannot be null");
        }

        List<T> result = new ArrayList<>(items);
        result.sort(comparator);
        return new QuickList<>(result);
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

    public double sum() {
        double total = 0;

        for (T item : items) {
            if (item == null) {
                continue;
            }

            if (!(item instanceof Number)) {
                throw new QuickListException("QuickList sum error: items must be numbers");
            }

            total += ((Number) item).doubleValue();
        }

        return total;
    }

    public QuickList<T> filter(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickList filter error: condition cannot be null");

        return new QuickList<>(
                items.stream()
                        .filter(condition)
                        .collect(Collectors.toList())
        );
    }

    public <R> QuickList<R> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper, "QuickList map error: mapper cannot be null");

        return new QuickList<>(
                items.stream()
                        .map(mapper)
                        .collect(Collectors.toList())
        );
    }

    public T find(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickList find error: condition cannot be null");

        return items.stream()
                .filter(condition)
                .findFirst()
                .orElse(null);
    }

    public boolean any(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickList any error: condition cannot be null");
        return items.stream().anyMatch(condition);
    }

    public boolean all(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickList all error: condition cannot be null");
        return items.stream().allMatch(condition);
    }

    public boolean none(Predicate<T> condition) {
        Objects.requireNonNull(condition, "QuickList none error: condition cannot be null");
        return items.stream().noneMatch(condition);
    }

    public QuickList<T> take(int count) {
        if (count <= 0) {
            return new QuickList<>(new ArrayList<>());
        }

        return new QuickList<>(
                items.stream()
                        .limit(count)
                        .collect(Collectors.toList())
        );
    }

    public QuickList<T> drop(int count) {
        if (count <= 0) {
            return new QuickList<>(items);
        }

        return new QuickList<>(
                items.stream()
                        .skip(count)
                        .collect(Collectors.toList())
        );
    }

    public QuickList<T> distinct() {
        return new QuickList<>(
                items.stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
    }

    public QuickList<T> forEach(Consumer<T> action) {
        Objects.requireNonNull(action, "QuickList forEach error: action cannot be null");
        items.forEach(action);
        return this;
    }

    public QuickList<T> copy() {
        return new QuickList<>(items);
    }

    public QuickList<T> cloneList() {
        return copy();
    }

    public String join(String separator) {
        if (separator == null) {
            throw new QuickListException("QuickList join error: separator cannot be null");
        }

        return items.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(separator));
    }

    public List<T> toList() {
        return new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return items.toString();
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
            throw new QuickListException("QuickList compare error: item is not Comparable: " + a);
        }

        try {
            return ((Comparable) a).compareTo(b);
        } catch (ClassCastException e) {
            throw new QuickListException(
                    "QuickList compare error: items are not mutually comparable: "
                            + a.getClass().getName() + " and " + b.getClass().getName(),
                    e
            );
        }
    }

    public static class QuickListException extends RuntimeException {
        public QuickListException(String message) {
            super(message);
        }

        public QuickListException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}