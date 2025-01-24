package com.zjht.unified.common.core.domain.ddl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TblIndex {
    private String indexName;
    private List<String> fields;
    private String indexType;
}
