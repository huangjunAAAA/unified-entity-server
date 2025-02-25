package com.zjht.unified.domain.composite;

import com.zjht.unified.domain.simple.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PrjSpecDO implements Serializable {
    private static final long serialVersionUID = 2349875437264738L;
    private List<ClazzDefCompositeDO> clazzList;
    private List<FsmDefCompositeDO> fsmList;
    private List<SentinelDefDO> sentinelDefList;
    private List<StaticDefDO> staticDefList;
    private List<ViewDefDO> viewDefList;
    private List<PrjSpecDO> depPkgList;
    private UiPrjDO uiPrj;
    private UePrjDO uePrj;
    private List<InitialInstanceDO> instanceList;
}
