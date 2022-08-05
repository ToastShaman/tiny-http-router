package com.github.toastshaman.httprouter;

import java.util.Objects;

import static com.github.toastshaman.httprouter.Method.GET;
import static com.github.toastshaman.httprouter.Method.POST;

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
