package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjht.unified.common.core.domain.misc.FieldLocator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SimpleField extends ParamField {
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("ref")
    private String ref;
    @JsonProperty("indexSteps")
    private List<FieldLocator> indexSteps=new ArrayList<>();

    public SimpleField() {
    }

    public SimpleField(String name, String desc, String type, Integer order, String ref, List<FieldLocator> indexSteps) {
        super(name, desc, type);
        this.order = order;
        this.ref = ref;
        this.indexSteps = indexSteps;
    }
}