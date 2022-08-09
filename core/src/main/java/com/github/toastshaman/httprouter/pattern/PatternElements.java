package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.PathElement;

import java.util.List;
import java.util.stream.IntStream;

public record PatternElements(List<PatternElement> patterns) {

    public boolean match(RoutingContext context, Path offeredPath) {
        List<PathElement> pathElements = offeredPath.explode();
        if (pathElements.size() != patterns.size()) return false;
        return IntStream.range(0, patterns.size())
                .mapToObj(idx -> patterns.get(idx).match(context, pathElements.get(idx)))
                .allMatch(it -> it);
    }
}
