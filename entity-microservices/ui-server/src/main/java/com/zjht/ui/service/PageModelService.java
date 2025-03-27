package com.zjht.ui.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zjht.ui.dto.UiComponentCompositeDTO;
import com.zjht.ui.dto.UiEventHandleCompositeDTO;
import com.zjht.ui.dto.UiPageCompositeDTO;
import com.zjht.ui.entity.UiComponent;
import com.zjht.ui.entity.UiPage;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.domain.exchange.*;
import com.zjht.unified.utils.JsonUtilUnderline;
import com.zjht.unified.utils.PageModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PageModelService {

    @Autowired
    private IUiPageService uiPageService;

    @Autowired
    private IUiPageCompositeService uiPageCompositeService;

    @Autowired
    private IUiComponentService uiComponentService;

    @Autowired
    private IUiComponentCompositeService uiComponentCompositeService;

    @Autowired
    private IUiEventHandleService uiEventHandleService;

    @Autowired
    private IUiEventHandleCompositeService uiEventHandleCompositeService;

    public void delCell(CID cid,boolean withChildren) {
        if(cid!=null)
            delCell(cid.getId(), cid.getGuid(),withChildren);
    }

    /**
     * 通过id或者guid找到根节点UiComponent，递归删除对应的父子关联的UiComponent(withChildren=true)
     * @param id
     * @param guid
     */
    public void delCell(Long id, String guid, boolean withChildren){
        if(id==null){
            UiComponent cc = uiComponentService.getOne(new LambdaQueryWrapper<UiComponent>().eq(UiComponent::getGuid, guid));
            if(cc!=null){
                id=cc.getId();
            }
        }
        if(id==null)
            return;
        if(withChildren){
            List<UiComponent> cList = uiComponentService.list(new LambdaQueryWrapper<UiComponent>().eq(UiComponent::getParentId, id));
            for(UiComponent c:cList){
                delCell(c.getId(), c.getGuid(),true);
            }
        }
        uiComponentService.removeById(id);
    }

    public void moveCell(Cell parentCell, Cell cell) {
        PageSpec page = getPageByCellId(parentCell.getId());
        if (page != null) {
            PageModelUtils.append(page, parentCell, cell);
            return;
        }
        throw new RuntimeException("parentCell Page not found");
    }

    /**
     * 通过cellId找到根节点UiComponent,递归查找子节点，并转为Cell对象
     * @param cellId
     * @return
     */
    public Cell getCellByGuid(CID cellId) {
        if(cellId.getId()==null){
            UiComponent cc = uiComponentService.getOne(new LambdaQueryWrapper<UiComponent>().eq(UiComponent::getGuid, cellId.getGuid()));
            if(cc!=null){
                UiComponentCompositeDTO uiComponentCompositeDTO = uiComponentCompositeService.selectById(cc.getId());
                return convertUiComponentToCellWithChildren(uiComponentCompositeDTO);
            }
        }else{
            UiComponentCompositeDTO uiComponentCompositeDTO = uiComponentCompositeService.selectById(cellId.getId());
            return convertUiComponentToCellWithChildren(uiComponentCompositeDTO);
        }
        return null;
    }

    /**
     * 通过cellId找到UiComponent，然后通过pageId找到UiPageCompositeDTO，转换为PageSpec返回
     * @param cellId
     * @return
     */
    public PageSpec getPageByCellId(CID cellId) {
        if(cellId==null)
            return null;
        if(cellId.getId()==null){
            UiComponent cc = uiComponentService.getOne(new LambdaQueryWrapper<UiComponent>().eq(UiComponent::getGuid, cellId.getGuid()));
            if(cc!=null)
                return getPage(cc.getPageId(),cc.getGuid());
        }else{
            UiComponent cc = uiComponentService.getById(cellId.getId());
            if(cc!=null)
                return getPage(cc.getPageId(),cc.getGuid());
        }
        return null;
    }

    /**
     * 将条件转为UiComponent，查出所有符合的UiComponent，并转为Cell列表返回
     * @param cell
     * @return
     */
    public List<Cell> queryCell(Cell cell,boolean withChildren) {
        UiComponentCompositeDTO cc = convertCellToUiComponent(cell);
        List<UiComponent> lst = uiComponentService.list(Wrappers.query(cc));
        List<Cell> cellLst = lst.stream()
                .map(c -> withChildren? convertUiComponentToCellWithChildren(uiComponentCompositeService.selectById(c.getId())):convertUiComponentToCell(uiComponentCompositeService.selectById(c.getId())))
                .collect(Collectors.toList());
        cellLst.forEach(c->{

        });
        return cellLst;
    }

    public PageSpec getPage(CID pageId) {
        if(pageId!=null)
            return getPage(pageId.getId(),pageId.getGuid());
        return null;
    }

    public PageSpec getPage(Long id, String guid) {
        if(id==null){
            UiPage page = uiPageService.getOne(new LambdaQueryWrapper<UiPage>().eq(UiPage::getGuid, guid));
            if(page!=null)
                id=page.getId();
        }
        if(id!=null){
            UiPageCompositeDTO pageDTO = uiPageCompositeService.selectById(id);
            return convertToPageSpec(pageDTO);
        }
        return null;
    }

    public PageSpec savePage(PageSpec page) {
        UiPageCompositeDTO uiPage = convertToUiPage(page);
        uiPageCompositeService.submit(uiPage);
        return convertToPageSpec(uiPage);
    }

    public PageSpec convertToPageSpec(UiPageCompositeDTO uiPage) {
        if (uiPage == null) {
            return null;
        }

        PageSpec pageSpec = new PageSpec();
        pageSpec.setPageId(new CID(uiPage.getId(), uiPage.getGuid()));
        pageSpec.setRoute(uiPage.getRoute());
        pageSpec.setRprjId(uiPage.getRprjId());

        // Convert UiComponent list to a tree structure and set the root node to PageSpec's cell
        if (uiPage.getPageIdUiComponentList() != null) {
            // First, build a map of components by their ID for quick lookup
            Map<Long, Cell> componentMap = new HashMap<>();
            for (UiComponentCompositeDTO uiComponent : uiPage.getPageIdUiComponentList()) {
                Cell cell = convertUiComponentToCell(uiComponent);
                componentMap.put(uiComponent.getId(), cell);
            }

            componentMap.values().stream().forEach(cell -> {
                if (cell.getParentId() != null) {
                    Cell parentCell = componentMap.get(cell.getParentId().getId());
                    if (parentCell != null) {
                        cell.getParentId().setGuid(parentCell.getId().getGuid());
                    }
                }
            });

            // Then, build the tree structure by setting parent-child relationships
            Cell rootCell = null;
            for (UiComponentCompositeDTO uiComponent : uiPage.getPageIdUiComponentList()) {
                Cell cell = componentMap.get(uiComponent.getId());
                if (uiComponent.getParentId() == null) {
                    // This is the root component
                    rootCell = cell;
                } else {
                    // Find the parent and add this cell as a child
                    Cell parentCell = componentMap.get(uiComponent.getParentId());
                    if (parentCell != null) {
                        parentCell.getChildren().add(cell);
                    }
                }
            }

            // Set the root cell to PageSpec's cell
            pageSpec.setCell(rootCell);
        }

        return pageSpec;
    }

    public UiPageCompositeDTO convertToUiPage(PageSpec pageSpec) {
        if (pageSpec == null) {
            return null;
        }

        UiPageCompositeDTO uiPage = new UiPageCompositeDTO();
        uiPage.setId(pageSpec.getPageId().getId());
        uiPage.setGuid(pageSpec.getPageId().getGuid());
        uiPage.setRoute(pageSpec.getRoute());
        uiPage.setRprjId(pageSpec.getRprjId());

        // Convert Cell to UiComponent list
        if (pageSpec.getCell() != null) {
            List<UiComponentCompositeDTO> uiComponents = convertCellTreeToUiComponentList(pageSpec.getCell());
            uiPage.setPageIdUiComponentList(uiComponents);
        }

        return uiPage;
    }

    private Cell convertUiComponentToCellWithChildren(UiComponentCompositeDTO uiComponent) {
        if(uiComponent==null)
            return null;
        UiComponentCompositeDTO cc = uiComponentCompositeService.selectById(uiComponent.getId());
        Cell root=convertUiComponentToCell(cc);
        List<UiComponent> cList = uiComponentService.list(new LambdaQueryWrapper<UiComponent>().eq(UiComponent::getParentId, uiComponent.getId()));

        cList.forEach(c->{
            UiComponentCompositeDTO subc = uiComponentCompositeService.selectById(c.getId());
            Cell subroot=convertUiComponentToCellWithChildren(subc);
            root.getChildren().add(subroot);
        });
        return root;
    }

    private Cell convertUiComponentToCell(UiComponentCompositeDTO uiComponent) {
        Cell cell = new Cell();
        cell.setId(new CID(uiComponent.getId(), uiComponent.getGuid()));
        cell.setLayout(uiComponent.getLayoutData());
        cell.setCss(uiComponent.getCssData());
        cell.setZIndex(uiComponent.getZIndex());
        cell.setSort(uiComponent.getSort());
        cell.setDynamicContainer(uiComponent.getDynamicContainer());
        cell.setComponentRef(uiComponent.getComponentRef());
        cell.setRender(JsonUtilUnderline.parse(uiComponent.getRenderData(),Render.class));
        if(uiComponent.getParentId()!=null){
            cell.setParentId(new CID(uiComponent.getParentId(), null));
        }

        // Convert UiEventHandle to Cell.event and Cell.contextmenu
        if (uiComponent.getComponentIdUiEventHandleList() != null) {
            for (UiEventHandleCompositeDTO uiEventHandle : uiComponent.getComponentIdUiEventHandleList()) {
                if (Constants.EVENT_TYPE_REGULAR.equals(uiEventHandle.getEventType())) {
                    Script script = new Script(uiEventHandle.getType(), uiEventHandle.getContent());
                    List<Script> value = new ArrayList<>();
                    value.add(script);
                    cell.getEvent().put(uiEventHandle.getEventCode(), value);
                } else if (Constants.EVENT_TYPE_CONTEXT.equals(uiEventHandle.getEventType())) {
                    List<Script> scripts = new ArrayList<>();
                    scripts.add(new Script(uiEventHandle.getType(), uiEventHandle.getContent()));
                    Contextmenu contextmenu = new Contextmenu(uiEventHandle.getEventCode(),
                        JsonUtilUnderline.parseArray(uiEventHandle.getTargetData(), Targeted.class),
                            scripts);
                    cell.getContextmenu().add(contextmenu);
                }
            }
        }

        return cell;
    }

    private List<UiComponentCompositeDTO> convertCellTreeToUiComponentList(Cell rootCell) {
        List<UiComponentCompositeDTO> uiComponents = new ArrayList<>();
        if (rootCell != null) {
            // Use a queue to perform a breadth-first traversal of the tree
            Queue<Cell> queue = new LinkedList<>();
            queue.add(rootCell);

            while (!queue.isEmpty()) {
                Cell cell = queue.poll();
                UiComponentCompositeDTO uiComponent = convertCellToUiComponent(cell);
                uiComponents.add(uiComponent);

                // Add children to the queue for processing
                if (cell.getChildren() != null) {
                    queue.addAll(cell.getChildren());
                }
            }
        }
        return uiComponents;
    }

    private UiComponentCompositeDTO convertCellToUiComponent(Cell cell) {
        UiComponentCompositeDTO uiComponent = new UiComponentCompositeDTO();
        uiComponent.setId(cell.getId().getId());
        uiComponent.setGuid(cell.getId().getGuid());
        uiComponent.setLayoutData(cell.getLayout());
        uiComponent.setCssData(cell.getCss());
        uiComponent.setZIndex(cell.getZIndex());
        uiComponent.setSort(cell.getSort());
        uiComponent.setDynamicContainer(cell.getDynamicContainer());
        uiComponent.setComponentRef(cell.getComponentRef());
        uiComponent.setRenderData(JsonUtilUnderline.toJson(cell.getRender()));

        // Convert Cell.event and Cell.contextmenu to UiEventHandle
        if (cell.getEvent() != null) {
            for (Map.Entry<String, List<Script>> entry : cell.getEvent().entrySet()) {
                for (Script script : entry.getValue()) {
                    UiEventHandleCompositeDTO uiEventHandle = new UiEventHandleCompositeDTO();
                    uiEventHandle.setEventCode(entry.getKey());
                    uiEventHandle.setEventType(Constants.EVENT_TYPE_REGULAR);
                    uiEventHandle.setType(script.getType());
                    uiEventHandle.setContent(script.getContent());
                    uiComponent.getComponentIdUiEventHandleList().add(uiEventHandle);
                }
            }
        }

        if (cell.getContextmenu() != null) {
            for (Contextmenu contextmenu : cell.getContextmenu()) {
                UiEventHandleCompositeDTO uiEventHandle = new UiEventHandleCompositeDTO();
                uiEventHandle.setEventCode(contextmenu.getName());
                uiEventHandle.setEventType(Constants.EVENT_TYPE_CONTEXT);
                uiEventHandle.setTargetData(JsonUtilUnderline.toJson(contextmenu.getTarget()));
                uiEventHandle.setType(contextmenu.getScript().get(0).getType());
                uiEventHandle.setContent(contextmenu.getScript().get(0).getContent());
                uiComponent.getComponentIdUiEventHandleList().add(uiEventHandle);
            }
        }

        return uiComponent;
    }
}


