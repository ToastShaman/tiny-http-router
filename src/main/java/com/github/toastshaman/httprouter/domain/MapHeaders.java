package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.Header;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MapHeaders implements Header {

    private final LinkedHashMap<String, String> headers = new LinkedHashMap<>();

    public MapHeaders() {
    }

    @Override
    public String get(String key) {
        Objects.requireNonNull(key);
        return headers.get(key);
    }

    @Override
    public void set(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        headers.put(key, value);
    }

    @Override
    public void remove(String key) {
        Objects.requireNonNull(key);
        headers.remove(key);
    }

    public Map<String, String> toMap() {
        return Map.copyOf(headers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapHeaders that = (MapHeaders) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return "MapHeaders{" +
                "headers=" + headers +
                '}';
    }
}
