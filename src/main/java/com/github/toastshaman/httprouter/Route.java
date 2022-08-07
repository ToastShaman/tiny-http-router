package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Path;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

public class Route<REQUEST, RESPONSE> {

    public final Method method;
    public final Path path;
    public final RoutingHandler<REQUEST, RESPONSE> handler;

    private final List<PathElement> pathElements;

    public Route(Method method,
                 Path path,
                 RoutingHandler<REQUEST, RESPONSE> handler) {
        this.method = Objects.requireNonNull(method, "method not provided");
        this.handler = Objects.requireNonNull(handler, "handler not provided");
        this.path = Objects.requireNonNull(path, "path not provided");
        this.pathElements = PathElementFactory.parse(path);
    }

    public Optional<MatchResult<REQUEST, RESPONSE>> matches(String offeredMethod, String offeredPath) {
        if (!method.name().equalsIgnoreCase(offeredMethod)) {
            return Optional.empty();
        }

        var elements = Arrays.stream(offeredPath.split("/"))
                .filter(it -> !it.isBlank())
                .toList();

        if (pathElements.size() != elements.size()) {
            return Optional.empty();
        }

        var matches = IntStream.range(0, pathElements.size())
                .mapToObj(i -> pathElements.get(i).matchesOrNull(elements.get(i)))
                .toList();

        var allMatch = matches.stream().allMatch(Objects::nonNull);

        if (allMatch) {
            var combined = matches.stream()
                    .map(it -> it.context)
                    .flatMap(it -> it.entrySet().stream())
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            return Optional.of(new MatchResult<>(this, MatchContext.of(combined)));
        }

        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route<?, ?> route = (Route<?, ?>) o;
        return Objects.equals(method, route.method)
                && Objects.equals(path, route.path)
                && Objects.equals(handler, route.handler)
                && Objects.equals(pathElements, route.pathElements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, handler, pathElements);
    }

    @Override
    public String toString() {
        return "Route{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
