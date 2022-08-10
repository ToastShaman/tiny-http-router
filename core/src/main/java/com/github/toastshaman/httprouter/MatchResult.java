package com.github.toastshaman.httprouter;

import java.util.Comparator;

import static com.github.toastshaman.httprouter.MatchResult.*;

public sealed interface MatchResult permits Matched, MethodNotAllowed, NoMatch {

    Integer ordinal();

    Route route();

    static Matched Matched(Route route) {
        return new Matched(route, 100);
    }

    static MethodNotAllowed MethodNotAllowed() {
        return new MethodNotAllowed(null, 0);
    }

    static NoMatch NoMatch() {
        return new NoMatch(null, -100);
    }

    static MatchResultComparator Comparator() {
        return new MatchResultComparator();
    }

    record Matched(
            Route route,
            Integer ordinal
    ) implements MatchResult {
    }

    record NoMatch(
            Route route,
            Integer ordinal
    ) implements MatchResult {
    }

    record MethodNotAllowed(
            Route route,
            Integer ordinal
    ) implements MatchResult {
    }

    class MatchResultComparator implements Comparator<MatchResult> {
        @Override
        public int compare(MatchResult o1, MatchResult o2) {
            return o1.ordinal().compareTo(o2.ordinal());
        }
    }
}
