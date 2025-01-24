package com.zjht.unified.data.common.core.domain.store;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 序列化格式  [TIME-]其他条件（使用-分隔）[:时间值]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TupleDate {
    private String timeslice;
    private Date actualDate;
    private List<String> conditions;
}
