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

package io.hanbings.flowlight;

import io.hanbings.flowlight.console.FlowlightConsole;
import io.hanbings.flowlight.mongodb.FlowlightMongodb;
import io.hanbings.flowlight.redis.FlowlightRedis;
import io.hanbings.flowlight.resource.FlowlightResource;
import io.hanbings.flowlight.server.FlowlightServer;
import io.hanbings.flowlight.task.FlowlightTask;

@SuppressWarnings("unused")
public class Flowlight {
    public static FlowlightConsole console() {
        return new FlowlightConsole();
    }

    public static FlowlightMongodb mongodb() {
        return new FlowlightMongodb();
    }

    public static FlowlightRedis redis() {
        return new FlowlightRedis();
    }

    public static FlowlightResource resource() {
        return new FlowlightResource();
    }

    public static FlowlightServer server() {
        return new FlowlightServer();
    }

    public static FlowlightTask task() {
        return new FlowlightTask();
    }
}
