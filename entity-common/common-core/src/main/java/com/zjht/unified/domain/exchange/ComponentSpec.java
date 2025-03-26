package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjht.unified.utils.JsonUtilUnderline;
import lombok.Data;

import java.util.List;

@Data
public class ComponentSpec {
    @JsonProperty("interfaces")
    private List<CustomizeOptions> interfaces;
    @JsonProperty("sample")
    private String sample;
    @JsonProperty("entries")
    private List<ExposeEntry> entries;

    public ComponentSpec() {
    }

    public ComponentSpec(List<CustomizeOptions> interfaces, String sample, List<ExposeEntry> entries) {
        this.interfaces = interfaces;
        this.sample = sample;
        this.entries = entries;
    }

    public String toJson() {
        return JsonUtilUnderline.toJson(this);
    }

    public static ComponentSpec fromJson(String json) {
        return JsonUtilUnderline.parse(json, ComponentSpec.class);
    }
}