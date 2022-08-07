package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Method;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.SimpleRouterFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.toastshaman.httprouter.domain.MatchContext.empty;
import static com.github.toastshaman.httprouter.domain.Method.*;

public class Router<REQUEST, RESPONSE> implements Routable<REQUEST, RESPONSE> {
    private final LinkedList<Route<REQUEST, RESPONSE>> routes = new LinkedList<>();

    private final Function<REQUEST, String> methodFn;
    private final Function<REQUEST, String> pathFn;
    private final RouterFactory<REQUEST, RESPONSE> routerFactory;

    private RoutingHandler<REQUEST, RESPONSE> fallbackHandler = null;
    private ExceptionHandler<REQUEST, RESPONSE> exceptionHandler = null;

    public Router(Function<REQUEST, String> methodFn,
                  Function<REQUEST, String> pathFn) {
        this(methodFn, pathFn, new SimpleRouterFactory<>());
    }

    public Router(Function<REQUEST, String> methodFn,
                  Function<REQUEST, String> pathFn,
                  RouterFactory<REQUEST, RESPONSE> routerFactory) {
        this.methodFn = Objects.requireNonNull(methodFn, "no mapping function provided for method");
        this.pathFn = Objects.requireNonNull(pathFn, "no mapping function provided for path");
        this.routerFactory = Objects.requireNonNull(routerFactory, "no router factory provided");
    }

    @Override
    public Router<REQUEST, RESPONSE> GET(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(GET, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> HEAD(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(HEAD, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> POST(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(POST, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> PUT(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(PUT, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> DELETE(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(DELETE, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> CONNECT(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(CONNECT, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> OPTIONS(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(OPTIONS, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> TRACE(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(TRACE, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> PATCH(String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(PATCH, path, handler);
    }

    @Override
    public Router<REQUEST, RESPONSE> add(Method method, String path, RoutingHandler<REQUEST, RESPONSE> handler) {
        return add(routerFactory.create(method, Path.of(path), handler));
    }

    @Override
    public Router<REQUEST, RESPONSE> add(String prefix, Consumer<RouteBuilder<REQUEST, RESPONSE>> routerFn) {
        var subRouter = new Router<REQUEST, RESPONSE>(methodFn, pathFn);
        routerFn.accept(subRouter);

        var routes = subRouter.getRoutes();
        if (routes.isEmpty()) {
            throw new IllegalArgumentException("no routes added for prefix: %s".formatted(prefix));
        }

        routes.forEach(r -> add(routerFactory.create(r.method(), r.path().prefix(prefix), r.handler())));

        return this;
    }

    @Override
    public Router<REQUEST, RESPONSE> notFound(RoutingHandler<REQUEST, RESPONSE> handler) {
        fallbackHandler = handler;
        return this;
    }

    @Override
    public Router<REQUEST, RESPONSE> exceptionally(ExceptionHandler<REQUEST, RESPONSE> handler) {
        exceptionHandler = handler;
        return this;
    }

    @Override
    public List<Route<REQUEST, RESPONSE>> getRoutes() {
        return List.copyOf(routes);
    }

    @Override
    public RoutingHandler<REQUEST, RESPONSE> getFallbackHandler() {
        return fallbackHandler;
    }

    @Override
    public ExceptionHandler<REQUEST, RESPONSE> getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public RESPONSE handle(REQUEST request) {
        Objects.requireNonNull(request, "request not provided");

        var method = methodFn.apply(request);
        var path = pathFn.apply(request);

        return routes.stream()
                .map(it -> it.matches(method, path))
                .flatMap(Optional::stream)
                .findFirst()
                .map(it -> {
                    try {
                        return it.route().handler().handle(request, it.context());
                    } catch (Exception e) {
                        return Optional.ofNullable(exceptionHandler).map(h -> h.handle(request, e)).orElse(null);
                    }
                }).or(() -> {
                    try {
                        return Optional.ofNullable(fallbackHandler).map(h -> h.handle(request, empty()));
                    } catch (Exception e) {
                        return Optional.ofNullable(exceptionHandler).map(h -> h.handle(request, e));
                    }
                }).orElseThrow(() -> new IllegalStateException("Failed to route request: %s".formatted(request)));
    }

    private Router<REQUEST, RESPONSE> add(Route<REQUEST, RESPONSE> route) {
        routes.addLast(route);
        return this;
    }
}