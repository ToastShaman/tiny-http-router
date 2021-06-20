package com.github.toastshaman.httprouter;

public interface Handler<REQUEST, RESPONSE> {
    RESPONSE handle(REQUEST request);
}
