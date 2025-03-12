package com.zjht.unified.common.core.domain.component;

import com.zjht.unified.common.core.domain.FieldLocator;
import lombok.Data;

import java.util.List;

@Data
public class SimpleField extends ParamField{
    private int order;

    /**
     * 标识这个字段是否适用于vue中的ref
     */
    private String ref;
    private List<FieldLocator> indexSteps;
}
