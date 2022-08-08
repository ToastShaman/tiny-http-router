package com.github.toastshaman.httprouter.pattern;

import com.github.toastshaman.httprouter.RoutingContext;
import com.github.toastshaman.httprouter.domain.PathElement;

public interface PatternElement {
    boolean match(RoutingContext context, PathElement path);
}
