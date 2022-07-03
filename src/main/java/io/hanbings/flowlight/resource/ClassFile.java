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

@Setter
@Getter
@SuppressWarnings("unused")
@Accessors(fluent = true, chain = true)
public class ClassFile {
    String artifact;
    Class<?> clazz;
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
            } catch (IOException x) {
                exception.accept(x);
            }
        } else {
            files(Objects.requireNonNull(clazz.getResource("/")).getPath()
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
}
