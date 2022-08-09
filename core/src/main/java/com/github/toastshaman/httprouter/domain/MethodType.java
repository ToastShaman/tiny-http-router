package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.Request;

public enum MethodType {
    CONNECT,
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT,
    TRACE;

    public static MethodType from(Request request) {
        return valueOf(request.method().toUpperCase());
    }
}
