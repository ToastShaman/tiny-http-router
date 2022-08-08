package com.github.toastshaman.httprouter;

import java.util.Optional;

public interface RoutingContext {
    String require(String key);

    Optional<String> optional(String key);

    void set(String key, String value);
}
