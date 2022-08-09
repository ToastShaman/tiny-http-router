package com.github.toastshaman.httprouter.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext;
import com.github.toastshaman.httprouter.Router;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.github.toastshaman.httprouter.aws.APIGatewayV2HTTPResponseWriter.Encoding.PLAIN;
import static org.assertj.core.api.Assertions.assertThat;

class APIGatewayV2HTTPEventWrapperTest {

    @Test
    void can_route_api_gw_v2_event() {
        var context = Mockito.mock(Context.class);
        var input = APIGatewayV2HTTPEvent.builder()
                .withRawPath("/a/b/c")
                .withRequestContext(RequestContext.builder()
                        .withHttp(RequestContext.Http.builder()
                                .withMethod("GET")
                                .build())
                        .build())
                .build();

        var router = Router.newRouter()
                .Get("/a/b/c", (w, r) -> {
                    w.header().set("Content-Type", "text/plain");
                    w.write("Success");
                    w.writeHeader(418);
                });

        var writer = new APIGatewayV2HTTPResponseWriter(PLAIN);
        var request = new APIGatewayV2HTTPEventWrapper(input, context);
        router.handle(writer, request);

        var response = writer.build();

        assertThat(response.getBody()).isEqualTo("Success");
        assertThat(response.getStatusCode()).isEqualTo(418);
        assertThat(response.getHeaders())
                .containsEntry("Content-Type", "text/plain");
    }
}