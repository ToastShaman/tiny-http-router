package com.github.toastshaman.httprouter;

public interface ExceptionHandler<REQUEST, RESPONSE> {
    RESPONSE handle(REQUEST request, Throwable exception);
}
