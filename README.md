<h1 align="center">⚡️ Flowlight</h1>

## 🍀 这是什么？

**在快捷的编码与稳健的架构间寻求平衡点？**

流光对 Javalin Mongodb Redis 等组件进行了封装，提供了对于快速建立 Restapi 服务的一整个轻量级应用框架！

**这是一个对登录与注册进行简单响应的服务**

```java
import io.hanbings.flowlight.Flowlight;
import io.hanbings.flowlight.mongodb.SyncMongodbConnector;
import io.hanbings.flowlight.server.JavalinServer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static void main(String... args) {

        // 用户数据实体
        record Data(String username, String password) { }

        // 获取 mongodb 客户端
        SyncMongodbConnector mongo = Flowlight.mongodb().sync()
                .host("127.0.0.1")
                .port(27017)
                .database("flowlight")
                .username("root")
                .password("root")
                .connect();

        // 创建 javalin web 服务器
        JavalinServer server = Flowlight.server().javalin()
                .host("127.0.0.1")
                .port(10086)
                .ssl(true)
                .cert("flowlight.jks")
                .password("flowlight")
                .server();

        // 添加路由
        server.post("/register", ctx -> {
                    mongo.connection().create(
                            "user",
                            new Data(
                                    ctx.queryParam("password"),
                                    ctx.queryParam("username")
                            )
                    );
                    ctx.result("success");
                })
                // 过滤掉参数不完整的请求
                .filter(ctx -> ctx.queryParamMap().containsKey("username"))
                .filter(ctx -> ctx.queryParamMap().containsKey("password"));

        server.get("/login", ctx -> {
                    List<Data> data = mongo.connection().read(
                            "user",
                            Map.of(
                                    "username",
                                    Objects.requireNonNull(ctx.queryParam("username"))
                            ),
                            Data.class
                    );
                    ctx.result(
                            data.get(0).password().equals(ctx.queryParam("password"))
                            ? "success"
                            : "fail"
                    );
                })
                // 过滤掉参数不完整的请求
                .filter(ctx -> ctx.queryParamMap().containsKey("username"))
                .filter(ctx -> ctx.queryParamMap().containsKey("password"));
    }
}
```

