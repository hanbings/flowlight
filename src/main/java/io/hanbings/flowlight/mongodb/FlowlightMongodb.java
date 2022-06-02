package io.hanbings.flowlight.mongodb;

public class FlowlightMongodb {
    public SyncMongodbConnector sync() {
        return new SyncMongodbConnector();
    }

    public AsyncMongodbConnector async() {
        return new AsyncMongodbConnector();
    }
}
