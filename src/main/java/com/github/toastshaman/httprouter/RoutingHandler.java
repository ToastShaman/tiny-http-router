package com.github.toastshaman.httprouter;

public interface RoutingHandler<REQUEST, RESPONSE> {
    RESPONSE handle(REQUEST request, RouterContext context);
}
