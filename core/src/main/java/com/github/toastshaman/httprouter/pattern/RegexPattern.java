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

    public static final Pattern PATTERN = Pattern.compile("^\\{([a-zA-Z0-9]+):([^}].+)}$");

    public static RegexPattern parseOrNull(String element) {
        var matcher = PATTERN.matcher(element);
        if (matcher.find()) {
            var name = matcher.group(1);
            var regex = matcher.group(2);
            return new RegexPattern(name, Pattern.compile(regex));
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexPattern that = (RegexPattern) o;
        return Objects.equals(pattern.pattern(), that.pattern.pattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern.pattern());
    }
}