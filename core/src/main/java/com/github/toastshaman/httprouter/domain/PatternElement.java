package com.github.toastshaman.httprouter.domain;

import java.util.Objects;

public record PatternElement(String value) {

    public PatternElement {
        if (Objects.requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException("pattern element must not be empty");
        }
    }
}
