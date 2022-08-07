package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.PathElement;
import com.github.toastshaman.httprouter.Route;
import com.github.toastshaman.httprouter.RouterFactory;
import com.github.toastshaman.httprouter.RoutingHandler;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class SimpleRouterFactory<REQUEST, RESPONSE> implements RouterFactory<REQUEST, RESPONSE> {

    @Override
    public Route<REQUEST, RESPONSE> create(Method method,
                                           Path path,
                                           RoutingHandler<REQUEST, RESPONSE> handler) {
        List<PathElement> pathElements = path
                .normalize()
                .map(this::getPathElement);
        return new SimpleRoute<>(method, path, handler, pathElements);
    }

    private PathElement getPathElement(String element) {
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
