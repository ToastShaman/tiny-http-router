package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.toastshaman.httprouter.domain.MethodType.*;

public class Mux implements Router {

    private final List<Route> routes = new ArrayList<>();

    private List<Function<Handler, Handler>> middlewares = new ArrayList<>();

    public Mux() {
    }

    private Mux(List<Function<Handler, Handler>> middlewares) {
        this.middlewares = middlewares;
    }

    private Handler notFoundHandler = (w, r) -> {
        w.write("Not Found");
        w.writeHeader(404);
    };

    @Override
    public Router Use(Function<Handler, Handler> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public Router Connect(String pattern, Handler handler) {
        insertRoute(CONNECT, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Delete(String pattern, Handler handler) {
        insertRoute(DELETE, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Get(String pattern, Handler handler) {
        insertRoute(GET, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Head(String pattern, Handler handler) {
        insertRoute(HEAD, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Options(String pattern, Handler handler) {
        insertRoute(OPTIONS, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Patch(String pattern, Handler handler) {
        insertRoute(PATCH, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Post(String pattern, Handler handler) {
        insertRoute(POST, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Put(String pattern, Handler handler) {
        insertRoute(PUT, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Trace(String pattern, Handler handler) {
        insertRoute(TRACE, Pattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Route(String pattern, Consumer<Router> routerFn) {
        Pattern p = Pattern.of(pattern);

        Mux mux = new Mux(middlewares);
        routerFn.accept(mux);

        List<Route> routesWithPrefix = mux.routes.stream()
                .map(it -> it.prefixWith(p))
                .toList();

        routes.addAll(routesWithPrefix);

        return this;
    }

    @Override
    public Router NotFound(Handler handler) {
        this.notFoundHandler = handler;
        return this;
    }

    @Override
    public void handle(ResponseWriter responseWriter, Request request) {
        var method = MethodType.from(request);
        var path = Path.from(request);
        var ctx = request.context();

        var endpoint = findRoute(ctx, method, path)
                .map(Route::handler)
                .orElse(notFoundHandler);

        endpoint.handle(responseWriter, request);
    }

    private void insertRoute(MethodType method, Pattern pattern, Handler handler) {
        routes.add(new SimpleRoute(method, pattern, chain(handler)));
    }

    // builds a Handler composed of a middleware stack and endpoint handler in the order they are passed.
    private Handler chain(Handler endpoint) {
        if (middlewares.isEmpty()) {
            return endpoint;
        }

        var handler = middlewares.get(middlewares.size() - 1).apply(endpoint);
        for (int i = middlewares.size() - 2; i >= 0; i--) {
            handler = middlewares.get(i).apply(handler);
        }

        return handler;
    }

    private Optional<Route> findRoute(RoutingContext context, MethodType methodType, Path path) {
        return routes.stream().filter(it -> it.match(context, methodType, path)).findFirst();
    }
}
