package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.PathElement;
import com.github.toastshaman.httprouter.domain.RoutingPatternElement;

import java.util.Objects;
import java.util.regex.Pattern;

public record NamedPattern(String name) implements MatchingPatternElement {

    public NamedPattern {
        if (Objects.requireNonNull(name).isBlank()) {
            throw new IllegalArgumentException("must not be empty");
        }
    }

    @Override
    public boolean match(RoutingContext context, PathElement path) {
        context.set(name, path.value());
        return true;
    }

    public static final Pattern PATTERN = Pattern.compile("^\\{([a-zA-Z0-9]+)}$");

    public static NamedPattern parseOrNull(RoutingPatternElement element) {
        var matcher = PATTERN.matcher(element.value());
        if (matcher.find()) {
            var name = matcher.group(1);
            return new NamedPattern(name);
        }
        return null;
    }
}
