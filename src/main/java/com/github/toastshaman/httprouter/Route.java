package com.github.toastshaman.httprouter;

import java.util.*;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Route<REQUEST, RESPONSE> {

    public final String method;
    public final String path;
    public final RoutingHandler<REQUEST, RESPONSE> handler;
    private final List<PathElement> pathElements;

    public Route(String method, String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(format("%s path must start with /", path));
        }

        if (path.endsWith("/")) {
            throw new IllegalArgumentException(format("%s path must not end with /", path));
        }

        this.method = Objects.requireNonNull(method, "method not provided");
        this.handler = Objects.requireNonNull(handler, "handler not provided");
        this.path = Objects.requireNonNull(path, "path not provided");
        this.pathElements = PathElementFactory.parse(path);
    }

    public Optional<MatchResult<REQUEST, RESPONSE>> matches(String offeredMethod, String offeredPath) {
        List<String> elements = Arrays.stream(offeredPath.split("/"))
                .filter(it -> !it.isBlank())
                .collect(toList());

        if (!method.equals(offeredMethod)) {
            return Optional.empty();
        }

        if (pathElements.size() != elements.size()) {
            return Optional.empty();
        }

        List<MatchContext> matches = IntStream.range(0, pathElements.size())
                .mapToObj(i -> pathElements.get(i).matchesOrNull(elements.get(i)))
                .collect(toList());

        boolean allMatch = matches.stream().allMatch(Objects::nonNull);

        if (allMatch) {
            Map<String, String> combined = matches.stream()
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
                && Objects.equals(handler, route.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, handler);
    }

    @Override
    public String toString() {
        return "RouterNode{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", handler=" + handler +
                '}';
    }
}
