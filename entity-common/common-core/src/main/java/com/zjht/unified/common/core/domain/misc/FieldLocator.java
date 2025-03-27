package com.zjht.unified.common.core.domain.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FieldLocator {
    @JsonProperty("field")
    private String field;
    @JsonProperty("type")
    private String type;
    @JsonProperty("dimension")
    private Integer dimension;
    @JsonProperty("index")
    private String index;
    @JsonProperty("filter")
    private String filter;

    public FieldLocator() {}

    public FieldLocator(String field, String type, Integer dimension, String index, String filter) {
        this.field = field;
        this.type = type;
        this.dimension = dimension;
        this.index = index;
        this.filter = filter;
    }
}