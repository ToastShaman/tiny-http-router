package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.RoutingPattern;

public interface Route {

    MethodType method();

    RoutingPattern pattern();

    Handler handler();

    Route prefixWith(RoutingPattern pattern);
}
