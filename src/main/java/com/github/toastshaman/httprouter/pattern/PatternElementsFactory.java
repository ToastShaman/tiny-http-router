package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.domain.Pattern;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PatternElementsFactory {

    private final List<Function<String, PatternElement>> factories = List.of(
            StaticPattern::parseOrNull,
            NamedPattern::parseOrNull,
            RegexPattern::parseOrNull
    );

    public PatternElements parse(Pattern pattern) {
        List<PatternElement> elements = pattern
                .explode()
                .stream()
                .flatMap(element -> factories.stream().map(it -> it.apply(element)).filter(Objects::nonNull))
                .toList();
        return new PatternElements(elements);
    }
}
