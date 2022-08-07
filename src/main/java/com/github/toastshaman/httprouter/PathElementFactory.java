package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Path;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PathElementFactory {

    private PathElementFactory() {
    }

    public static List<PathElement> parse(Path path) {
        Objects.requireNonNull(path, "path not provided");

        return path.normalize()
                .split()
                .stream()
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
}
