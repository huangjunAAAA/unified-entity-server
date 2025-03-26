package com.zjht.unified.service;

import com.zjht.unified.domain.exchange.CID;
import com.zjht.unified.domain.exchange.Cell;
import com.zjht.unified.domain.exchange.PageSpec;
import com.zjht.unified.utils.PageModelUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PageModelService {

    public void delCell(CID guid) {

    }

    public void moveCell(Cell parentCell, Cell cell) {
        PageSpec page = getPageByCellId(parentCell.getId());
        if (page != null) {
            PageModelUtils.append(page, parentCell, cell);
            return;
        }
        throw new RuntimeException("parentCell Page not found");
    }

    public Cell getCellByGuid(CID cellId) {

        return null;
    }

    public PageSpec getPageByCellId(CID cellId) {

        return null;
    }

    public List<Cell> queryCellAcrossPage(Cell cell) {

        return null;
    }

    public PageSpec getPage(CID pageId) {
        return null;
    }

    public PageSpec savePage(PageSpec page) {
        return null;
    }
}
