package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Method;

import java.util.Objects;

import static com.github.toastshaman.httprouter.domain.Method.GET;
import static com.github.toastshaman.httprouter.domain.Method.POST;

public record MyRequest(Method method, String path) {

    public MyRequest {
        Objects.requireNonNull(method);
        Objects.requireNonNull(path);
    }

    public static MyRequest GET(String path) {
        return new MyRequest(GET, path);
    }

    public static MyRequest POST(String path) {
        return new MyRequest(POST, path);
    }
}
