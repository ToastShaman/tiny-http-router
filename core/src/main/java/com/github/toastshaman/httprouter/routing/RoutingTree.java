package com.github.toastshaman.httprouter.routing;

import com.github.toastshaman.httprouter.MatchResult;
import com.github.toastshaman.httprouter.Route;
import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.RoutingTable;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.PathElement;
import com.github.toastshaman.httprouter.domain.RoutingPattern;
import com.github.toastshaman.httprouter.pattern.MatchingPatternElement;
import com.github.toastshaman.httprouter.pattern.PatternElements;
import com.github.toastshaman.httprouter.pattern.PatternElementsFactory;

import java.util.*;
import java.util.function.Function;

import static com.github.toastshaman.httprouter.MatchResult.*;

public class RoutingTree implements RoutingTable {

    private final Node root = new Node(null);
    private final Function<RoutingPattern, PatternElements> patternFactory;
    private final List<Route> allRoutes = new ArrayList<>();

    public RoutingTree() {
        this(PatternElementsFactory::parse);
    }

    public RoutingTree(Function<RoutingPattern, PatternElements> factory) {
        this.patternFactory = factory;
    }

    @Override
    public List<Route> routes() {
        return List.copyOf(allRoutes);
    }

    @Override
    public void insert(Route route) {
        allRoutes.add(route);

        var patterns = new ArrayDeque<>(patternFactory.apply(route.pattern()).patterns());
        insert(root, route, patterns);
    }

    private void insert(Node current,
                        Route route,
                        Deque<MatchingPatternElement> elements) {
        if (elements.isEmpty()) {
            current.addRoute(route);
            return;
        }
        insert(current.addChild(new Node(elements.pop())), route, elements);
    }

    @Override
    public MatchResult find(RoutingContext context,
                            MethodType method,
                            Path path) {
        var pathElements = new ArrayDeque<>(path.split());
        return find(root, context, method, pathElements);
    }

    private MatchResult find(Node current,
                             RoutingContext context,
                             MethodType methodType,
                             Deque<PathElement> pathElements) {
        if (pathElements.isEmpty()) {
            var route = current.getRouteOrNull(methodType);
            if (route == null) return MethodNotAllowed();
            return Matched(route);
        }

        var matched = current.getChildOrNull(context, pathElements.pop());
        if (matched == null) return NoMatch();
        return find(matched, context, methodType, pathElements);
    }

    private static class Node {
        public final MatchingPatternElement patternElement;
        public final Map<MethodType, Route> routes = new LinkedHashMap<>();
        public final Map<MatchingPatternElement, Node> children = new LinkedHashMap<>();
        public Node parent = null;

        public Node(MatchingPatternElement patternElement) {
            this.patternElement = patternElement;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node addChild(Node child) {
            var existing = children.get(child.patternElement);
            if (existing != null) return existing;
            child.setParent(this);
            children.put(child.patternElement, child);
            return child;
        }

        public void addRoute(Route route) {
            routes.putIfAbsent(route.method(), route);
        }

        public Route getRouteOrNull(MethodType methodType) {
            return routes.get(methodType);
        }

        public Node getChildOrNull(RoutingContext context, PathElement element) {
            for (Node node : children.values()) {
                if (node.patternElement.match(context, element)) {
                    return node;
                }
            }
            return null;
        }
    }
}
