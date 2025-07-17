package com.zjht.ui.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TsBlock {
    private List<String> declVars=new ArrayList<>();
    private String body;
    private List<String> deps=new ArrayList<>();
}
