package com.github.toastshaman.httprouter;

import java.util.Map;
import java.util.Optional;

public interface RouterContext {
    String required(String name);

    Optional<String> optional(String name);

    Map<String, String> toMap();
}
