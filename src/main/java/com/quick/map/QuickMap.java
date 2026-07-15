package com.quick.map;

import com.quick.list.QuickList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class QuickMap<K, V> {

    private final Map<K, V> items;

    public QuickMap() {
        this.items = new LinkedHashMap<>();
    }

    public QuickMap(Map<K, V> items) {
        this.items = new LinkedHashMap<>(requireMap(items, "QuickMap constructor error: items cannot be null"));
    }

    private QuickMap(Map<K, V> items, boolean useDirectMap) {
        this.items = requireMap(items, "QuickMap constructor error: items cannot be null");
    }

    public static <K, V> QuickMap<K, V> hash() {
        return new QuickMap<>(new HashMap<>(), true);
    }

    public static <K, V> QuickMap<K, V> linked() {
        return new QuickMap<>(new LinkedHashMap<>(), true);
    }

    public static <K, V> QuickMap<K, V> tree() {
        return new QuickMap<>(new TreeMap<>(), true);
    }

    public static <K, V> QuickMap<K, V> hash(Map<K, V> items) {
        return new QuickMap<>(new HashMap<>(requireMap(items, "QuickMap hash error: items cannot be null")), true);
    }

    public static <K, V> QuickMap<K, V> linked(Map<K, V> items) {
        return new QuickMap<>(new LinkedHashMap<>(requireMap(items, "QuickMap linked error: items cannot be null")), true);
    }

    public static <K, V> QuickMap<K, V> tree(Map<K, V> items) {
        try {
            return new QuickMap<>(new TreeMap<>(requireMap(items, "QuickMap tree error: items cannot be null")), true);
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap tree error: TreeMap requires non-null keys that are mutually comparable",
                    e
            );
        }
    }

    public QuickMap<K, V> put(K key, V value) {
        try {
            items.put(key, value);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap put error: invalid key for this map type",
                    e
            );
        }
    }

    public QuickMap<K, V> putAll(Map<K, V> map) {
        Map<K, V> safeMap = requireMap(map, "QuickMap putAll error: map cannot be null");

        try {
            items.putAll(safeMap);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap putAll error: one or more keys are invalid for this map type",
                    e
            );
        }
    }

    public QuickMap<K, V> putIfAbsent(K key, V value) {
        try {
            items.putIfAbsent(key, value);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap putIfAbsent error: invalid key for this map type",
                    e
            );
        }
    }

    public V get(K key) {
        return items.get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        return items.getOrDefault(key, defaultValue);
    }

    public QuickMap<K, V> replace(K key, V value) {
        try {
            items.replace(key, value);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap replace error: invalid key for this map type",
                    e
            );
        }
    }

    public boolean replace(K key, V oldValue, V newValue) {
        try {
            return items.replace(key, oldValue, newValue);
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap replace error: invalid key for this map type",
                    e
            );
        }
    }

    public QuickMap<K, V> remove(K key) {
        items.remove(key);
        return this;
    }

    public boolean remove(K key, V value) {
        return items.remove(key, value);
    }

    public QuickMap<K, V> merge(K key, V value, BiFunction<V, V, V> remappingFunction) {
        Objects.requireNonNull(remappingFunction, "QuickMap merge error: remappingFunction cannot be null");

        try {
            items.merge(key, value, remappingFunction);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap merge error: invalid key or value for this map type",
                    e
            );
        }
    }

    public QuickMap<K, V> computeIfAbsent(K key, Function<K, V> mappingFunction) {
        Objects.requireNonNull(mappingFunction, "QuickMap computeIfAbsent error: mappingFunction cannot be null");

        try {
            items.computeIfAbsent(key, mappingFunction);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap computeIfAbsent error: invalid key for this map type",
                    e
            );
        }
    }

    public QuickMap<K, V> computeIfPresent(K key, BiFunction<K, V, V> remappingFunction) {
        Objects.requireNonNull(remappingFunction, "QuickMap computeIfPresent error: remappingFunction cannot be null");

        try {
            items.computeIfPresent(key, remappingFunction);
            return this;
        } catch (NullPointerException | ClassCastException e) {
            throw new QuickMapException(
                    "QuickMap computeIfPresent error: invalid key for this map type",
                    e
            );
        }
    }

    public boolean containsKey(K key) {
        return items.containsKey(key);
    }

    public boolean containsValue(V value) {
        return items.containsValue(value);
    }

    public boolean hasKey(K key) {
        return items.containsKey(key);
    }

    public boolean hasValue(V value) {
        return items.containsValue(value);
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

    public K firstKey() {
        if (items.isEmpty()) {
            return null;
        }
        return items.keySet().iterator().next();
    }

    public V firstValue() {
        if (items.isEmpty()) {
            return null;
        }
        return items.values().iterator().next();
    }

    public QuickMap<K, V> clear() {
        items.clear();
        return this;
    }

    public QuickList<K> keys() {
        return new QuickList<>(items.keySet().stream().toList());
    }

    public QuickList<V> values() {
        return new QuickList<>(items.values().stream().toList());
    }

    public QuickList<Map.Entry<K, V>> entries() {
        return new QuickList<>(items.entrySet().stream().toList());
    }

    public QuickMap<K, V> forEach(BiConsumer<K, V> action) {
        Objects.requireNonNull(action, "QuickMap forEach error: action cannot be null");
        items.forEach(action);
        return this;
    }

    public QuickMap<K, V> copy() {
        if (items instanceof TreeMap) {
            return QuickMap.tree(items);
        }
        if (items instanceof HashMap && !(items instanceof LinkedHashMap)) {
            return QuickMap.hash(items);
        }
        return QuickMap.linked(items);
    }

    public Map<K, V> toMap() {
        if (items instanceof TreeMap) {
            return new TreeMap<>(items);
        }
        if (items instanceof HashMap && !(items instanceof LinkedHashMap)) {
            return new HashMap<>(items);
        }
        return new LinkedHashMap<>(items);
    }

    @Override
    public String toString() {
        return items.toString();
    }

    private static <K, V> Map<K, V> requireMap(Map<K, V> map, String message) {
        if (map == null) {
            throw new QuickMapException(message);
        }
        return map;
    }

    public static class QuickMapException extends RuntimeException {
        public QuickMapException(String message) {
            super(message);
        }

        public QuickMapException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}