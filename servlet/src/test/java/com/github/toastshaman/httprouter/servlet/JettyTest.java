package com.github.toastshaman.httprouter.servlet;

import com.github.toastshaman.httprouter.Router;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class JettyTest {

    @Test
    void can_route_in_jetty() throws IOException, InterruptedException {
        var server = new EmbeddedServer().start();

        try {
            var port = server.port();
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:%d/hello/kevin?first=foo&second=bar".formatted(port)))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            var response = client.send(request, BodyHandlers.ofString());

            assertThat(response.body()).isEqualTo("Hello kevin");
            assertThat(response.statusCode()).isEqualTo(418);
            assertThat(response.headers().map())
                    .containsEntry("Content-Type", List.of("text/plain;charset=utf-8"))
                    .containsEntry("first", List.of("foo"))
                    .containsEntry("second", List.of("bar"));
        } finally {
            server.stop();
        }
    }

    public static class EmbeddedServer {
        private final Server server;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        private final Router router = Router.newRouter()
                .Use(next -> (w, r) -> {
                    w.header().set("Content-Type", "text/plain");
                    next.handle(w, r);
                })
                .Get("/hello/{name}", (w, r) -> {
                    w.write("Hello %s".formatted(r.context().require("name")));
                    w.header().set("first", r.queryStringParameters().get("first"));
                    w.header().set("second", r.queryStringParameters().get("second"));
                    w.writeHeader(418);
                });

        public EmbeddedServer() {
            this.server = new Server(0);
            this.server.addConnector(new ServerConnector(server));
            this.server.setHandler(new AbstractHandler() {
                @Override
                public void handle(String target,
                                   Request baseRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

                    var responseWriter = new HttpServletResponseWriter(response);
                    router.handle(responseWriter, new HttpServletRequestWrapper(request));
                    responseWriter.done();
                    baseRequest.setHandled(true);
                }
            });
        }

        public EmbeddedServer start() {
            try {
                server.start();
                return this;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Integer port() {
            var future = new CompletableFuture<Integer>();
            executor.scheduleWithFixedDelay(() -> {
                if (server.isStarted()) {
                    future.complete(server.getURI().getPort());
                    executor.shutdown();
                }
            }, 0, 500, MILLISECONDS);
            return future.join();
        }

        public void stop() {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
