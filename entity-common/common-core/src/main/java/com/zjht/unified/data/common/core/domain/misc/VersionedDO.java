package com.zjht.unified.data.common.core.domain.misc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VersionedDO<T> {
    private T data;
    private String ver;
}
