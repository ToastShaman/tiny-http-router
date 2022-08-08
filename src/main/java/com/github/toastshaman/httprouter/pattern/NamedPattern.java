package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.PathElement;

import java.util.Objects;
import java.util.regex.Pattern;

public record NamedPattern(String name) implements PatternElement {

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

    public static NamedPattern parseOrNull(String element) {
        var matcher = Pattern.compile("\\{([a-zA-Z0-9]+)}").matcher(element);
        if (matcher.find()) {
            var name = matcher.group(0);
            return new NamedPattern(name);
        }
        return null;
    }
}
