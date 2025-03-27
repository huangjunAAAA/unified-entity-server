package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Script {
    @JsonProperty("type")
    private String type;
    @JsonProperty("content")
    private String content;

    public Script() {
    }

    public Script(String type, String content) {
        this.type = type;
        this.content = content;
    }
}