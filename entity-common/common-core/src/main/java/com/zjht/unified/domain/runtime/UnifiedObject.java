package com.zjht.unified.domain.runtime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedObject implements Serializable {
    private String guid;
    private String clazzGUID;
    private Boolean persistTag;
}
