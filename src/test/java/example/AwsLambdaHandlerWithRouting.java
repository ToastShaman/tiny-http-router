package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.github.toastshaman.httprouter.Handler;
import com.github.toastshaman.httprouter.Router;
import com.google.gson.Gson;

import java.util.Map;

class AwsLambdaHandlerWithRouting {

    private static class MyApi implements Handler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
        private final Gson gson = new Gson();

        @Override
        public APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent event) {
            return new Router<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>(
                    it -> it.getRequestContext().getHttp().getMethod(),
                    it -> it.getRequestContext().getHttp().getPath())
                    .GET("/hello/{name}", (req, ctx) -> {
                        String name = ctx.required("name");
                        return APIGatewayV2HTTPResponse.builder()
                                .withStatusCode(200)
                                .withHeaders(Map.of("Content-Type", "application/json"))
                                .withIsBase64Encoded(false)
                                .withBody(gson.toJson(Map.of("message", name)))
                                .build();
                    })
                    .add("/items", r -> {
                        r.GET("/{ID:[0-9]{4}", (req, ctx) -> {
                            int id = Integer.parseInt(ctx.required("ID"));
                            return APIGatewayV2HTTPResponse.builder()
                                    .withStatusCode(200)
                                    .withHeaders(Map.of("Content-Type", "application/json"))
                                    .withIsBase64Encoded(false)
                                    .withBody(gson.toJson(Map.of("ID", id)))
                                    .build();
                        });
                    })
                    .notFound((req, ctx) -> APIGatewayV2HTTPResponse.builder()
                            .withStatusCode(404)
                            .withHeaders(Map.of("Content-Type", "application/json"))
                            .withIsBase64Encoded(false)
                            .withBody(gson.toJson(Map.of("message", "path not found")))
                            .build())
                    .exceptionally((req, error) -> APIGatewayV2HTTPResponse.builder()
                            .withStatusCode(500)
                            .withHeaders(Map.of("Content-Type", "application/json"))
                            .withIsBase64Encoded(false)
                            .withBody(gson.toJson(Map.of("error", error.getMessage())))
                            .build())
                    .handle(event);
        }
    }

    private static class MyHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
        private final MyApi api;

        public MyHandler() {
            this(new MyApi());
        }

        public MyHandler(MyApi api) {
            this.api = api;
        }

        @Override
        public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
            return api.handle(input);
        }
    }
}