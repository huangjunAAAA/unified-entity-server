package com.zjht.unified.data.common.core.domain.ddl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblDef {
    private String name;
    private List<TblCol> cols;
    private List<TblIndex> indices;
    private Map<String,String> properties;
    private String partitionCol;
    private String hashCol;
}
