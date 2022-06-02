/*
 * Copyright 2022 Alkaid
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

package io.hanbings.flowlight.mongodb;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

@Setter
@Getter
@SuppressWarnings("unused")
@Accessors(fluent = true, chain = true)
public class SyncMongodbConnector {
    String host = "127.0.0.1";
    int port = 27017;
    String database;
    String username;
    String password;
    Gson gson = new Gson();
    MongoClientOptions options = null;

    // 托管 Client 的实例
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    MongoClient client = null;

    public SyncMongodbConnector connect() {
        client = Optional.ofNullable(client).orElseGet(() -> {
            if (username != null && password != null) {
                MongoCredential credential =
                        MongoCredential.createCredential(username, database, password.toCharArray());
                return new MongoClient(new ServerAddress(host, port),
                        credential,
                        Optional.ofNullable(options)
                                .orElse(MongoClientOptions
                                        .builder()
                                        .build()
                                )
                );
            } else {
                return new MongoClient(new ServerAddress(host, port),
                        Optional.ofNullable(options)
                                .orElse(MongoClientOptions.builder().build())
                );
            }
        });
        return this;
    }

    public SyncMongodbConnection connection() {
        return new SyncMongodbConnection(gson, client.getDatabase(database));
    }

    public void disconnect() {
        client.close();
    }
}
