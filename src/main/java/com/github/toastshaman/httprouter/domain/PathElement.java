package com.github.toastshaman.httprouter.domain;

import java.util.Objects;

public record PathElement(String value) {

    public PathElement {
        if (Objects.requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException("path element must not be empty");
        }
    }
}
