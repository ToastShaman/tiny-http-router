package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.Handler;
import com.github.toastshaman.httprouter.Route;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Pattern;

import java.util.Objects;

public record SimpleRoute(
        MethodType method,
        Pattern pattern,
        Handler handler
) implements Route {
    public SimpleRoute {
        Objects.requireNonNull(method);
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(handler);
    }

    public SimpleRoute prefixWith(Pattern pattern) {
        return new SimpleRoute(method(), pattern.concat(pattern()), handler());
    }
}
