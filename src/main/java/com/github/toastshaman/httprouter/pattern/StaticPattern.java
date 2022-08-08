package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.PathElement;

import java.util.Objects;

public record StaticPattern(String value) implements PatternElement {

    public StaticPattern {
        if (Objects.requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException("must not be empty");
        }
    }
    @Override
    public boolean match(RoutingContext context, PathElement path) {
        return value.equals(path.value());
    }

    public static StaticPattern parseOrNull(String element) {
        if (element.matches("[a-zA-Z0-9]+")) {
            return new StaticPattern(element);
        }
        return null;
    }

}
