package com.zjht.unified.service.ctx;

import org.apache.poi.ss.formula.functions.T;

public interface PrjContextProvider {
    public <T> T getPrjectContext();
    public void setPrjectContext(Object pCtx);
}
