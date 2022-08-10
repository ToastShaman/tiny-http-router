package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.Request;
import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.MapRoutingContext;
import com.github.toastshaman.httprouter.domain.MethodType;

import java.util.Map;

public record MyRequest(
        String method,
        String path,
        String body,
        Map<String, String> headers,
        RoutingContext context
) implements Request {

    public static MyRequest Any(MethodType method, String path) {
        return new MyRequest(method.name(), path, null, Map.of(), new MapRoutingContext());
    }

    public static MyRequest Get(String path) {
        return new MyRequest("GET", path, null, Map.of(), new MapRoutingContext());
    }

    public static MyRequest Post(String path, String body) {
        return new MyRequest("POST", path, body, Map.of(), new MapRoutingContext());
    }

    @Override
    public Map<String, String> queryStringParameters() {
        return Map.of();
    }
}
