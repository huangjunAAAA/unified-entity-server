package com.zjht.unified.service.ctx;

import com.zjht.unified.common.core.constants.CoreClazzDef;
import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;

@Service
public class EntityDepService {

    @Resource
    private RtRedisObjectStorageService rtRedisObjectStorageService;
    public ClazzDefCompositeDO getClsDefByGuid(TaskContext ctx, String clsGuid){
        if (StringUtils.isBlank(clsGuid)){
            return null;
        }
        ClazzDefCompositeDO clsdef =CoreClazzDef.getCoreClassObject(clsGuid);
        if(clsdef!=null)
            return clsdef;
        clsdef = rtRedisObjectStorageService.getClsDef(ctx, ctx.getPrjInfo().getPrjVer(), clsGuid);
        if(clsdef==null){
            for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
                TaskContext dep = iterator.next();
                clsdef=rtRedisObjectStorageService.getClsDef(dep, dep.getPrjInfo().getPrjVer(), clsGuid);
                if(clsdef!=null)
                    return clsdef;
            }
        }else
            return clsdef;
        return null;
    }

    public PrjUniqueInfo getPrjInfoByGuid(TaskContext ctx, String clsGuid){
        if (StringUtils.isBlank(clsGuid)){
            return null;
        }
        ClazzDefCompositeDO clsdef = rtRedisObjectStorageService.getClsDef(ctx, ctx.getPrjInfo().getPrjVer(), clsGuid);
        if(clsdef==null){
            for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
                TaskContext dep = iterator.next();
                clsdef=rtRedisObjectStorageService.getClsDef(dep, dep.getPrjInfo().getPrjVer(), clsGuid);
                if(clsdef!=null)
                    return dep.getPrjInfo();
            }
        }else{
            return ctx.getPrjInfo();
        }
        return null;
    }

    public ClazzDefCompositeDO getClsDefByInstanceId(TaskContext ctx, String guid){
        if (StringUtils.isBlank(guid)){
            return null;
        }
        UnifiedObject uo = rtRedisObjectStorageService.getObject(ctx, guid, ctx.getPrjInfo().getPrjGuid(), ctx.getPrjInfo().getPrjVer());
        if(uo==null){
            for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
                TaskContext dep = iterator.next();
                uo=rtRedisObjectStorageService.getObject(dep, guid, dep.getPrjInfo().getPrjGuid(), dep.getPrjInfo().getPrjVer());
                if(uo!=null)
                    return rtRedisObjectStorageService.getClsDef(dep, uo.getPrjVer(),  uo.getClazzGUID());
            }
        }else
            return rtRedisObjectStorageService.getClsDef(ctx, uo.getPrjVer(),  uo.getClazzGUID());
        return null;
    }

    public ClazzDefCompositeDO getClsByName(TaskContext ctx, String name){
        if (StringUtils.isBlank(name)){
            return null;
        }
        String sysGuid = CoreClazzDef.getCoreClassGuid(name);
        if(sysGuid!=null){
            return CoreClazzDef.getCoreClassObject(sysGuid);
        }
        ClazzDefCompositeDO clsdef=rtRedisObjectStorageService.getClsDefByName(ctx, ctx.getPrjInfo().getPrjVer(), name);
        if(clsdef!=null)
            return clsdef;
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            clsdef=rtRedisObjectStorageService.getClsDefByName(dep, dep.getPrjInfo().getPrjVer(), name);
            if(clsdef!=null)
                return clsdef;
        }
        return null;
    }

    public FieldDefCompositeDO getFieldDefByGuid(TaskContext ctx, String fieldGuid){
        if(StringUtils.isBlank(fieldGuid))
            return null;
        Object fieldObj=rtRedisObjectStorageService.getAttrDefByGuid(ctx, ctx.getPrjInfo().getPrjVer(), fieldGuid);
        if(fieldObj!=null){
            if(fieldObj instanceof FieldDefCompositeDO)
                return (FieldDefCompositeDO) fieldObj;
            return null;
        }
        for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
            TaskContext dep = iterator.next();
            fieldObj=rtRedisObjectStorageService.getAttrDefByGuid(dep, dep.getPrjInfo().getPrjVer(), fieldGuid);
            if(fieldObj!=null){
                if(fieldObj instanceof FieldDefCompositeDO)
                    return (FieldDefCompositeDO) fieldObj;
                return null;
            }
        }
        return null;
    }

    public Object getObjectAttrValue(TaskContext ctx, String guid, String attrName){
        if (StringUtils.isBlank(guid)||StringUtils.isBlank(attrName))
            return null;
        UnifiedObject uo = getObject(ctx, guid);
        if(uo==null)
            return null;
        return rtRedisObjectStorageService.getObjectAttrValue(ctx, guid, attrName, uo.getPrjGuid(), uo.getPrjVer());
    }

    public UnifiedObject getObject(TaskContext ctx, String guid){
        if (StringUtils.isBlank(guid))
            return null;
        UnifiedObject uo = rtRedisObjectStorageService.getObject(ctx, guid, ctx.getPrjInfo().getPrjGuid(), ctx.getPrjInfo().getPrjVer());
        if(uo==null){
            for (Iterator<TaskContext> iterator = ctx.getDeps().values().iterator(); iterator.hasNext(); ) {
                TaskContext dep = iterator.next();
                uo=rtRedisObjectStorageService.getObject(dep, guid, dep.getPrjInfo().getPrjGuid(), dep.getPrjInfo().getPrjVer());
                if(uo!=null)
                    return uo;
            }
        }else
            return uo;
        return null;
    }

    public Boolean deleteObject(TaskContext ctx, String guid){
        if (StringUtils.isBlank(guid))
            return false;
        UnifiedObject uo = getObject(ctx, guid);
        if(uo==null)
            return false;
        return rtRedisObjectStorageService.deleteObject(ctx, guid, uo.getPrjGuid(), uo.getPrjVer());
    }

}
