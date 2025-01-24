package com.zjht.unified.data.common.core.domain.ddl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblCol {
    private String nameEn;
    private String nameZh;
    private String type;
    private String jdbcType;
    private int isPK;
    private int isTempstamp;
}
