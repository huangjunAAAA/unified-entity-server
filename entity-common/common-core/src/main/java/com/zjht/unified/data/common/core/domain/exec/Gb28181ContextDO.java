package com.zjht.unified.data.common.core.domain.exec;

import lombok.Data;

@Data
public class Gb28181ContextDO {
    private int enable;
    private Long planId;
    private Long colpId;
    private Long deviceId;
    private String sipDomain;
    private String ver;
}
