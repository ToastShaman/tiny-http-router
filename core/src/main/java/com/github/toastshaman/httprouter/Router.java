package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.mux.Mux;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Router extends Handler {

    static Router newRouter() {
        return new Mux();
    }

    Router Use(Function<Handler, Handler> middleware);

    Router Connect(String pattern, Handler handler);

    Router Delete(String pattern, Handler handler);

    Router Get(String pattern, Handler handler);

    Router Head(String pattern, Handler handler);

    Router Options(String pattern, Handler handler);

    Router Patch(String pattern, Handler handler);

    Router Post(String pattern, Handler handler);

    Router Put(String pattern, Handler handler);

    Router Trace(String pattern, Handler handler);

    Router Route(String pattern, Consumer<Router> router);

    Router NotFound(Handler handler);

    Router MethodNotAllowed(Handler handler);
}
