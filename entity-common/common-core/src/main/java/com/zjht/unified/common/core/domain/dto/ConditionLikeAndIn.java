package com.zjht.unified.common.core.domain.dto;

import lombok.Data;

@Data
public class ConditionLikeAndIn<T,K> {
    private T equals;
    private T like;
    private K inCondition;
}
