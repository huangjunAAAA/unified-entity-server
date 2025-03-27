package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Contextmenu {
    @JsonProperty("name")
    private String name;
    @JsonProperty("target")
    private List<Targeted> target;
    @JsonProperty("script")
    private List<Script> script;

    public Contextmenu() {
    }

    public Contextmenu(String name, List<Targeted> target, List<Script> script) {
        this.name = name;
        this.target = target;
        this.script = script;
    }
}