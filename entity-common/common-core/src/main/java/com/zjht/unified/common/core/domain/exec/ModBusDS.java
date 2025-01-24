package com.zjht.unified.common.core.domain.exec;

import lombok.Data;

@Data
public class ModBusDS {
    private String uri;
    private Integer max;
    private Integer maxIdle;
}
