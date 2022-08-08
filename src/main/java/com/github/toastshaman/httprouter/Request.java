package com.github.toastshaman.httprouter;

import java.io.Reader;

public interface Request {
    String method();

    String path();

    RoutingContext context();

    String body();
}
