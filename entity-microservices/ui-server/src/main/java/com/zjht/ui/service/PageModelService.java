package com.zjht.ui.service;

import com.zjht.ui.entity.UiPage;
import com.zjht.unified.domain.exchange.CID;
import com.zjht.unified.domain.exchange.Cell;
import com.zjht.unified.domain.exchange.PageSpec;
import com.zjht.unified.utils.PageModelUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Cell> queryCell(Cell cell) {

        return null;
    }

    public PageSpec getPage(CID pageId) {
        return null;
    }

    public PageSpec getPage(Long id, String guid) {
        return null;
    }

    public PageSpec savePage(PageSpec page) {
        return null;
    }

    public PageSpec convertToPageSpec(UiPage uiPage) {
        return null;
    }

    public UiPage convertToUiPage(PageSpec pageSpec) {
        return null;
    }
}
