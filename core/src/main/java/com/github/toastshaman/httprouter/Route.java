package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.MatchResult;
import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Path;
import com.github.toastshaman.httprouter.domain.Pattern;

public interface Route {

    MethodType method();

    Pattern pattern();

    Handler handler();

    MatchResult match(RoutingContext Context, MethodType offeredMethod, Path offeredPath);

    Route prefixWith(Pattern pattern);
}
