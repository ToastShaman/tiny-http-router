package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.domain.PatternElement;
import com.github.toastshaman.httprouter.domain.RoutingPattern;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class PatternElementsFactory {

    private static final List<Function<PatternElement, MatchingPatternElement>> factories = List.of(
            StaticPattern::parseOrNull,
            NamedPattern::parseOrNull,
            RegexPattern::parseOrNull
    );

    private PatternElementsFactory() {
    }

    public static PatternElements parse(RoutingPattern pattern) {
        var elements = pattern
                .split()
                .stream()
                .flatMap(element -> factories.stream().map(it -> it.apply(element)).filter(Objects::nonNull))
                .toList();
        return new PatternElements(elements);
    }
}
