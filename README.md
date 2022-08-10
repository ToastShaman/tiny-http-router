# tiny-http-router

Tiny (no-dependencies) HTTP router library.

Example usage with AWS Lambda.

```java
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
            })
            .Post("/hello/{name}/create", (w, r) -> {
                var name = r.context().require("name");
                var body = r.body();
                w.write(gson.toJson(Map.of("message", "accepted")));
                w.writeHeader(201);
            }).Route("/world", s -> {
                s.Patch("/{id:[0-9]+}", (w, r) -> {
                    var id = r.context().require("id");
                    w.write(gson.toJson(Map.of("message", "accepted %s".formatted(id))));
                    w.writeHeader(201);
                });
            });

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        var request = new APIGatewayV2HTTPEventWrapper(input, context);
        var writer = new APIGatewayResponseWriter();
        router.handle(writer, request);
        return writer.build();
    }
}

```
