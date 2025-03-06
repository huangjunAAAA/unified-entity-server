package com.zjht.unified.service.ctx;


import com.wukong.core.weblog.utils.JsonUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.config.RedisKeyName;
import com.zjht.unified.domain.composite.PrjSpecDO;
import com.zjht.unified.domain.simple.InstanceFieldDO;
import com.zjht.unified.domain.runtime.UnifiedObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class RtRedisObjectStorageService {


    @Autowired
    private EntityDepService entityDepService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void setObject(TaskContext ctx, UnifiedObject uo){
        String key=RedisKeyName.getObjectRtKey(ctx.getVer(),uo.getGuid());
        redisTemplate.opsForValue().setIfAbsent(key,uo);
    }

    public UnifiedObject getObject(TaskContext ctx, String guid){
        String key=RedisKeyName.getObjectRtKey(ctx.getVer(),guid);
        return (UnifiedObject) redisTemplate.opsForValue().get(key);
    }

    public void setObjectAttrValue(TaskContext ctx, String guid, String attrName, Object val, boolean dispatch){
        log.info("set attr:"+guid+"."+attrName+"=>["+val+"]");
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer());
        redisTemplate.opsForHash().put(key,attrName,val);
        if(val instanceof  UnifiedObject){
            setObject(ctx, (UnifiedObject) val);
        }
        if (dispatch) {

        }
    }

    public void delObjectAttr(TaskContext ctx,String guid,String attrName){
        log.info("delete attr:"+guid+"."+attrName);
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer());
        redisTemplate.opsForHash().delete(key,attrName);
    }

    public Object getObjectAttrValue(TaskContext ctx, String guid, String attrName){
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer());
        return redisTemplate.opsForHash().get(key,attrName);
    }

    public Map<String, Object> getObjectAttrValueMap(TaskContext ctx, String guid){
        String key = RedisKeyName.getObjectKey(guid, ctx.getVer());
        Map<Object, Object> kvMap = redisTemplate.opsForHash().entries(key);
        Map<String, Object> resultMap = new HashMap<>();
        kvMap.forEach((k, v) -> resultMap.put(String.valueOf(k), v));
        return resultMap;
    }

    public String getAttrDef(TaskContext ctx, String guid, String attrName){
        String key = RedisKeyName.getObjectAttrKey(guid, ctx.getVer(), attrName);
        return (String)redisTemplate.opsForValue().get(key);
    }

    public void setAttrDef(TaskContext ctx, String guid, String attrName,String calcExpr){
        String key = RedisKeyName.getObjectAttrKey(guid, ctx.getVer(), attrName);
        redisTemplate.opsForValue().set(key,calcExpr);
    }

    public void initSpecDefinition(TaskContext ctx, PrjSpecDO spec){
        spec.getStaticDefList().forEach(sd->{
            setObjectAttrValue(ctx,RedisKeyName.getStaticKey(ctx.getVer()),sd.getFieldName(),sd.getFieldValue(),false);
        });

        spec.getClazzList().forEach(cd->{
            if(!ctx.getClazzMap().containsKey(cd.getName()))
                ctx.getClazzMap().put(cd.getName(),cd);
            ctx.getClazzMap().put(spec.getUePrj().getGuid()+":"+cd.getName(),cd);
            ctx.getClazzGUIDMap().put(cd.getGuid(),cd);
        });
        spec.getClazzList().forEach(cd->{
            cd.getClazzIdFieldDefList().forEach(fd->{
                if(fd.getNature()== Constants.FIELD_TYPE_SCRIPT){
                    setAttrDef(ctx,cd.getGuid(),fd.getName(),fd.getInitValue());
                }
            });
            cd.getClazzIdMethodDefList().forEach(md->{
                String key = RedisKeyName.getObjectAttrKey(cd.getGuid(), ctx.getVer(), md.getName());
                ctx.getMethods().put(key,md);
            });
        });

        if(spec.getDepPkgList()!=null){
            spec.getDepPkgList().forEach(pkg->{
                initSpecDefinition(ctx,pkg);
            });
        }
    }

    public void initializeInstances(TaskContext ctx, PrjSpecDO spec){
        spec.getInstanceList().forEach(inst->{
            setObject(ctx,new UnifiedObject(inst.getGuid(),inst.getClassGuid(),true));
            if(StringUtils.isNotBlank(inst.getAttrValue())){
                List<InstanceFieldDO> fdlst = JsonUtil.parseArray(inst.getAttrValue(), InstanceFieldDO.class);
                fdlst.forEach(fd->{
                    setObjectAttrValue(ctx,inst.getGuid(),fd.getField(),fd.getCurrentValue(),false);
                });
            }
        });
    }
}
