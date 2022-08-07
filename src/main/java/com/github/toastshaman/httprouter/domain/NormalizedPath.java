package com.github.toastshaman.httprouter.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class NormalizedPath {

    private final String value;

    public NormalizedPath(Path path) {
        Objects.requireNonNull(path);
        this.value = Arrays.stream(path.value().split("/"))
                .filter(it -> !it.isBlank())
                .collect(joining("/", "/", ""));
    }

    public <T> List<T> map(Function<String, T> fn) {
        return Arrays.stream(value.split("/"))
                .filter(it -> !it.isBlank())
                .map(fn)
                .collect(toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalizedPath that = (NormalizedPath) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "NormalizedPath{" +
                "value='" + value + '\'' +
                '}';
    }
}
