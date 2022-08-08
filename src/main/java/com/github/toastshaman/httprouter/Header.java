package com.github.toastshaman.httprouter;

public interface Header {

    String get(String key);

    void set(String key, String value);

    void remove(String key);
}
