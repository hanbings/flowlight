package io.hanbings.flowlight.message;

import io.hanbings.flowlight.function.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class Mail {
    List<String> to = new ArrayList<>();
    String from;
    String subject;
    String content;
    List<String> file = new ArrayList<>();
    List<Pair<String, String>> resource = new ArrayList<>();
    boolean html = true;
    boolean multipart = false;

    public Mail to(String to) {
        this.to.add(to);
        return this;
    }

    public Mail file(String file) {
        this.file.add(file);
        return this;
    }
}
