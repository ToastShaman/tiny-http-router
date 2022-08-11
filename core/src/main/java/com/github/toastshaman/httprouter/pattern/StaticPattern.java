package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.PathElement;
import com.github.toastshaman.httprouter.domain.PatternElement;

import java.util.Objects;

public record StaticPattern(PatternElement element) implements MatchingPatternElement {

    public StaticPattern {
        Objects.requireNonNull(element);
    }

    @Override
    public boolean match(RoutingContext context, PathElement path) {
        return element.value().equals(path.value());
    }

    public static StaticPattern parseOrNull(PatternElement element) {
        if (element.value().matches("^[a-zA-Z0-9]+$")) {
            return new StaticPattern(element);
        }
        return null;
    }
}
