package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Targeted {
    @JsonProperty("varname")
    private String varname;
    @JsonProperty("type")
    private String type;

    public Targeted() {
    }

    public Targeted(String varname, String type) {
        this.varname = varname;
        this.type = type;
    }
}