package com.github.toastshaman.httprouter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RouterTest {

    @Test
    void matches_method_regardless_of_case() {
        MyResponse response = newRouter()
                .GET("/hello", (r, ctx) -> MyResponse.OK())
                .handle(MyRequest.GET("/hello"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    @Test
    void matches_methods() {
        MyResponse response = newRouter()
                .GET("/hello", (r, ctx) -> MyResponse.INTERNAL_SERVER_ERROR())
                .POST("/hello", (r, ctx) -> MyResponse.OK())
                .handle(MyRequest.POST("/hello"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    @Test
    void no_match_because_paths_are_case_sensitive() {
        assertThatThrownBy(() -> newRouter()
                .GET("/HELLO", (r, ctx) -> MyResponse.OK())
                .handle(MyRequest.GET("/hello")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to route request: MyRequest{method=GET, path='/hello'}");
    }

    @Test
    void uses_fallback_when_no_routes_could_be_found() {
        MyResponse response = newRouter()
                .GET("/hello", (r, ctx) -> MyResponse.OK())
                .notFound((r, ctx) -> MyResponse.NOT_FOUND())
                .handle(MyRequest.GET("/does-not-exist"));

        assertThat(response).isEqualTo(MyResponse.NOT_FOUND());
    }

    @Test
    void uses_exception_handler_if_exception_is_thrown() {
        MyResponse response = newRouter()
                .GET("/hello", (r, ctx) -> {
                    throw new IllegalArgumentException("Oops");
                })
                .exceptionally((r, error) -> {
                    assertThat(error).hasMessage("Oops");
                    return MyResponse.OK();
                })
                .handle(MyRequest.GET("/hello"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    @Test
    void can_extract_variable_from_path() {
        MyResponse response = newRouter()
                .GET("/hello/{world}", (r, ctx) -> {
                    assertThat(ctx.required("world")).isEqualTo("kevin");
                    return MyResponse.OK();
                })
                .handle(MyRequest.GET("/hello/kevin"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    @Test
    void can_extract_multiple_variable_from_path() {
        MyResponse response = newRouter()
                .GET("/hello/{a}/{b}", (r, ctx) -> {
                    assertThat(ctx.required("a")).isEqualTo("first");
                    assertThat(ctx.required("b")).isEqualTo("second");
                    return MyResponse.OK();
                })
                .handle(MyRequest.GET("/hello/first/second"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    @Test
    void matches_route_based_on_regex() {
        MyResponse response = newRouter()
                .GET("/items/{id:[0-9]{6}}", (r, ctx) -> {
                    assertThat(ctx.required("id")).isEqualTo("123456");
                    return MyResponse.OK();
                })
                .handle(MyRequest.GET("/items/123456"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    @Test
    void allows_composing_routes() {
        MyResponse response = newRouter()
                .add("/items", r -> r
                        .GET("/{id:[0-9]{6}}", (req, ctx) -> {
                            assertThat(ctx.required("id")).isEqualTo("123456");
                            return MyResponse.OK();
                        })
                ).handle(MyRequest.GET("/items/123456"));

        assertThat(response).isEqualTo(MyResponse.OK());
    }

    private Router<MyRequest, MyResponse> newRouter() {
        return new Router<>(
                it -> it.method.name().toLowerCase(),
                it -> it.path
        );
    }
}