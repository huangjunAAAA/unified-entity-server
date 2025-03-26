package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Render {
    @JsonProperty("type")
    private String type;
    @JsonProperty("node_code")
    private String nodeCode;
    @JsonProperty("coordinate")
    private Point3D coordinate;
    @JsonProperty("size")
    private Size size;

    public Render() {
    }

    public Render(String type, String nodeCode, Point3D coordinate, Size size) {
        this.type = type;
        this.nodeCode = nodeCode;
        this.coordinate = coordinate;
        this.size = size;
    }
}