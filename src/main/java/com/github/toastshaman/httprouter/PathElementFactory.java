package com.github.toastshaman.httprouter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class PathElementFactory {

    private PathElementFactory() {
    }

    public static List<PathElement> parse(String path) {
        Objects.requireNonNull(path, "path not provided");

        return Arrays.stream(normalizePath(path).split("/"))
                .filter(it -> !it.isBlank())
                .map(PathElementFactory::eval)
                .collect(toList());
    }

    public static PathElement eval(String element) {
        Objects.requireNonNull(element, "element not provided");

        return Stream.<Function<String, PathElement>>of(
                        RegexPathElement::parseOrNull,
                        VariablePathElement::parseOrNull
                )
                .map(it -> it.apply(element))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(new StaticPathElement(element));
    }

    private static String normalizePath(String path) {
        return Arrays.stream(path.split("/"))
                .filter(it -> !it.isBlank())
                .collect(joining("/", "/", ""));
    }
}
