package com.github.toastshaman.httprouter.domain;

import java.util.Arrays;
import java.util.List;

public record Pattern(String value) {

    public Pattern {
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("routing pattern must begin with '/' in '%s'".formatted(value));
        }

        if (value.endsWith("/")) {
            throw new IllegalArgumentException("routing pattern must not end with '/' in '%s'".formatted(value));
        }
    }

    public Pattern concat(Pattern other) {
        return Pattern.of(value + other.value);
    }

    public List<String> explode() {
        return Arrays.stream(value.split("/"))
                .filter(it -> !it.isBlank())
                .toList();
    }

    public static Pattern of(String pattern) {
        return new Pattern(pattern);
    }
}
