package com.github.toastshaman.httprouter.servlet;

import com.github.toastshaman.httprouter.Headers;
import com.github.toastshaman.httprouter.ResponseWriter;
import com.github.toastshaman.httprouter.domain.MapHeaders;
import jakarta.servlet.http.HttpServletResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpServletResponseWriter implements ResponseWriter {

    public final HttpServletResponse response;
    public final MapHeaders headers = new MapHeaders();
    private String body;
    private int statusCode;

    public HttpServletResponseWriter(HttpServletResponse response) {
        this.response = response;
    }

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

    public void done() {
        try {
            headers.toMap().forEach(response::setHeader);
            response.setStatus(statusCode);
            response.setCharacterEncoding(UTF_8.name());
            response.getWriter().write(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
