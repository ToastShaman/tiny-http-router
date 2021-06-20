package com.github.toastshaman.httprouter;

import java.util.Optional;

public interface RouterContext {
    String require(String name);
    Optional<String> optional(String name);
}
