package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.RoutingContext;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

public class MapRoutingContext implements RoutingContext {

    private final LinkedHashMap<String, String> context;

    public MapRoutingContext() {
        this.context = new LinkedHashMap<>();
    }

    @Override
    public String require(String key) {
        return Optional.ofNullable(key)
                .map(context::get)
                .orElseThrow(() -> new IllegalStateException("%s not found in context".formatted(key)));
    }

    @Override
    public Optional<String> optional(String key) {
        return Optional.ofNullable(key).map(context::get);
    }

    @Override
    public void set(String key, String value) {
        context.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapRoutingContext that = (MapRoutingContext) o;
        return Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context);
    }

    @Override
    public String toString() {
        return "MapRoutingContext{" +
                "context=" + context +
                '}';
    }
}
