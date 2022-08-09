package com.github.toastshaman.httprouter.servlet;

import com.github.toastshaman.httprouter.Router;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HttpServletRequestWrapperTest {

    @Test
    void can_route_a_servlet_request() throws IOException {
        var input = mock(HttpServletRequest.class);
        when(input.getMethod()).thenReturn("GET");
        when(input.getServletPath()).thenReturn("/a/b/c");

        var out = new StringWriter();
        var response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        var router = Router.newRouter()
                .Get("/a/b/c", (w, r) -> {
                    w.header().set("Content-Type", "text/plain");
                    w.write("Success");
                    w.writeHeader(418);
                });

        var writer = new HttpServletResponseWriter(response);
        var request = new HttpServletRequestWrapper(input);
        router.handle(writer, request);

        writer.done();

        verify(response).setStatus(eq(418));
        verify(response).setHeader(eq("Content-Type"), eq("text/plain"));
        assertThat(out.toString()).isEqualTo("Success");
    }
}