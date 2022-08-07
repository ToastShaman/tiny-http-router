package com.github.toastshaman.httprouter.domain;

import java.util.Objects;

public record Path(String value) {

    public Path {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException("path must not be blank");
        }

        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("%s path must start with /".formatted(value));
        }

        if (value.endsWith("/")) {
            throw new IllegalArgumentException("%s path must not end with /".formatted(value));
        }
    }

    public Path prefix(String prefix) {
        return Path.of("%s%s".formatted(prefix, value));
    }

    public NormalizedPath normalize() {
        return new NormalizedPath(this);
    }

    public static Path of(String path) {
        return new Path(path);
    }
}
