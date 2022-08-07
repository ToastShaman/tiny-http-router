package com.github.toastshaman.httprouter.domain;

import com.github.toastshaman.httprouter.PathElement;
import com.github.toastshaman.httprouter.RouterContext;

import java.util.Objects;

public class StaticPathElement implements PathElement {

    private final String element;

    public StaticPathElement(String element) {
        this.element = Objects.requireNonNull(element, "element not provided");
    }

    @Override
    public RouterContext matchesOrNull(String offeredElement) {
        if (offeredElement.equals(element)) {
            return new MatchContext();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticPathElement that = (StaticPathElement) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public String toString() {
        return "StaticPathElement{" +
                "element='" + element + '\'' +
                '}';
    }
}
