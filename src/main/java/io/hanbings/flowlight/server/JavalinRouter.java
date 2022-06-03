package io.hanbings.flowlight.server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class JavalinRouter {
    final Javalin app;
    final HandlerType method;
    final String path;
    final Consumer<Context> handler;
    Consumer<Context> none = ctx -> {
        ctx.result("404");
    };
    List<Predicate<Context>> filters = new ArrayList<>();

    public JavalinRouter(Javalin app, HandlerType method, String path, Consumer<Context> handler) {
        this.app = app;
        this.method = method;
        this.path = path;
        this.handler = handler;

        app.addHandler(method, path, ctx -> {
            if (filters.stream().allMatch(filter -> filter.test(ctx))) {
                handler.accept(ctx);
                return;
            }
            none.accept(ctx);
        });
    }

    public JavalinRouter filter(Predicate<Context> filter) {
        filters.add(filter);

        return this;
    }
}
