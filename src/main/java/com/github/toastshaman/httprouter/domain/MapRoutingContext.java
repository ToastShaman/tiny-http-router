package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.RoutingContext;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

public class MapRoutingContext implements RoutingContext {

    private final LinkedHashMap<String, String> context = new LinkedHashMap<>();

    @Override
    public String require(String key) {
        return Objects.requireNonNull(context.get(key));
    }

    @Override
    public Optional<String> optional(String key) {
        return Optional.ofNullable(context.get(key));
    }

    @Override
    public void set(String key, String value) {
        context.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }
}
