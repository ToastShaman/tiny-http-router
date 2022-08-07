package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.PathElement;
import com.github.toastshaman.httprouter.RouterContext;

import java.util.Map;
import java.util.Objects;

public class VariablePathElement implements PathElement {

    private final String name;

    private VariablePathElement(String name) {
        this.name = Objects.requireNonNull(name, "name not provided");
    }

    @Override
    public RouterContext matchesOrNull(String offeredElement) {
        return new MatchContext(Map.of(name, offeredElement));
    }

    public static VariablePathElement parseOrNull(String offeredElement) {
        if (offeredElement.startsWith("{") && offeredElement.endsWith("}") && !offeredElement.contains(":")) {
            var name = offeredElement.substring(1, offeredElement.length() - 1);
            return new VariablePathElement(name);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariablePathElement that = (VariablePathElement) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "VariablePathElement{" +
                "name='" + name + '\'' +
                '}';
    }
}
