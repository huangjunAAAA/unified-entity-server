package com.zjht.unified.common.core.domain.ddl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipeDef {
    private String name;
    private String tbl;
    private String brokerList;
    private String topic;
    private String groupId;
}
