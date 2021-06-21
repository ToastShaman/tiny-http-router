package com.github.toastshaman.httprouter;

import java.util.List;

public interface Routable<REQUEST, RESPONSE> extends RouteBuilder<REQUEST, RESPONSE>, Handler<REQUEST, RESPONSE> {
    List<Route<REQUEST, RESPONSE>> getRoutes();
    RoutingHandler<REQUEST, RESPONSE> getFallbackHandler();
    ExceptionHandler<REQUEST, RESPONSE> getExceptionHandler();
}
