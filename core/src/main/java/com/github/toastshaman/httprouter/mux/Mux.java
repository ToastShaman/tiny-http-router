package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.*;
import com.github.toastshaman.httprouter.domain.MatchResult;
import com.github.toastshaman.httprouter.domain.MatchResult.MethodNotAllowed;
import com.github.toastshaman.httprouter.domain.MatchResult.NoMatch;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.Pattern;
import com.github.toastshaman.httprouter.routing.RoutingTree;

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

        Mux mux = new Mux(routingTable, middlewares);
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
        if (matchResult instanceof NoMatch) {
            endpoint = notFoundHandler;
        } else if (matchResult instanceof MethodNotAllowed) {
            endpoint = methodNotAllowedHandler;
        } else {
            endpoint = matchResult.route().handler();
        }

        endpoint.handle(responseWriter, request);
    }

    private void insertRoute(MethodType method, Pattern pattern, Handler handler) {
        routingTable.insert(new SimpleRoute(method, pattern, chain(handler)));
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

    private MatchResult findRoute(RoutingContext context, MethodType methodType, Path path) {
        return routingTable.find(context, methodType, path);
    }
}
