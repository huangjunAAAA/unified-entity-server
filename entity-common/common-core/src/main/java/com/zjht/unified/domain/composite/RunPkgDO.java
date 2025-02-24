package com.zjht.unified.domain.composite;

import com.zjht.unified.domain.simple.PrjDepDO;
import com.zjht.unified.domain.simple.SentinelDefDO;
import com.zjht.unified.domain.simple.StaticDefDO;
import com.zjht.unified.domain.simple.ViewDefDO;
import lombok.Data;

import java.util.List;

@Data
public class RunPkgDO {
    private List<ClazzDefCompositeDO> clazzList;
    private List<FsmDefCompositeDO> fsmList;
    private List<PrjDepDO> prjDepList;
    private List<SentinelDefDO> sentinelDefList;
    private List<StaticDefDO> staticDefList;
    private List<ViewDefDO> viewDefList;
}
