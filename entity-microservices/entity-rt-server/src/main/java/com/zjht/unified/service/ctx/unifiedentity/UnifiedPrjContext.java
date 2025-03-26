package com.zjht.unified.service.ctx.unifiedentity;

import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.service.ctx.PrjContextProvider;

public class UnifiedPrjContext implements PrjContextProvider {

    private PrjSpecDO prjCtx;

    @Override
    public <T> T getPrjectContext() {
        return (T) prjCtx;
    }

    @Override
    public void setPrjectContext(Object pCtx) {
        prjCtx=prjCtx;
    }
}
