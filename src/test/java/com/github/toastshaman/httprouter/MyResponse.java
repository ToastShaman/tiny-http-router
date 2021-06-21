package com.github.toastshaman.httprouter;

import java.util.Objects;

public class MyResponse {
    public final Integer code;

    public MyResponse(Integer code) {
        this.code = code;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyResponse that = (MyResponse) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "MyResponse{" +
                "code=" + code +
                '}';
    }
}
