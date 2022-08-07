package com.github.toastshaman.httprouter;

public interface PathElement {
    RouterContext matchesOrNull(String offeredElement);
}
