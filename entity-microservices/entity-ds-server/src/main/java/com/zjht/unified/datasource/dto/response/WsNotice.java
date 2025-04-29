package com.zjht.unified.datasource.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WsNotice {
    private String type;
    private String msg;
}
