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
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
@Setter
@Getter
@SuppressWarnings("unused")
@Accessors(fluent = true, chain = true)
public class ClassFile {
    String artifact;
    Class<?> clazz;

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
                while (urlEnumeration.hasMoreElements()) {
                    URL url = urlEnumeration.nextElement();
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = connection.getJarFile();
                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                    while (jarEntryEnumeration.hasMoreElements()) {
                        JarEntry entry = jarEntryEnumeration.nextElement();
                        String jarEntryName = entry.getName();
                        if (jarEntryName.contains(".class")
                                && jarEntryName.replaceAll("/", ".").startsWith(artifact)) {
                            collect.add(jarEntryName.substring(0,
                                    jarEntryName.lastIndexOf(".")).replace("/", "."));
                        }
                    }
                }
            } catch (IOException exception) {
                log.error("unknown io error.", exception);
            }
        } else {
            getFiles(Objects.requireNonNull(clazz.getResource("/")).getPath()
                    + artifact.replace(".", "/"))
                    .stream()
                    .filter(f -> f.getName().endsWith(".class"))
                    .forEach(f -> collect.add(
                            f.getPath()
                                    .replace(Objects.requireNonNull(clazz.getResource("/"))
                                            .getPath()
                                            .replace("/", "\\")
                                            .replaceFirst("\\\\", ""), "")
                                    .replace("\\", ".")
                                    .replace(".class", "")
                    ));
        }
        return collect;
    }

    public static List<File> getFiles(String path) {
        List<File> files = new ArrayList<>();
        addFile(new File(path), files);
        return files;
    }

    private static void addFile(File file, List<File> files) {
        if (file.isFile()) {
            files.add(file);
        } else {
            Arrays.stream(Objects.requireNonNull(file.listFiles()))
                    .filter(Objects::nonNull)
                    .forEach(f -> addFile(f, files));
        }
    }
}
