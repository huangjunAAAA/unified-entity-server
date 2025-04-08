package com.zjht.unified.domain.exchange;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomizeOptions {
    private List<SimpleField> bindings=new ArrayList<>();
    private String reactiveScript;
}
