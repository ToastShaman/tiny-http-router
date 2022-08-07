package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.Method;
import com.github.toastshaman.httprouter.domain.Path;

public interface RouterFactory<REQUEST, RESPONSE> {

    Route<REQUEST, RESPONSE> create(
            Method method,
            Path path,
            RoutingHandler<REQUEST, RESPONSE> handler);
}
