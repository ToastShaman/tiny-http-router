package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record Path(String value) {

    public Path {
        if (Objects.requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException("path must not be empty");
        }
    }

    public List<PathElement> explode() {
        return Arrays.stream(value.split("/"))
                .filter(it -> !it.isBlank())
                .map(PathElement::new)
                .toList();
    }

    public static Path from(Request request) {
        return new Path(request.path());
    }
}
