package com.zjht.unified.jsengine.v8.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TsBlock {
    private List<String> declVars=new ArrayList<>();
    private String body;
    private List<String> deps=new ArrayList<>();
}
