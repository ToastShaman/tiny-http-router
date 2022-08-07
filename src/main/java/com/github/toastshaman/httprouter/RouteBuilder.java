package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Method;

import java.util.function.Consumer;

public interface RouteBuilder<REQUEST, RESPONSE> {
    RouteBuilder<REQUEST, RESPONSE> GET(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> HEAD(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> POST(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> PUT(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> DELETE(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> CONNECT(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> OPTIONS(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> TRACE(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> PATCH(String path, RoutingHandler<REQUEST, RESPONSE> handler);

    RouteBuilder<REQUEST, RESPONSE> add(String prefix, Consumer<RouteBuilder<REQUEST, RESPONSE>> routerFn);

    RouteBuilder<REQUEST, RESPONSE> add(Method method, String path, RoutingHandler<REQUEST, RESPONSE> handler);

}
