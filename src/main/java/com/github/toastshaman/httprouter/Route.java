package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Path;

import java.util.Optional;

public interface Route<REQUEST, RESPONSE> {

    Method method();

    Path path();

    RoutingHandler<REQUEST, RESPONSE> handler();
    Optional<MatchResult<REQUEST, RESPONSE>> matches(String offeredMethod, String offeredPath);
}
