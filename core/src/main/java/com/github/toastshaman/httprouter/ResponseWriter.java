package com.github.toastshaman.httprouter;

public interface ResponseWriter {
    Headers header();

    void write(String content);

    void writeHeader(int statusCode);
}
