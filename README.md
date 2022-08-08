# tiny-http-router

Tiny (no-dependencies) HTTP router library.

Example usage with AWS Lambda.

```java
package com.github.toastshaman.httprouter.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.domain.MapHeaders;
import com.github.toastshaman.httprouter.domain.MapRoutingContext;
import com.google.gson.Gson;

import java.io.Reader;
import java.io.StringReader;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MyLambda implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private final Gson gson = new Gson();

    private final Router router = Router.newRouter()
            .Use(next -> (w, r) -> {
                w.header().set("Content-Type", "application/json");
                next.handle(w, r);
            })
            .Get("/hello/{name}", (w, r) -> {
                var name = r.context().require("name");
                w.write(gson.toJson(Map.of("message", "hello %s".formatted(name))));
                w.writeHeader(200);
            });

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        var request = new APIGatewayV2HTTPEventWrapper(input, context);
        var writer = new APIGatewayResponseWriter();
        router.handle(writer, request);
        return writer.build();
    }

    public static class APIGatewayV2HTTPEventWrapper implements Request {
        public final APIGatewayV2HTTPEvent input;
        public final ContextWrapper context;

        public APIGatewayV2HTTPEventWrapper(APIGatewayV2HTTPEvent input, Context context) {
            this.input = Objects.requireNonNull(input);
            this.context = new ContextWrapper(context);
        }

        @Override
        public String method() {
            return input.getRequestContext().getHttp().getMethod();
        }

        @Override
        public String path() {
            return input.getRequestContext().getHttp().getPath();
        }

        @Override
        public RoutingContext context() {
            return context;
        }

        @Override
        public Reader reader() {
            return new StringReader(input.getBody());
        }
    }

    public static class ContextWrapper extends MapRoutingContext {
        public final Context original;

        public ContextWrapper(Context context) {
            super();
            this.original = Objects.requireNonNull(context);
        }
    }

    public static class APIGatewayResponseWriter implements ResponseWriter {

        private final MapHeaders header = new MapHeaders();

        private String body;

        private int statusCode = 0;

        @Override
        public Header header() {
            return header;
        }

        @Override
        public void write(String content) {
            this.body = content;
        }

        @Override
        public void writeHeader(int statusCode) {
            this.statusCode = statusCode;
        }

        public APIGatewayV2HTTPResponse build() {
            return APIGatewayV2HTTPResponse.builder()
                    .withHeaders(header.toMap())
                    .withBody(Base64.getEncoder().encodeToString(body.getBytes(UTF_8)))
                    .withStatusCode(statusCode)
                    .withIsBase64Encoded(true)
                    .build();
        }
    }
}

```
