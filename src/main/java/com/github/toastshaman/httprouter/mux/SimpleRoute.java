package com.github.toastshaman.httprouter.mux;

import com.github.toastshaman.httprouter.Handler;
import com.github.toastshaman.httprouter.Route;
import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.MatchResult;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.Pattern;
import com.github.toastshaman.httprouter.pattern.PatternElements;
import com.github.toastshaman.httprouter.pattern.PatternElementsFactory;

import java.util.Objects;

import static com.github.toastshaman.httprouter.domain.MatchResult.*;

public final class SimpleRoute implements Route {
    private final MethodType method;
    private final Pattern pattern;
    private final Handler handler;
    private final PatternElements patterns;

    public SimpleRoute(MethodType method, Pattern pattern, Handler handler) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(handler);
        this.method = method;
        this.pattern = pattern;
        this.handler = handler;
        this.patterns = new PatternElementsFactory().parse(pattern);
    }

    @Override
    public MatchResult match(RoutingContext context, MethodType offeredMethod, Path offeredPath) {
        if (!offeredMethod.equals(method)) return MethodNotAllowed(this);
        return patterns.match(context, offeredPath) ? Matched(this) : NoMatch();
    }

    public SimpleRoute prefixWith(Pattern pattern) {
        return new SimpleRoute(method(), pattern.concat(pattern()), handler());
    }

    @Override
    public MethodType method() {
        return method;
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public Handler handler() {
        return handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimpleRoute) obj;
        return Objects.equals(this.method, that.method) &&
                Objects.equals(this.pattern, that.pattern) &&
                Objects.equals(this.handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, pattern, handler);
    }

    @Override
    public String toString() {
        return "SimpleRoute[" +
                "method=" + method + ", " +
                "pattern=" + pattern + ", " +
                "handler=" + handler + ']';
    }
}
