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

package io.hanbings.flowlight.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Setter
@Getter
@SuppressWarnings("unused")
@Accessors(fluent = true, chain = true)
public class ExecutorTask {
    int min = 1;
    int max = 1;
    long keep = 0;
    TimeUnit unit = TimeUnit.MILLISECONDS;
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    ExecutorService executor;

    public ExecutorTask start() {
        executor = new ThreadPoolExecutor(min, max, keep, unit, queue);
        return this;
    }

    public ExecutorTask execute(Runnable runnable) {
        executor.execute(runnable);
        return this;
    }

    public <T> ExecutorTask execute(Callable<T> callable, Consumer<Future<T>> result) {
        result.accept(executor.submit(callable));
        return this;
    }

    public void stop() {
        executor.shutdown();
    }
}
