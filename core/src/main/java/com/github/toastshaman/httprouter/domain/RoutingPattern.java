package com.github.toastshaman.httprouter.domain;

import java.util.Arrays;
import java.util.List;

public record RoutingPattern(String value) {

    public RoutingPattern {
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("routing pattern must begin with '/' in '%s'".formatted(value));
        }

        if (!value.equals("/") && value.endsWith("/")) {
            throw new IllegalArgumentException("routing pattern must not end with '/' in '%s'".formatted(value));
        }
    }

    public RoutingPattern concat(RoutingPattern other) {
        return RoutingPattern.of(value + other.value);
    }

    public List<PatternElement> split() {
        return Arrays.stream(value.split("/"))
                .filter(it -> !it.isBlank())
                .map(PatternElement::new)
                .toList();
    }

    public static RoutingPattern of(String pattern) {
        return new RoutingPattern(pattern);
    }
}
