package com.zjht.unified.service.ctx;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrjUniqueInfo {

    private Long prjId;
    private String prjGuid;
    private String prjVer;
}
