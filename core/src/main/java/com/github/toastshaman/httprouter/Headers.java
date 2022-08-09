package com.github.toastshaman.httprouter;

public interface Headers {

    String get(String key);

    void set(String key, String value);

    void remove(String key);
}
