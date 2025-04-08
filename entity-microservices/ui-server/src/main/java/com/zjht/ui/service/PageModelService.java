package com.zjht.ui.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zjht.ui.dto.FilesetCompositeDTO;
import com.zjht.ui.dto.UiComponentCompositeDTO;
import com.zjht.ui.dto.UiEventHandleCompositeDTO;
import com.zjht.ui.dto.UiPageCompositeDTO;
import com.zjht.ui.entity.*;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.util.ScriptUtils;
import com.zjht.unified.domain.exchange.*;
import com.zjht.unified.utils.JsonUtilUnderline;
import com.zjht.unified.utils.PageModelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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

    @Autowired
    private IFilesetService filesetService;

    @Autowired
    private IUiPrjService uiPrjService;

    @Autowired
    private IUiLayoutService uiLayoutService;

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
        uiComponentCompositeService.removeById(id);
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
        if(cellId==null)
            return null;
        if(cellId.getId()==null){
            if(cellId.getGuid()==null)
                return null;
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
                .filter(c->c!=null).collect(Collectors.toList());
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
        Map<String, UiComponentCompositeDTO> cMap = uiPage.getPageIdUiComponentList().stream().collect(Collectors.toMap(UiComponent::getGuid, v -> v));
        if(page.getCell()!=null) {
            UiComponentCompositeDTO rootCC = cMap.get(page.getCell().getId().getGuid());
            rootCC.setParentType(Constants.CC_BELONG_TO_PAGE);
            uiPage.setRootComId(rootCC.getId());
            // 根据page的cell本身的层级结构设置UiComponent的parentId
            page.getCell().traverse((cell, parentCell) -> {
                if (parentCell != null) {
                    UiComponentCompositeDTO cc = cMap.get(cell.getId().getGuid());
                    UiComponentCompositeDTO ccParent = cMap.get(parentCell.getId().getGuid());
                    cc.setParentId(ccParent.getId());
                }
                return false;
            });
            uiPageService.updateById(uiPage);
            uiPage.getPageIdUiComponentList().forEach(cc -> {
                if(cc.getParentId()!=null && !Objects.equals(cc.getId(),rootCC.getId())){
                    cc.setParentType(Constants.CC_BELONG_TO_CELL);
                }
                uiComponentCompositeService.submit(cc);
            });
        }
        return convertToPageSpec(uiPage);
    }

    private static class CIDMap<T>{
        private HashMap<Long, T> idMap = new HashMap<>();
        private HashMap<String, T> guidMap = new HashMap<>();
        public T get(CID id){
            if(id.getId()!=null)
                return idMap.get(id.getId());
            if(id.getGuid()!=null)
                return guidMap.get(id.getGuid());
            return null;
        }

        public T put(CID id, T t){
            if(id.getId()!=null)
                return idMap.put(id.getId(), t);
            if(id.getGuid()!=null)
                return guidMap.put(id.getGuid(), t);
            return null;
        }

        public T remove(CID id){
            if(id.getId()!=null)
                return idMap.remove(id.getId());
            if(id.getGuid()!=null)
                return guidMap.remove(id.getGuid());
            return null;
        }
    }

    public PageSpec convertToPageSpec(UiPageCompositeDTO uiPage) {
        if (uiPage == null) {
            return null;
        }

        PageSpec pageSpec = new PageSpec();
        pageSpec.setPageId(new CID(uiPage.getId(), uiPage.getGuid()));
        pageSpec.setRoute(uiPage.getRoute());
        pageSpec.setUiPrjId(uiPage.getRprjId());
        pageSpec.setCanvasRawData(uiPage.getCanvasData());

        if(CollectionUtils.isNotEmpty(uiPage.getBelongtoIdFilesetList())){
            FilesetCompositeDTO sf = uiPage.getBelongtoIdFilesetList().get(0);

            Map<String, String> vueParts = ScriptUtils.parseVueFile(sf.getContent());

            // 加入template部分
            StringBuilder pf=new StringBuilder();
            pf.append(vueParts.get("templateTag")).append("\n");
            pf.append(vueParts.get("template")).append("\n").append("</template>").append("\n");
            pageSpec.setTemplateTag(pf.toString());

            // 加入style部分
            StringBuilder pf2=new StringBuilder();
            pf2.append(vueParts.get("styleTag")).append("\n");
            pf2.append(vueParts.get("style")).append("\n").append("</style>");
            pageSpec.setStyleTag(pf2.toString());
        }

        // Convert UiComponent list to a tree structure and set the root node to PageSpec's cell
        if (uiPage.getPageIdUiComponentList() != null) {
            // First, build a map of components by their ID for quick lookup
            CIDMap<Cell> componentMap = new CIDMap<>();
            List<Cell> allCells=new ArrayList<>();
            for (UiComponentCompositeDTO uiComponent : uiPage.getPageIdUiComponentList()) {
                Cell cell = convertUiComponentToCell(uiComponent);
                componentMap.put(new CID(uiComponent.getId(),uiComponent.getGuid()), cell);
                allCells.add(cell);
            }

            allCells.forEach(cell -> {
                if (cell.getParentId() != null) {
                    Cell parentCell = componentMap.get(cell.getParentId());
                    if (parentCell != null) {
                        cell.getParentId().setGuid(parentCell.getId().getGuid());
                    }
                }
            });

            // Then, build the tree structure by setting parent-child relationships
            Cell rootCell = null;
            for (UiComponentCompositeDTO uiComponent : uiPage.getPageIdUiComponentList()) {
                Cell cell = componentMap.get(new CID(uiComponent.getId(),uiComponent.getGuid()));
                if (Objects.equals(uiComponent.getId(), uiPage.getRootComId())) {
                    // This is the root component
                    rootCell = cell;
                } else {
                    // Find the parent and add this cell as a child
                    Cell parentCell = componentMap.get(new CID(uiComponent.getParentId(),null));
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
        uiPage.setRprjId(pageSpec.getUiPrjId());
        uiPage.setRootComId(pageSpec.getCell() != null ? pageSpec.getCell().getId().getId() : null);
        uiPage.setCanvasData(pageSpec.getCanvasRawData());

        // Convert Cell to UiComponent list
        if (pageSpec.getCell() != null) {
            List<UiComponentCompositeDTO> uiComponents = convertCellTreeToUiComponentList(pageSpec.getCell());
            uiComponents.forEach(uiComponent -> {uiComponent.setRprjId(pageSpec.getUiPrjId());});
            uiPage.setPageIdUiComponentList(uiComponents);
        }

        if(uiPage.getId()!=null){
            Fileset targetFile = filesetService.getOne(new LambdaQueryWrapper<Fileset>().eq(Fileset::getBelongtoId, uiPage.getRprjId())
                    .eq(Fileset::getBelongtoType, Constants.FILE_TYPE_PROJECT_EXTRA)
                    .eq(Fileset::getPath, uiPage.getPath()));
            FilesetCompositeDTO sf=new FilesetCompositeDTO();
            if(targetFile!=null){
                BeanUtils.copyProperties(targetFile,sf);
            }else{
                UiPrj prj = uiPrjService.getById(uiPage.getRprjId());
                sf.setBelongtoId(uiPage.getRprjId());
                sf.setBelongtoType(Constants.FILE_TYPE_PROJECT_EXTRA);
                sf.setPath(uiPage.getPath());
                sf.setStorageType(prj.getStorageType());
            }

            StringBuilder content=new StringBuilder();
            if(pageSpec.getTemplateTag()!=null){
                content.append(pageSpec.getTemplateTag());
            }else{
                content.append("<template></template>").append("\\n");
            }
            content.append("<script setup lang=\"ts\"></script>").append("\\n");

            if(pageSpec.getStyleTag()!=null){
                content.append(pageSpec.getStyleTag());
            }else{
                content.append("<style></style>");
            }
            sf.setContent(content.toString());
            filesetService.saveOrUpdate(sf);
        }

        return uiPage;
    }

    private Cell convertUiComponentToCellWithChildren(UiComponentCompositeDTO uiComponent) {
        if(uiComponent==null)
            return null;
        UiComponentCompositeDTO cc = uiComponentCompositeService.selectById(uiComponent.getId());
        if(cc==null)
            return null;
        Cell root=convertUiComponentToCell(cc);
        List<UiComponent> cList = uiComponentService.list(new LambdaQueryWrapper<UiComponent>().eq(UiComponent::getParentId, uiComponent.getId()));
        if(cList!=null)
            cList.forEach(c->{
                UiComponentCompositeDTO subc = uiComponentCompositeService.selectById(c.getId());
                if(subc!=null) {
                    Cell subroot = convertUiComponentToCellWithChildren(subc);
                    root.getChildren().add(subroot);
                }
            });
        return root;
    }

    public Cell convertUiComponentToCell(UiComponentCompositeDTO uiComponent) {
        Cell cell = new Cell();
        cell.setId(new CID(uiComponent.getId(), uiComponent.getGuid()));
        cell.setLayout(uiComponent.getLayoutData());
        cell.setCss(uiComponent.getCssData());
        cell.setZIndex(uiComponent.getZIndex());
        cell.setSort(uiComponent.getSort());
        cell.setDynamicContainer(uiComponent.getDynamicContainer());
        cell.setComponentRef(uiComponent.getComponentRef());
        cell.setPluginData(uiComponent.getPluginData());
        cell.setPluginScript(uiComponent.getPluginScript());
        if(StringUtils.isNotBlank(uiComponent.getRenderData()))
            cell.setRender(JsonUtilUnderline.parse(uiComponent.getRenderData(),Render.class));
        if(uiComponent.getParentId()!=null){
            cell.setParentId(new CID(uiComponent.getParentId(), null));
        }
        ArrayList<Event> events = new ArrayList<>();

        // Convert UiEventHandle to Cell.event and Cell.contextmenu
        if (uiComponent.getComponentIdUiEventHandleList() != null) {
            for (UiEventHandleCompositeDTO uiEventHandle : uiComponent.getComponentIdUiEventHandleList()) {
                if (Constants.EVENT_TYPE_REGULAR.equals(uiEventHandle.getEventType())) {
                    Script script = new Script(uiEventHandle.getType(), uiEventHandle.getContent());
                    List<Script> value = new ArrayList<>();
                    value.add(script);
//                    HashMap<String, List<Script>> enventMap = new HashMap<>();
                    Event event = new Event();
                    event.setKey(uiEventHandle.getEventCode());
                    event.setScripts(value);
                    events.add(event);
//                    cell.getEvent().put(uiEventHandle.getEventCode(), value);
                } else if (Constants.EVENT_TYPE_CONTEXT.equals(uiEventHandle.getEventType())) {
                    List<Script> scripts = new ArrayList<>();
                    scripts.add(new Script(uiEventHandle.getType(), uiEventHandle.getContent()));
                    Contextmenu contextmenu = new Contextmenu(uiEventHandle.getEventCode(),
                        JsonUtilUnderline.parseArray(uiEventHandle.getTargetData(), Targeted.class),
                            scripts);
                    cell.getContextmenu().add(contextmenu);
                }
            }
            cell.setEvent(events);
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
        uiComponent.setPluginData(cell.getPluginData());
        uiComponent.setPluginScript(cell.getPluginScript());

        // Convert Cell.event and Cell.contextmenu to UiEventHandle
        if (cell.getEvent() != null) {
            for (Event event : cell.getEvent()) {
                for (Script script : event.getScripts()) {
                    UiEventHandleCompositeDTO uiEventHandle = new UiEventHandleCompositeDTO();
                    uiEventHandle.setEventCode(event.getKey());
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

        if(cell.getComponentRef()!=null){
            UiComponent tCC = uiComponentService.getById(cell.getComponentRef());
            if(tCC!=null){
                uiComponent.setComponentType(tCC.getComponentType());
                uiComponent.setDeriveType(tCC.getDeriveType());
            }
        }

        return uiComponent;
    }


    public List<Cell> getTemplateCell(){
        List<UiComponent> ccList = uiComponentService.list(new LambdaQueryWrapper<UiComponent>().ge(UiComponent::getTemplate, Constants.TRUE));
        Map<Long, UiComponent> ccMap=ccList.stream().collect(Collectors.toMap(UiComponent::getId, Function.identity()));
        List<Cell> cellList = ccList.stream().map(cc -> uiComponentCompositeService.selectById(cc.getId())).map(this::convertUiComponentToCell).collect(Collectors.toList());
        List<Cell> tempCells = buildCellTree(cellList);
        tempCells.forEach(cell -> {
            UiComponent cc = ccMap.get(cell.getId().getId());
            cell.setTemplate(new TemplateInfo(cc.getTemplate()+"",cc.getTemplate()+"Group",0));
        });
        return tempCells;
    }

    public List<Cell> buildCellTree(List<Cell> cellList){
        Map<CID,Cell> cellMap = cellList.stream().collect(Collectors.toMap(Cell::getId, Function.identity()));
        cellList.forEach(cell -> {
            if(cell.getParentId()!=null){
                Cell parentCell = cellMap.get(cell.getParentId());
                if(parentCell!=null){
                    cell.getParentId().setGuid(parentCell.getId().getGuid());
                    parentCell.getChildren().add(cell);
                }
            }
        });
        return cellList.stream().filter(cell -> cell.getParentId()==null).collect(Collectors.toList());
    }
}


