/*
 * Copyright 2022 Flowlight
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hanbings.flowlight.server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.function.Consumer;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("SpellCheckingInspection unused")
public class JavalinServer {
    String host = "127.0.0.1";
    int port = 8080;
    boolean ssl = false;
    String cert;
    String password;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    Javalin app;

    public JavalinServer server() {
        app = Javalin.create(c ->
                        c.server(() -> {
                            Server server = new Server();
                            // 选择是否使用 https
                            @SuppressWarnings("all")
                            ServerConnector connector
                                    = ssl()
                                    ? new ServerConnector(server,
                                    new SslContextFactory.Server() {{
                                        setKeyStorePath(cert());
                                        setKeyStorePassword(password());
                                    }})
                                    : new ServerConnector(server);
                            connector.setPort(port());
                            server.setConnectors(new Connector[]{connector});
                            return server;
                        }))
                .start(port());

        return this;
    }

    public JavalinServer application(Consumer<Javalin> consumer) {
        consumer.accept(app);
        return this;
    }

    public JavalinRouter get(String path, Consumer<Context> handler) {
        return new JavalinRouter(app, HandlerType.GET, path, handler);
    }

    public JavalinRouter post(String path, Consumer<Context> handler) {
        return new JavalinRouter(app, HandlerType.POST, path, handler);
    }

    public JavalinRouter put(String path, Consumer<Context> handler) {
        return new JavalinRouter(app, HandlerType.PUT, path, handler);
    }

    public JavalinRouter delete(String path, Consumer<Context> handler) {
        return new JavalinRouter(app, HandlerType.DELETE, path, handler);
    }
}
