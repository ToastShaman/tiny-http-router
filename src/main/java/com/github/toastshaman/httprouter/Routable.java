package com.github.toastshaman.httprouter;

import java.util.List;
import java.util.function.Consumer;

public interface Routable<REQUEST, RESPONSE> extends RouteBuilder<REQUEST, RESPONSE>, Handler<REQUEST, RESPONSE> {
    List<Route<REQUEST, RESPONSE>> getRoutes();
}
