package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Path;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface RouterFactory<REQUEST, RESPONSE> {

    Route<REQUEST, RESPONSE> create(
            Method method,
            Path path,
            RoutingHandler<REQUEST, RESPONSE> handler);

   static <R, T> RouterFactory<R, T> defaultFactory() {
        return (method, path, handler) -> {
            List<PathElement> pathElements = path
                    .normalize()
                    .map(RouterFactory::getPathElement);
            return new SimpleRoute<>(method, path, handler, pathElements);
        };
    }
    private static PathElement getPathElement(String element) {
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
