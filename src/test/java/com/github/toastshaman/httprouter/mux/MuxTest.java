package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.domain.MethodType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MuxTest {

    @Test
    void applies_middleware() {
        var router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .Use(next -> (w, r) -> {
                    w.header().set("X-Middlewares-1", "First");
                    next.handle(w, r);
                })
                .Use(next -> (w, r) -> {
                    w.header().set("X-Middlewares-2", "Second");
                    next.handle(w, r);
                })
                .Get("/hello", (w, r) -> {
                    w.write("Hello World");
                    w.writeHeader(200);
                });

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/hello"));

        assertThat(responseWriter.statusCode).isEqualTo(200);
        assertThat(responseWriter.body).isEqualTo("Hello World");
        assertThat(responseWriter.headers.toMap())
                .containsEntry("Content-Type", "application/json")
                .containsEntry("X-Middlewares-1", "First")
                .containsEntry("X-Middlewares-2", "Second");
    }

    @ParameterizedTest
    @EnumSource(MethodType.class)
    void routes_all_http_methods(MethodType method) {
        var router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("method", r.method());
                    next.handle(w, r);
                })
                .Get("/a", (w, r) -> w.writeHeader(200))
                .Post("/a", (w, r) -> w.writeHeader(200))
                .Patch("/a", (w, r) -> w.writeHeader(200))
                .Put("/a", (w, r) -> w.writeHeader(200))
                .Connect("/a", (w, r) -> w.writeHeader(200))
                .Delete("/a", (w, r) -> w.writeHeader(200))
                .Head("/a", (w, r) -> w.writeHeader(200))
                .Options("/a", (w, r) -> w.writeHeader(200))
                .Trace("/a", (w, r) -> w.writeHeader(200));


        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Any(method, "/a"));

        assertThat(responseWriter.headers.toMap())
                .containsEntry("method", method.name());
    }

    @Test
    void routes_static_paths() {
        var router = Router.newRouter()
                .Get("/a", (w, r) -> w.writeHeader(418));

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/a"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
    }

    @Test
    void routes_named_paths() {
        var router = Router.newRouter()
                .Get("/a/{b}", (w, r) -> {
                    w.header().set("b", r.context().require("b"));
                    w.writeHeader(418);
                });

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/a/foobar"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
        assertThat(responseWriter.headers.toMap())
                .containsEntry("b", "foobar");
    }

    @Test
    void routes_regex_paths() {
        var router = Router.newRouter()
                .Get("/a/{id:[0-9]{4}}", (w, r) -> {
                    w.header().set("id", r.context().require("id"));
                    w.writeHeader(418);
                });

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/a/1234"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
        assertThat(responseWriter.headers.toMap())
                .containsEntry("id", "1234");
    }

    @Test
    void routes_sub_routes() {
        var router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .Route("/hello", s -> {
                    s.Use(next -> (w, r) -> {
                        w.header().set("X-Routed", "true");
                        next.handle(w, r);
                    });
                    s.Get("/world", (w,r) -> {
                        w.write("Hello World");
                        w.writeHeader(418);
                    });
                })
                .Get("/hello", (w, r) -> {
                    w.writeHeader(200);
                });

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/hello/world"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
        assertThat(responseWriter.headers.toMap())
                .containsEntry("Content-Type", "application/json")
                .containsEntry("X-Routed", "true");
    }

    @Test
    void can_override_not_found_handler() {
        var router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .NotFound((w,r) -> w.writeHeader(418))
                .Get("/a", (w, r) -> w.writeHeader(200));

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/hello/world"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
    }
}