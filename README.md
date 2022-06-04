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

**速速开一个命令行**

```java
Flowlight.console().jline()
            // 注入日志
            .logger(Logger.getLogger("Flowlight"))
            // 注入线程池
            .executor(Executors.newSingleThreadExecutor())
            // 自定义异常处理
            .exception(Throwable::printStackTrace)
            // 命令前缀
            .prompt("Flowlight> ")
            // 未找到指令时的默认处理
            .notfound(s -> String.format("command %s not found", s))
            // 匹配指令
            .command("stop", s -> System.exit(0))
            // 以当前输出流进入命令行模式
            .console();
```

**开一个任务调度器**

```java
ScheduleTask schedule = Flowlight.task().schedule()
            .min(100)
            .start();

// 提交一个延迟 10 秒 每 10 秒执行一次的任务
schedule.task()
        .run(() -> System.out.println("Hello World!"))
        .delay(10)
        .period(10)
        .register();
```

**同时也有辅助开发的工具**

```java
// 扫描指定包中的类 支持 idea 中 ant 直接运行的扫描
List<String> files = Flowlight.resource()
            .classfile()
            // 指定主类
            .clazz(Flowlight.class)
            // 指定包
            .artifact("io.hanbings.flowlight")
            // 自定义异常处理
            .exception(Throwable::printStackTrace)
            .paths();
        
// 随机 UUID
RandomUtils.uuid();
// 随机字符串
RandomUtils.strings(64);
        
// 日志文字变色
TextColorful.blue("hello");
// 日志文字效果 加粗 斜体 下划线
TextEffect.bold("hello");
TextEffect.negative("hello");
TextEffect.underline("hello");
// 混着用
TextEffect.bold(TextColorful.blue("hello"));
```

## ⚡️ 快速开始

添加仓库以及依赖

```groovy
maven {
    url "https://repository.hanbings.com/snapshots"
}
```

```groovy
implementation "io.hanbings:flowlight:1.0-SNAPSHOT"
```

## ⚖ 开源许可

本项目使用 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) 许可协议进行开源。

本项目是**非盈利性**项目。

## 🍀 关于开源

开源是一种精神。

开源运动所坚持的原则：

1. 坚持开放与共享，鼓励最大化的参与与协作。
2. 尊重作者权益，保证软件程序完整的同时，鼓励修改的自由以及衍生创新。
3. 保持独立性和中立性。

与来自五湖四海的开发者共同**讨论**技术问题，**解决**技术难题，**促进**应用的发展是开源的本质目的。

**众人拾柴火焰高，开源需要依靠大家的努力，请自觉遵守开源协议，弘扬开源精神，共建开源社区！**
