package com.github.toastshaman.httprouter;

import java.util.Objects;

import static com.github.toastshaman.httprouter.Method.*;

public class MyRequest {

    public final Method method;
    public final String path;

    public MyRequest(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    public static MyRequest GET(String path) {
        return new MyRequest(GET, path);
    }

    public static MyRequest POST(String path) {
        return new MyRequest(POST, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyRequest myRequest = (MyRequest) o;
        return method == myRequest.method && Objects.equals(path, myRequest.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }

    @Override
    public String toString() {
        return "MyRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                '}';
    }
}
