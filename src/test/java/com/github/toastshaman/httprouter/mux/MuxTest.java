package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.domain.MapHeaders;
import com.github.toastshaman.httprouter.domain.MapRoutingContext;
import org.junit.jupiter.api.Test;

class MuxTest {

    @Test
    void f() {
        Router router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .Use(next -> (w, r) -> {
                    System.out.println("Hello");
                    w.header().set("X-Middlewares", "First");
                    next.handle(w, r);
                })
                .Use(next -> (w, r) -> {
                    System.out.println("World");
                    w.header().set("X-Middlewares", "Second");
                    next.handle(w, r);
                })
                .Get("/a", (w, r) -> {
                    w.write("Hello World");
                    w.writeHeader(200);
                })
                .Get("/a/b", (w, r) -> {
                    w.write("Hello World");
                    w.writeHeader(200);
                })
                .Get("/a/b/{c}/d", (w, r) -> {
                    w.write("Hello World");
                    w.writeHeader(200);
                })
                .Get("/a/b/{id:[0-9]+}", (w, r) -> {
                    System.out.println(r.context().require("id"));
                    w.write("Hello World");
                    w.writeHeader(200);
                })
                .Route("/foo", sub -> {
                    sub.Use(next -> (w, r) -> {
                        w.header().set("X-Middlewares", "Third");
                        next.handle(w, r);
                    });
                    sub.Post("/bar", (w, r) -> {
                        w.write("Hello World");
                        w.writeHeader(201);
                    });
                });

        MyResponseWriter responseWriter = new MyResponseWriter();
        router.handle(responseWriter, new MyRequest());

        System.out.println(responseWriter);
    }

    static class MyRequest implements Request {
        private final RoutingContext context = new MapRoutingContext();

        @Override
        public String method() {
            return "POST";
        }

        @Override
        public String path() {
            return "/foo/bar";
        }

        @Override
        public RoutingContext context() {
            return context;
        }

        @Override
        public String body() {
            return null;
        }
    }

    static class MyResponseWriter implements ResponseWriter {
        public final Header headers = new MapHeaders();
        public int statusCode = -1;
        public String body = "";

        @Override
        public Header header() {
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
}