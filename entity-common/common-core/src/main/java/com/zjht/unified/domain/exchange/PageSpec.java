package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageSpec {
    @JsonProperty("page_id")
    private CID pageId;
    @JsonProperty("cell")
    private Cell cell;

    @JsonProperty("route")
    private String route;
    @JsonProperty("source_id")
    private CID sourceId;
    @JsonProperty("ui_prj_id")
    private Long uiPrjId;


    public PageSpec() {
    }

    public PageSpec(CID pageId) {
        this.pageId = pageId;
    }
}