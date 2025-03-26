package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageSpec {
    @JsonProperty("page_id")
    private CID pageId;
    @JsonProperty("cell")
    private Cell cell;

    public PageSpec() {
    }

    public PageSpec(CID pageId) {
        this.pageId = pageId;
    }

    public PageSpec(CID pageId, Cell cell) {
        this.pageId = pageId;
        this.cell = cell;
    }
}