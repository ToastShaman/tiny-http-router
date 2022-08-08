package com.github.toastshaman.httprouter;

public interface ResponseWriter {
    Header header();

    void write(String content);

    void writeHeader(int statusCode);
}
