package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Point3D {
    @JsonProperty("x")
    private int x;
    @JsonProperty("y")
    private int y;
    @JsonProperty("z")
    private int z;

    public Point3D() {
    }

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}