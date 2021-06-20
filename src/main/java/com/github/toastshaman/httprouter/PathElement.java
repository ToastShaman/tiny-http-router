package com.github.toastshaman.httprouter;

public interface PathElement {
    MatchContext matchesOrNull(String offeredElement);
}
