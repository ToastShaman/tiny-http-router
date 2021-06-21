package com.github.toastshaman.httprouter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public class MatchContext implements RouterContext {
    public final Map<String, String> context;

    public MatchContext(Map<String, String> context) {
        this.context = Objects.requireNonNull(context, "context not provided");
    }

    public MatchContext() {
        this(emptyMap());
    }

    @Override
    public String required(String name) {
        return optional(name).orElseThrow(() -> new IllegalArgumentException(format("%%s not found%s", name)));
    }

    @Override
    public Optional<String> optional(String name) {
        return Optional.ofNullable(context.get(name)).filter(it -> !it.isBlank());
    }

    public static MatchContext empty() {
        return new MatchContext();
    }

    public static MatchContext of(Map<String, String> context) {
        return new MatchContext(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchContext context1 = (MatchContext) o;
        return Objects.equals(context, context1.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context);
    }

    @Override
    public String toString() {
        return "MatchContext{" +
                "context=" + context +
                '}';
    }
}
