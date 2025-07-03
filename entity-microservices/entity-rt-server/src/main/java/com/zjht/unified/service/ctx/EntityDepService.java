package com.zjht.unified.service.ctx;

import com.zjht.unified.common.core.constants.CoreClazzDef;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class EntityDepService {

    @Resource
    private RtRedisObjectStorageService rtRedisObjectStorageService;

    public ClazzDefCompositeDO getClsDefByGuid(TaskContext ctx, String clsGuid) {
        return getClsDefByGuid(ctx, clsGuid, null);
    }

    public ClazzDefCompositeDO getClsDefByGuid(TaskContext ctx, String clsGuid, String prjGuid) {
        if (StringUtils.isBlank(clsGuid)) {
            return null;
        }
        // 尝试core class
        ClazzDefCompositeDO clsdef = CoreClazzDef.getCoreClassObject(clsGuid);
        if (clsdef != null)
            return clsdef;

        // 尝试当前项目
        if (prjGuid == null || Objects.equals(ctx.getPrjInfo().getPrjGuid(), prjGuid))
            clsdef = rtRedisObjectStorageService.getClsDef(ctx, ctx.getPrjInfo().getPrjVer(), clsGuid);
        if (clsdef != null)
            return clsdef;

        // 递归尝试依赖项目
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            clsdef = getClsDefByGuid(dep, clsGuid, prjGuid);
            if (clsdef != null)
                return clsdef;
        }
        return null;
    }

    public PrjUniqueInfo getPrjInfoByGuid(TaskContext ctx, String clsGuid) {
        if (StringUtils.isBlank(clsGuid)) {
            return null;
        }
        ClazzDefCompositeDO clsdef = rtRedisObjectStorageService.getClsDef(ctx, ctx.getPrjInfo().getPrjVer(), clsGuid);
        if (clsdef == null) {
            for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
                TaskContext dep = iterator.next();
                return getPrjInfoByGuid(dep, clsGuid);
            }
        } else {
            return ctx.getPrjInfo();
        }
        return null;
    }

    public ClazzDefCompositeDO getClsDefByInstanceId(TaskContext ctx, String guid) {
        return getClsDefByInstanceId(ctx, guid, null);
    }

    public ClazzDefCompositeDO getClsDefByInstanceId(TaskContext ctx, String guid, String prjGuid) {
        if (StringUtils.isBlank(guid)) {
            return null;
        }
        UnifiedObject uo = null;
        if (prjGuid == null || Objects.equals(ctx.getPrjInfo().getPrjGuid(), prjGuid)) {
            uo = rtRedisObjectStorageService.getObject(ctx, guid, ctx.getPrjInfo().getPrjGuid(), ctx.getPrjInfo().getPrjVer());
        }
        if(uo != null)
            return rtRedisObjectStorageService.getClsDef(ctx, uo.getPrjVer(), uo.getClazzGUID());;
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            return getClsDefByInstanceId(dep, guid, prjGuid);
        }
        return null;
    }

    public ClazzDefCompositeDO getClsByName(TaskContext ctx, String name) {
        return getClsByName(ctx, name, null);
    }

    public ClazzDefCompositeDO getClsByName(TaskContext ctx, String name, String prjGuid) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        // 尝试core class
        String sysGuid = CoreClazzDef.getCoreClassGuid(name);
        if (sysGuid != null) {
            return CoreClazzDef.getCoreClassObject(sysGuid);
        }

        ClazzDefCompositeDO clsdef = null;
        if (prjGuid == null || Objects.equals(ctx.getPrjInfo().getPrjGuid(), prjGuid))
            clsdef = rtRedisObjectStorageService.getClsDefByName(ctx, ctx.getPrjInfo().getPrjVer(), name);
        if (clsdef != null)
            return clsdef;
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            clsdef = getClsByName(dep, name, prjGuid);
            if (clsdef != null)
                return clsdef;
        }
        return null;
    }

    public FieldDefCompositeDO getFieldDefByGuid(TaskContext ctx, String fieldGuid) {
        return getFieldDefByGuid(ctx, fieldGuid, null);
    }

    public FieldDefCompositeDO getFieldDefByGuid(TaskContext ctx, String fieldGuid, String prjGuid) {
        if (StringUtils.isBlank(fieldGuid))
            return null;
        Object fieldObj = null;
        if(prjGuid == null || (Objects.equals(ctx.getPrjInfo().getPrjGuid(), prjGuid)))
            fieldObj = rtRedisObjectStorageService.getAttrDefByGuid(ctx, ctx.getPrjInfo().getPrjVer(), fieldGuid);
        if (fieldObj != null) {
            if (fieldObj instanceof FieldDefCompositeDO)
                return (FieldDefCompositeDO) fieldObj;
            return null;
        }
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            if(prjGuid == null|| (Objects.equals(ctx.getPrjInfo().getPrjGuid(), prjGuid)))
                fieldObj = getFieldDefByGuid(dep, fieldGuid, prjGuid);
            if (fieldObj != null) {
                if (fieldObj instanceof FieldDefCompositeDO)
                    return (FieldDefCompositeDO) fieldObj;
                return null;
            }
        }
        return null;
    }

    public Object getObjectAttrValue(TaskContext ctx, String guid, String attrName) {
        if (StringUtils.isBlank(guid) || StringUtils.isBlank(attrName))
            return null;
        UnifiedObject uo = getObject(ctx, guid);
        if (uo == null)
            return null;
        return rtRedisObjectStorageService.getObjectAttrValue(ctx, guid, attrName, uo.getPrjGuid(), uo.getPrjVer());
    }

    public UnifiedObject getObject(TaskContext ctx, String guid) {
        return getObject(ctx,guid,null);
    }
    public UnifiedObject getObject(TaskContext ctx, String guid, String prjGuid) {
        if (StringUtils.isBlank(guid))
            return null;
        UnifiedObject uo = null;
        if(prjGuid == null|| (Objects.equals(ctx.getPrjInfo().getPrjGuid(), prjGuid)))
            uo=rtRedisObjectStorageService.getObject(ctx, guid, ctx.getPrjInfo().getPrjGuid(), ctx.getPrjInfo().getPrjVer());
        if(uo!=null)
            return uo;
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            uo = getObject(dep, guid, prjGuid);
            if (uo != null)
                return uo;
        }
        return null;
    }

    public Boolean deleteObject(TaskContext ctx, String guid) {
        if (StringUtils.isBlank(guid))
            return false;
        UnifiedObject uo = getObject(ctx, guid);
        if (uo == null)
            return false;
        return rtRedisObjectStorageService.deleteObject(ctx, guid, uo.getPrjGuid(), uo.getPrjVer());
    }

    public List<ClazzDefCompositeDO> getClassDefWithParents(TaskContext ctx, String clsGuid) {
        ClazzDefCompositeDO cRoot = getClsDefByGuid(ctx, clsGuid);
        if(cRoot==null){
            return new ArrayList<>();
        }
        List<ClazzDefCompositeDO> clazzList = new ArrayList<>();
        clazzList.add(cRoot);
        while(cRoot.getParentGuid()!=null){
            cRoot = getClsDefByGuid(ctx, cRoot.getParentGuid());
            if(cRoot==null){
                break;
            }
            clazzList.add(cRoot);
        }
        return clazzList;
    }

}
