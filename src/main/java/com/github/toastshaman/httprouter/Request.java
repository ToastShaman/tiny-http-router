package com.github.toastshaman.httprouter;

import java.util.Map;

public interface Request {
    String method();

    String path();

    String body();

    Map<String, String> queryStringParameters();

    RoutingContext context();
}
