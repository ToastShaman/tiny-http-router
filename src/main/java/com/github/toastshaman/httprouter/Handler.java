package com.github.toastshaman.httprouter;

public interface Handler {
    void handle(ResponseWriter responseWriter, Request request);
}
