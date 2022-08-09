package com.github.toastshaman.httprouter.aws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.github.toastshaman.httprouter.Headers;
import com.github.toastshaman.httprouter.ResponseWriter;
import com.github.toastshaman.httprouter.domain.MapHeaders;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class APIGatewayV2HTTPResponseWriter implements ResponseWriter {

    private final MapHeaders headers = new MapHeaders();
    private final Encoding encoding;
    private String body;
    private int statusCode;

    public APIGatewayV2HTTPResponseWriter() {
        this(Encoding.BASE64);
    }

    public APIGatewayV2HTTPResponseWriter(Encoding encoding) {
        this.encoding = encoding;
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

    public APIGatewayV2HTTPResponse build() {
        var builder = APIGatewayV2HTTPResponse.builder()
                .withHeaders(headers.toMap())
                .withStatusCode(statusCode);

        switch (encoding) {
            case PLAIN -> builder
                    .withBody(body)
                    .withIsBase64Encoded(false);
            case BASE64 -> builder
                    .withBody(Base64.getEncoder().encodeToString(body.getBytes(UTF_8)))
                    .withIsBase64Encoded(true);
        }

        return builder.build();
    }

    enum Encoding {
        PLAIN, BASE64
    }
}
