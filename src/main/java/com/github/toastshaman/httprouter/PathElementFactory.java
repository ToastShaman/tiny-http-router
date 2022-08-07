package com.github.toastshaman.httprouter;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface PathElementFactory {

    static PathElementFactory create() {
        return element -> {
            Objects.requireNonNull(element, "element not provided");

            return Stream.<Function<String, PathElement>>of(
                            RegexPathElement::parseOrNull,
                            VariablePathElement::parseOrNull
                    )
                    .map(it -> it.apply(element))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(new StaticPathElement(element));
        };
    }

    PathElement eval(String element);
}
