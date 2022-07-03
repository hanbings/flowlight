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

package io.hanbings.flowlight.resource;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 由于 java 对打包 resource 文件夹到 jar 时处理方式特殊 <br>
 * 工具类输出的路径都带有 / 且将替换 \\ 为 / <br>
 * 如： /resources/index.html <br>
 * 需要手动指定一个基文件夹 并且对于 java 外的文件结构必须与 jar 内保持一致 <br>
 * 举例： <br>
 * - jar 外： resource/resources/plugin.yml <br>
 * # jar 内： resources/plugin.yml <br>
 * - jar 外： resource/resources/config/mongodb.yml <br>
 * # jar 内： resources/config/mongodb.yml <br>
 * <p>
 * 使用 path(String) 方法设置基文件夹 默认为 resources <br>
 * 该工具无法扫描 resource 资源目录根目录下的文件 <br>
 * 即： <br>
 * - resource/config.yml 无法扫描 <br>
 * - resource/resources/config.yml 扫描结果： /resources/config.yml <br>
 * <p>
 * 使用 paths() 方法进行扫描 返回一个 String 类型的 List <br>
 */
@Setter
@Getter
@SuppressWarnings("unused")
@Accessors(fluent = true, chain = true)
public class ResourceFile {
    String artifact;
    Class<?> clazz;
    String path = "resources";
    Consumer<Exception> exception = Throwable::printStackTrace;

    public static List<File> files(String path) {
        List<File> files = new ArrayList<>();
        file(new File(path), files);
        return files;
    }

    private static void file(File file, List<File> files) {
        if (file.isFile()) {
            files.add(file);
        } else {
            Arrays.stream(Objects.requireNonNull(file.listFiles()))
                    .filter(Objects::nonNull)
                    .forEach(f -> file(f, files));
        }
    }

    @SuppressWarnings("all")
    public List<String> paths() {
        List<String> collect = new ArrayList<>();
        // 遍历 String 带路径类名
        if (
                clazz.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
                        .endsWith(".jar")
        ) {
            try {
                Enumeration<URL> urlEnumeration = Thread.currentThread()
                        .getContextClassLoader().getResources(artifact.replace(".", "/"));
                // 遍历 jar 包中的资源 只选取 resources 目录下的文件
                while (urlEnumeration.hasMoreElements()) {
                    URL url = urlEnumeration.nextElement();
                    if (url.getProtocol().equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            if (jarEntry.getName().startsWith(path)) {
                                if (jarEntry.isDirectory()) {
                                    continue;
                                }
                                collect.add(jarEntry.getName());
                            }
                        }
                    }
                }
            } catch (IOException x) {
                exception.accept(x);
            }
        } else {
            Optional.ofNullable(
                    clazz.getResource("../" + artifact
                            .replaceAll("\\.", "../")
                            .replaceAll("[^\\.\\/]", "")
                            + "/"
                            + path)
            ).ifPresent(url -> {
                files(url.getPath().replace("classes/java/main/", "resources/main/"))
                        .forEach(f -> collect.add(
                                f.getPath().indexOf("resources/main/") == -1
                                        ? f.getPath()
                                        .substring(f.getPath().indexOf("resources\\main\\") + 15)
                                        : f.getPath()
                                        .substring(f.getPath().indexOf("resources/main/") + 15)
                        ));
            });
        }
        // 整理格式 添加 / 并替换 \\ 为 /
        return collect.stream()
                .map(s -> s = ("/" + s))
                .map(s -> s.replace("\\", "/"))
                .collect(Collectors.toList());
    }
}