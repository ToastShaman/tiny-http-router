package com.github.toastshaman.httprouter;

import java.util.Objects;

public class MatchResult<REQUEST, RESPONSE> {

    public final Route<REQUEST, RESPONSE> route;
    public final MatchContext context;

    public MatchResult(Route<REQUEST, RESPONSE> route, MatchContext context) {
        this.route = Objects.requireNonNull(route, "node not provided");
        this.context = Objects.requireNonNull(context, "context not provided)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchResult<?, ?> that = (MatchResult<?, ?>) o;
        return Objects.equals(route, that.route) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, context);
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "route=" + route +
                ", context=" + context +
                '}';
    }
}
