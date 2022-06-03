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

package io.hanbings.flowlight.console;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
@SuppressWarnings("unused")
public class JlineConsole {
    Logger logger = Logger.getLogger(JlineConsole.class.getName());
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Consumer<Exception> exception = Throwable::printStackTrace;
    String prompt = "console> ";
    @SuppressWarnings("SpellCheckingInspection")
    Function<String, String> notfound = s -> String.format("command %s not found.", s);

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    Map<String, Consumer<String[]>> commands = new HashMap<>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    Terminal terminal;

    @SuppressWarnings("InfiniteLoopStatement")
    public JlineConsole console() {
        executor.execute(() -> {
            try {
                LineReader reader = LineReaderBuilder.builder()
                        .terminal(TerminalBuilder.builder()
                                .system(true)
                                .jna(true)
                                .color(true)
                                .build())
                        .build();

                while (true) {
                    String line = reader.readLine(prompt);
                    // 解析异常输入
                    String[] command = line.split(" ").length == 0
                            ? new String[]{line}
                            : line.split(" ");
                    if (commands.containsKey(command[0])) {
                        commands.get(line).accept(command);
                    } else {
                        logger.info(notfound.apply(command[0]));
                    }
                }

            } catch (IOException | UserInterruptException | EndOfFileException x) {
                exception.accept(x);
            }
        });
        return this;
    }

    public void shutdown() {
        terminal.pause();
    }

    public JlineConsole command(String command, Consumer<String[]> handler) {
        commands.put(command, handler);
        return this;
    }
}
