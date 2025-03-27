package com.zjht.unified.utils;

import com.zjht.unified.domain.exchange.CID;
import com.zjht.unified.domain.exchange.Cell;
import com.zjht.unified.domain.exchange.PageSpec;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class PageModelUtils {
    public static Cell newCell(PageSpec page, CID parentGuid) {
        Cell newCell = new Cell();
        CID newCID = new CID(-1, UUID.randomUUID().toString());
        newCell.setId(newCID);

        if (page != null && parentGuid != null) {
            Cell parentCell = getCell(page.getCell(), parentGuid);
            if (parentCell != null) {
                append(page, parentCell, newCell);
            }
        }

        return newCell;
    }


    public static void delCellFromPageByGuid(PageSpec page, CID guid) {
        if (page != null) {
            Cell cell = getCell(page.getCell(), guid);
            if (cell == null) {
                throw new RuntimeException("Inconsistent Data, Cell not found");
            }
            delCellFromPage(page, cell);
        }
    }

    public static void delCellFromPage(PageSpec page, Cell cell) {
        if (cell.equals(page.getCell())) {
            page.setCell(null);
        } else {
            Cell parentCell = getCell(page.getCell(), cell.getParentId());
            if (parentCell != null) {
                deleteCell(parentCell, cell);
            }
        }
    }

    public static void moveCellByGuid(PageSpec page, CID parentGuid, Cell cell) {
        if (page == null) {
            throw new RuntimeException("Page not found");
        }
        Cell parentCell = getCell(page.getCell(), parentGuid);
        if (parentCell == null) {
            throw new RuntimeException("parentCell not found");
        }
        append(page, parentCell, cell);
    }

    public static Cell switchCell(PageSpec page, Cell condidate) {
        if (page == null) return null;

        CID targetCID = condidate.getId();
        if (targetCID == null) return null;

        Cell oldCell = getCell(page.getCell(), targetCID);
        if (oldCell == null) return null;

        if (condidate.equals(oldCell)) return oldCell;

        Cell oldParentCell = null;
        CID oldParentId = oldCell.getParentId();
        if (oldParentId != null) {
            oldParentCell = getCell(page.getCell(), oldParentId);
        } else {
            oldParentCell = null;
        }

        if (oldParentCell != null) {
            int index = oldParentCell.getChildren().indexOf(oldCell);
            if (index != -1) {
                oldParentCell.getChildren().set(index, condidate);
            }
        } else {
            if (page.getCell().equals(oldCell)) {
                page.setCell(condidate);
            }
        }

        condidate.setParentId(oldParentCell != null ? oldParentCell.getId() : null);
        return oldCell;
    }

    public static void deleteCell(Cell parentCell, Cell cell) {
        int index = parentCell.getChildren().indexOf(cell);
        if (index != -1) {
            parentCell.getChildren().remove(index);
        }
    }

    public static void append(PageSpec page, Cell parentCell, Cell cell) {
        parentCell.getChildren().add(cell);
        if (cell.getParentId() != null) {
            Cell oldParent = getCell(page.getCell(), cell.getParentId());
            if (oldParent != null) {
                deleteCell(oldParent, cell);
            }
        }
        cell.setParentId(parentCell.getId());
    }


    public static PageSpec newPage() {
        CID newCID = new CID(null, UUID.randomUUID().toString());
        PageSpec newPageSpec = new PageSpec(newCID);
        return newPageSpec;
    }


    /**
     * 遍历root的子节点，如果子节点的id等于cellId，则返回该子节点，否则递归遍历子节点
     *
     * @param root
     * @param cellId
     * @return
     */
    public static Cell getCell(Cell root, CID cellId) {
        if (root == null)
            return null;

        AtomicReference<Cell> ret = new AtomicReference<>();
        root.traverse((Cell cell, Cell parent) -> {
            if (cell.getId().equals(cellId)){
                ret.set(cell);
                return true;
            }
            return false;
        });
        return ret.get();
    }


}