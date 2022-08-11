package com.github.toastshaman.httprouter.domain;

import java.util.Objects;

public record RoutingPatternElement(String value) {

    public RoutingPatternElement {
        if (Objects.requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException("pattern element must not be empty");
        }
    }
}
