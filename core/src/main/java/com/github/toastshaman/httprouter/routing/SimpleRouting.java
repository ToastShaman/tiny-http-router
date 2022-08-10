package com.github.toastshaman.httprouter.routing;

import com.github.toastshaman.httprouter.Route;
import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.RoutingTable;
import com.github.toastshaman.httprouter.MatchResult;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.Pattern;
import com.github.toastshaman.httprouter.pattern.PatternElements;
import com.github.toastshaman.httprouter.pattern.PatternElementsFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.github.toastshaman.httprouter.MatchResult.*;

public class SimpleRouting implements RoutingTable {

    private final Function<Pattern, PatternElements> patternFactory;
    private final List<Tuple<Route, PatternElements>> routes = new ArrayList<>();

    public SimpleRouting() {
        this(PatternElementsFactory::parse);
    }

    public SimpleRouting(Function<Pattern, PatternElements> factory) {
        this.patternFactory = factory;
    }

    @Override
    public List<Route> routes() {
        return routes.stream().map(it -> it.first).toList();
    }

    @Override
    public void insert(Route route) {
        routes.add(Tuple.of(route, patternFactory.apply(route.pattern())));
    }

    @Override
    public MatchResult find(RoutingContext context, MethodType method, Path path) {
        return routes.stream().map(it -> match(it, context, method, path))
                .max(MatchResult.Comparator())
                .orElse(NoMatch());
    }

    private MatchResult match(Tuple<Route, PatternElements> record,
                              RoutingContext context,
                              MethodType offeredMethod,
                              Path offeredPath) {
        var route = record.first;
        var patterns = record.second;
        if (!offeredMethod.equals(route.method())) return MethodNotAllowed();
        return patterns.match(context, offeredPath) ? Matched(route) : NoMatch();
    }

    private record Tuple<A, B>(A first, B second) {
        private Tuple {
            Objects.requireNonNull(first);
            Objects.requireNonNull(second);
        }

        public static <A,B> Tuple<A,B> of(A first, B second) {
            return new Tuple<>(first, second);
        }
    }
}
