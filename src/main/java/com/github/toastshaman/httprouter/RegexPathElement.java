package com.github.toastshaman.httprouter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegexPathElement implements PathElement {

    private final String name;
    private final Predicate<String> pattern;

    private RegexPathElement(String name, Pattern pattern) {
        this.name = Objects.requireNonNull(name, "name not provided");
        this.pattern = Objects.requireNonNull(pattern, "pattern not provided").asMatchPredicate();
    }

    @Override
    public MatchContext matchesOrNull(String offeredElement) {
        if (pattern.test(offeredElement)) {
            return new MatchContext(Map.of(name, offeredElement));
        }
        return null;
    }

    public static RegexPathElement parseOrNull(String offeredElement) {
        if (offeredElement.startsWith("{") && offeredElement.endsWith("}") && offeredElement.contains(":")) {
            List<String> nameAndRegex = Arrays.asList(offeredElement.substring(1, offeredElement.length() - 1).split(":"));
            String name = nameAndRegex.get(0);
            Pattern regex = Pattern.compile(nameAndRegex.get(1));
            return new RegexPathElement(name, regex);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexPathElement that = (RegexPathElement) o;
        return Objects.equals(name, that.name) && Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pattern);
    }

    @Override
    public String toString() {
        return "RegexPathElement{" +
                "name='" + name + '\'' +
                ", pattern=" + pattern +
                '}';
    }
}
