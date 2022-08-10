package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.mux.Mux;
import com.github.toastshaman.httprouter.mux.MyRequest;
import com.github.toastshaman.httprouter.mux.MyResponseWriter;
import com.github.toastshaman.httprouter.routing.RoutingTree;
import com.github.toastshaman.httprouter.routing.SimpleRouting;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@State(Scope.Benchmark)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RoutingTreeBenchmark {

    private final Router tree = new Mux(new RoutingTree());
    private final Router simple = new Mux(new SimpleRouting());

    @Setup(Level.Trial)
    public void initialize() {
        List.of(tree, simple).forEach(it -> {
            for (int i = 0; i < 500_000; i++) {
                it.Get("/a/b/c/%d".formatted(i), (w, r) -> w.writeHeader(200));
            }
            it.Get("/a/b/c/d/f/{id:[0-9]+}", (w, r) -> w.writeHeader(200));
            it.Post("/a/b/c/d/f/{id:[0-9]+}", (w, r) -> w.writeHeader(200));
        });
    }

    @Benchmark
    @Fork(value = 2)
    @Warmup(iterations = 3, time = 10)
    public void benchmark_routing_tree_find_route(Blackhole bh) {
        tree.handle(new MyResponseWriter(), MyRequest.Get("/a/b/c/d/f/1"));
    }

    @Benchmark
    @Fork(value = 2)
    @Warmup(iterations = 3, time = 10)
    public void benchmark_simple_routing_find_route(Blackhole bh) {
        simple.handle(new MyResponseWriter(), MyRequest.Get("/a/b/c/d/f/1"));
    }
}