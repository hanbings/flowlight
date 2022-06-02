package io.hanbings.flowlight;

import io.hanbings.flowlight.mongodb.FlowlightMongodb;
import io.hanbings.flowlight.redis.FlowlightRedis;
import io.hanbings.flowlight.server.FlowlightServer;
import io.hanbings.flowlight.task.FlowlightTask;

@SuppressWarnings("unused")
public class Flowlight {
    public static FlowlightServer server() {
        return new FlowlightServer();
    }

    public static FlowlightTask task() {
        return new FlowlightTask();
    }

    public FlowlightMongodb mongodb() {
        return new FlowlightMongodb();
    }

    public FlowlightRedis redis() {
        return new FlowlightRedis();
    }
}
