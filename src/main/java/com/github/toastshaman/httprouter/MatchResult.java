package com.github.toastshaman.httprouter;

import java.util.Objects;

public record MatchResult<REQUEST, RESPONSE>(
        Route<REQUEST, RESPONSE> route,
        MatchContext context
) {

    public MatchResult {
        Objects.requireNonNull(route, "node not provided");
        Objects.requireNonNull(context, "context not provided)");
    }
}
