package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.MatchResult.MethodNotAllowed;
import com.github.toastshaman.httprouter.MatchResult.NoMatch;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.RoutingPattern;
import com.github.toastshaman.httprouter.routing.RoutingTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.toastshaman.httprouter.domain.MethodType.*;

public class Mux implements Router {

    private final RoutingTable routingTable;

    private List<Function<Handler, Handler>> middlewares = new ArrayList<>();

    public Mux() {
        this(new RoutingTree());
    }

    public Mux(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    private Mux(RoutingTable routingTable, List<Function<Handler, Handler>> middlewares) {
        this.routingTable = routingTable;
        this.middlewares = middlewares;
    }

    private Handler notFoundHandler = (w, r) -> {
        w.header().set("Content-Type", "text/plain");
        w.write("Not Found");
        w.writeHeader(404);
    };

    private Handler methodNotAllowedHandler = (w, r) -> {
        w.header().set("Content-Type", "text/plain");
        w.write("Method Not Allowed");
        w.writeHeader(405);
    };

    @Override
    public Router Use(Function<Handler, Handler> middleware) {
        middlewares.add(middleware);
        return this;
    }

    @Override
    public Router Connect(String pattern, Handler handler) {
        insertRoute(CONNECT, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Delete(String pattern, Handler handler) {
        insertRoute(DELETE, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Get(String pattern, Handler handler) {
        insertRoute(GET, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Head(String pattern, Handler handler) {
        insertRoute(HEAD, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Options(String pattern, Handler handler) {
        insertRoute(OPTIONS, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Patch(String pattern, Handler handler) {
        insertRoute(PATCH, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Post(String pattern, Handler handler) {
        insertRoute(POST, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Put(String pattern, Handler handler) {
        insertRoute(PUT, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Trace(String pattern, Handler handler) {
        insertRoute(TRACE, RoutingPattern.of(pattern), handler);
        return this;
    }

    @Override
    public Router Route(String pattern, Consumer<Router> routerFn) {
        var p = RoutingPattern.of(pattern);

        var mux = new Mux(routingTable, middlewares);
        routerFn.accept(mux);

        mux.routingTable.routes()
                .stream()
                .map(it -> it.prefixWith(p))
                .toList()
                .forEach(routingTable::insert);

        return this;
    }

    @Override
    public Router NotFound(Handler handler) {
        this.notFoundHandler = Objects.requireNonNull(handler);
        return this;
    }

    @Override
    public Router MethodNotAllowed(Handler handler) {
        this.methodNotAllowedHandler = Objects.requireNonNull(handler);
        return this;
    }

    @Override
    public void handle(ResponseWriter responseWriter, Request request) {
        var method = MethodType.from(request);
        var path = Path.from(request);
        var ctx = request.context();

        var matchResult = findRoute(ctx, method, path);

        Handler endpoint;
        if (matchResult instanceof NoMatch) endpoint = notFoundHandler;
        else if (matchResult instanceof MethodNotAllowed) endpoint = methodNotAllowedHandler;
        else endpoint = matchResult.route().handler();

        responseWriter.writeHeader(200);
        endpoint.handle(responseWriter, request);
    }

    private void insertRoute(MethodType method, RoutingPattern pattern, Handler handler) {
        routingTable.insert(new SimpleRoute(method, pattern, chain(handler)));
    }

    // builds a Handler composed of a middleware stack and endpoint handler in the order they are passed.
    private Handler chain(Handler endpoint) {
        if (middlewares.isEmpty()) {
            return endpoint;
        }

        var queue = new ArrayDeque<>(middlewares);
        var handler = Objects.requireNonNull(queue.pollLast()).apply(endpoint);
        while (!queue.isEmpty()) {
            handler = queue.pollLast().apply(handler);
        }

        return handler;
    }

    private MatchResult findRoute(RoutingContext context, MethodType methodType, Path path) {
        return routingTable.find(context, methodType, path);
    }
}
