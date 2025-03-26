package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Size {
    @JsonProperty("width")
    private String width;
    @JsonProperty("height")
    private String height;

    public Size() {
    }

    public Size(String width, String height) {
        this.width = width;
        this.height = height;
    }
}