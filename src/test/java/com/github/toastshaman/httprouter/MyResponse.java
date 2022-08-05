package com.github.toastshaman.httprouter;

import java.util.Objects;

public record MyResponse(Integer code) {

    public MyResponse {
        Objects.requireNonNull(code);
    }

    public static MyResponse OK() {
        return new MyResponse(200);
    }

    public static MyResponse NOT_FOUND() {
        return new MyResponse(404);
    }

    public static MyResponse INTERNAL_SERVER_ERROR() {
        return new MyResponse(500);
    }
}
