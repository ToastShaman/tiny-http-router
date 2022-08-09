package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.Headers;
import com.github.toastshaman.httprouter.ResponseWriter;
import com.github.toastshaman.httprouter.domain.MapHeaders;

class MyResponseWriter implements ResponseWriter {
    public final MapHeaders headers = new MapHeaders();
    public int statusCode = -1;

    public String body = "";

    @Override
    public Headers header() {
        return headers;
    }

    @Override
    public void write(String content) {
        this.body = content;
    }

    @Override
    public void writeHeader(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "MyResponseWriter{" +
                "headers=" + headers +
                ", statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}
