package com.github.toastshaman.httprouter.servlet;

import com.github.toastshaman.httprouter.Request;
import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.MapRoutingContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class HttpServletRequestWrapper implements Request {

    public final HttpServletRequest request;

    public final MapRoutingContext context;

    public HttpServletRequestWrapper(HttpServletRequest request) {
        this.request = Objects.requireNonNull(request);
        this.context = new MapRoutingContext();
    }

    @Override
    public String method() {
        return request.getMethod();
    }

    @Override
    public String path() {
        return request.getServletPath();
    }

    @Override
    public String body() {
        try {
            return request.getReader().lines().collect(joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> queryStringParameters() {
        return request.getParameterMap()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, it -> it.getValue()[0]));
    }

    @Override
    public Map<String, String> headers() {
        var headerMap = new HashMap<String, String>();
        request.getHeaderNames()
                .asIterator()
                .forEachRemaining(it -> headerMap.put(it, request.getHeader(it)));
        return headerMap;
    }

    @Override
    public RoutingContext context() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpServletRequestWrapper that = (HttpServletRequestWrapper) o;
        return Objects.equals(request, that.request) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, context);
    }

    @Override
    public String toString() {
        return "HttpServletRequestWrapper{" +
                "request=" + request +
                ", context=" + context +
                '}';
    }
}
