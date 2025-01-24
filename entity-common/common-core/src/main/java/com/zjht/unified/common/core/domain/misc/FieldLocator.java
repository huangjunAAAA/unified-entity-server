package com.zjht.unified.common.core.domain.misc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldLocator {
    private String field;
    private String type;
    private Integer dimension;
    private String index;
    private String filter;
}


