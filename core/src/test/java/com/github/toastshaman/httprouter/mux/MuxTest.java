package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;
import java.util.function.Function;

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
                    s.Get("/world", (w, r) -> {
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
                .NotFound((w, r) -> w.writeHeader(418))
                .Get("/a", (w, r) -> w.writeHeader(200));

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/hello/world"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
    }

    @Test
    void can_override_method_not_allowed_handler() {
        var router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .MethodNotAllowed((w, r) -> w.writeHeader(418))
                .Get("/a", (w, r) -> w.writeHeader(200))
                .Post("/a/b/c", (w, r) -> w.writeHeader(201))
                ;

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Post("/a", ""));

        assertThat(responseWriter.statusCode).isEqualTo(418);
    }

    @Test
    void picks_first_matching_route() {
        var router = Router.newRouter()
                .Get("/a", (w, r) -> w.writeHeader(418))
                .Get("/a", (w, r) -> w.writeHeader(200));

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/a"));

        assertThat(responseWriter.statusCode).isEqualTo(418);
    }

    @Test
    void can_use_method_to_create_handler() {
        var router = Router.newRouter()
                .Get("/hello", createHandler("Hello"));

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/hello"));

        assertThat(responseWriter.statusCode).isEqualTo(200);
        assertThat(responseWriter.body).isEqualTo("""
                {"message":"Hello"}""");
    }

    @SuppressWarnings("SameParameterValue")
    private Handler createHandler(String message) {
        return (w, r) -> {
            w.header().set("Content-Type", "application/json");
            w.write(new Gson().toJson(Map.of("message", message)));
            w.writeHeader(200);
        };
    }

    @Test
    void middleware_can_abort_early() {
        var router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .Use(next -> (w, r) -> {
                    if (r.method().equals("GET")) {
                        w.writeHeader(500);
                        return;
                    }
                    next.handle(w, r);
                })
                .Get("/a", (w, r) -> w.writeHeader(200));

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/a"));

        assertThat(responseWriter.statusCode).isEqualTo(500);
        assertThat(responseWriter.headers.toMap())
                .containsEntry("Content-Type", "application/json");
    }

    @Test
    void middleware_can_handle_exceptions() {
        Function<Handler, Handler> errorMiddleware = next -> (w, r) -> {
            try {
                next.handle(w, r);
            } catch (Exception e) {
                w.write(new Gson().toJson(Map.of("error", e.getMessage())));
                w.writeHeader(500);
            }
        };

        var router = Router.newRouter()
                .Use(errorMiddleware)
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "application/json");
                    next.handle(w, r);
                })
                .Get("/a", (w, r) -> {
                    throw new IllegalArgumentException("Something went wrong");
                });

        var responseWriter = new MyResponseWriter();
        router.handle(responseWriter, MyRequest.Get("/a"));

        assertThat(responseWriter.statusCode).isEqualTo(500);
        assertThat(responseWriter.headers.toMap())
                .containsEntry("Content-Type", "application/json");
        assertThat(responseWriter.body).isEqualTo("""
                        {"error":"Something went wrong"}""");
    }
}