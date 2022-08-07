package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.Route;
import com.github.toastshaman.httprouter.RouterContext;

import java.util.Objects;

public record MatchResult<REQUEST, RESPONSE>(
        Route<REQUEST, RESPONSE> route,
        RouterContext context
) {

    public MatchResult {
        Objects.requireNonNull(route, "node not provided");
        Objects.requireNonNull(context, "context not provided)");
    }
}
