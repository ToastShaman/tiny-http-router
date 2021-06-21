package com.github.toastshaman.httprouter;

import java.util.List;

public interface Routable<REQUEST, RESPONSE> extends RouteBuilder<REQUEST, RESPONSE>, Handler<REQUEST, RESPONSE> {
    Routable<REQUEST, RESPONSE> notFound(RoutingHandler<REQUEST, RESPONSE> handler);

    Routable<REQUEST, RESPONSE> exceptionally(ExceptionHandler<REQUEST, RESPONSE> handler);

    List<Route<REQUEST, RESPONSE>> getRoutes();

    RoutingHandler<REQUEST, RESPONSE> getFallbackHandler();

    ExceptionHandler<REQUEST, RESPONSE> getExceptionHandler();
}
