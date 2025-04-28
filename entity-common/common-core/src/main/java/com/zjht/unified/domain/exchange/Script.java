package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Script {
    @JsonProperty("type")
    private String type;
    @JsonProperty("content")
    private String content;

    @JsonProperty("engine")
    private String engine;
    @JsonProperty("method")
    private String method;


}