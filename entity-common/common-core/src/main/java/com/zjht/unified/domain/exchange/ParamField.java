package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ParamField {
    @JsonProperty("name")
    private String name;
    @JsonProperty("desc")
    private String desc;
    @JsonProperty("type")
    private String type;

    public ParamField() {
    }

    public ParamField(String name, String desc, String type) {
        this.name = name;
        this.desc = desc;
        this.type = type;
    }
}