package com.zjht.unified.domain.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjht.unified.utils.JsonUtilUnderline;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Data
public class Cell {
    @JsonProperty("id")
    private CID id;
    @JsonProperty("layout")
    private String layout;
    @JsonProperty("css")
    private String css;
    @JsonProperty("zIndex")
    private Integer zIndex;
    @JsonProperty("children")
    private List<Cell> children;
    @JsonProperty("dynamic_container")
    private Integer dynamicContainer;
    @JsonProperty("component_ref")
    private Long componentRef;
    @JsonProperty("render")
    private Render render;
    @JsonProperty("sort")
    private Integer sort;
    @JsonProperty("event")
    private Map<String, List<Script>> event;
    @JsonProperty("contextmenu")
    private List<Contextmenu> contextmenu;
    @JsonProperty("dataset")
    private ComponentSpec dataset;
    @JsonProperty("parent_id")
    private CID parentId;
    @JsonProperty("template")
    private TemplateInfo template;

    public Cell() {
    }

    public Cell(CID id, String layout, String css, Integer zIndex, List<Cell> children, Integer dynamicContainer, Long componentRef, Render render, Integer sort, Map<String, List<Script>> event, List<Contextmenu> contextmenu, ComponentSpec dataset, CID parentId) {
        this.id = id;
        this.layout = layout;
        this.css = css;
        this.zIndex = zIndex;
        this.children = children;
        this.dynamicContainer = dynamicContainer;
        this.componentRef = componentRef;
        this.render = render;
        this.sort = sort;
        this.event = event;
        this.contextmenu = contextmenu;
        this.dataset = dataset;
        this.parentId = parentId;
    }

    public void traverse(BiFunction<Cell, Cell,Boolean> visitor){
        traverse(visitor,this);
    }

    private boolean traverse(BiFunction<Cell, Cell,Boolean> visitor, Cell parent) {
        Boolean stop=visitor.apply(this, parent);
        if(stop)
            return true;
        for (Cell child : children) {
            stop=child.traverse(visitor, this);
            if(stop)
                return true;
        }
        return false;
    }

    public List<Cell> queryCellByCondition(Cell condition) {
        List<Cell> result = new ArrayList<>();
        traverse((cell, parent) -> {
            if (matchesCondition(cell, condition)) {
                result.add(cell);
            }
            return false;
        });
        return result;
    }

    private boolean matchesCondition(Cell cell, Cell condition) {
        if (condition.getId() != null && !condition.getId().equals(cell.getId())) return false;
        if (condition.getLayout() != null && !condition.getLayout().equals(cell.getLayout())) return false;
        if (condition.getCss() != null && !cell.getCss().contains(condition.getCss())) return false;
        if (condition.getZIndex() != cell.getZIndex()) return false;
        if (condition.getDynamicContainer() != cell.getDynamicContainer())
            return false;
        if (condition.getComponentRef() != cell.getComponentRef()) return false;
        if (condition.getRender() != null && !condition.getRender().equals(cell.getRender())) return false;
        if (condition.getSort() != cell.getSort()) return false;
        if (condition.getEvent() != null && !condition.getEvent().equals(cell.getEvent())) return false;
        if (condition.getContextmenu() != null && !condition.getContextmenu().equals(cell.getContextmenu()))
            return false;
        if (condition.getDataset() != null && !condition.getDataset().equals(cell.getDataset())) return false;
        if (condition.getParentId() != null && !condition.getParentId().equals(cell.getParentId())) return false;
        return true;
    }

    public void syncParentId() {
        traverse((cell, parent) -> {
            if (parent != null)
                cell.setParentId(parent.getId());
            return false;
        });
    }

    public Cell getCellById(CID id) {
        if (this.id != null && this.id.equals(id)) {
            return this;
        }
        for (Cell child : children) {
            Cell foundCell = child.getCellById(id);
            if (foundCell != null) {
                return foundCell;
            }
        }
        return null;
    }

    public String toJson() {
        return JsonUtilUnderline.toJson(this);
    }

    public static Cell fromJson(String json) {
        return JsonUtilUnderline.parse(json, Cell.class);
    }
}