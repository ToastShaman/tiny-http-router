package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;

import java.util.List;

public interface RoutingTable {

    List<Route> routes();

    void insert(Route route);

    MatchResult find(RoutingContext context, MethodType method, Path path);
}
