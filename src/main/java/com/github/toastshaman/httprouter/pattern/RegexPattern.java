package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.PathElement;

import java.util.Objects;
import java.util.regex.Pattern;

public record RegexPattern(String name, Pattern pattern) implements PatternElement {

    public RegexPattern {
        Objects.requireNonNull(pattern);
        if (Objects.requireNonNull(name).isBlank()) {
            throw new IllegalArgumentException("must not be empty");
        }
    }

    @Override
    public boolean match(RoutingContext context, PathElement path) {
        boolean matches = pattern.asMatchPredicate().test(path.value());
        if (matches) {
            context.set(name, path.value());
            return true;
        }
        return false;
    }

    public static RegexPattern parseOrNull(String element) {
        var matcher = Pattern.compile("\\{([a-zA-Z0-9]+):([^}].+)}").matcher(element);
        if (matcher.find()) {
            var name = matcher.group(1);
            var regex = matcher.group(2);
            return new RegexPattern(name, Pattern.compile(regex));
        }
        return null;
    }
}
