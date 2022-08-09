package com.github.toastshaman.httprouter.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.github.toastshaman.httprouter.Request;
import com.github.toastshaman.httprouter.RoutingContext;

import java.util.Map;
import java.util.Objects;

public class APIGatewayV2HTTPEventWrapper implements Request {

    public final APIGatewayV2HTTPEvent event;
    public final ContextWrapper context;

    public APIGatewayV2HTTPEventWrapper(APIGatewayV2HTTPEvent event, Context context) {
        this.event = Objects.requireNonNull(event);
        this.context = new ContextWrapper(context);
    }

    @Override
    public String method() {
        return event.getRequestContext().getHttp().getMethod();
    }

    @Override
    public String path() {
        return event.getRawPath();
    }

    @Override
    public String body() {
        return event.getBody();
    }

    @Override
    public Map<String, String> queryStringParameters() {
        return Map.copyOf(event.getQueryStringParameters());
    }

    @Override
    public Map<String, String> headers() {
        return Map.copyOf(event.getHeaders());
    }

    @Override
    public RoutingContext context() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APIGatewayV2HTTPEventWrapper that = (APIGatewayV2HTTPEventWrapper) o;
        return Objects.equals(event, that.event) && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, context);
    }

    @Override
    public String toString() {
        return "APIGatewayV2HTTPEventWrapper{" +
                "event=" + event +
                ", context=" + context +
                '}';
    }
}
