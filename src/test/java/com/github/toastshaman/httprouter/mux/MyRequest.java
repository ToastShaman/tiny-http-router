package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.Request;
import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.MapRoutingContext;
import com.github.toastshaman.httprouter.domain.MethodType;

import java.util.Map;

record MyRequest(
        String method,
        String path,
        String body,
        RoutingContext context
) implements Request {

    public static MyRequest Any(MethodType method, String path) {
        return new MyRequest(method.name(), path, null, new MapRoutingContext());
    }

    public static MyRequest Get(String path) {
        return new MyRequest("GET", path, null, new MapRoutingContext());
    }

    public static MyRequest Post(String path, String body) {
        return new MyRequest("GET", path, body, new MapRoutingContext());
    }

    @Override
    public Map<String, String> getQueryStringParameters() {
        return Map.of();
    }
}
