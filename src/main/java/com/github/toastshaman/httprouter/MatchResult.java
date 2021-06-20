package com.github.toastshaman.httprouter;

import java.util.Objects;

public class MatchResult<REQUEST, RESPONSE> {

    public final Route<REQUEST, RESPONSE> node;
    public final MatchContext context;

    public MatchResult(Route<REQUEST, RESPONSE> node, MatchContext context) {
        this.node = Objects.requireNonNull(node, "node not provided");
        this.context = Objects.requireNonNull(context, "context not provided)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchResult<?, ?> that = (MatchResult<?, ?>) o;
        return Objects.equals(node, that.node) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, context);
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "node=" + node +
                ", context=" + context +
                '}';
    }
}
