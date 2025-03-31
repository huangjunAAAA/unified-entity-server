package com.zjht.unified.domain.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TemplateInfo {
    private String type;
    private String name;
    private Integer sort;
}
