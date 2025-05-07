package com.zjht.unified.service.v8exec.model;

import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.simple.ClsRelationDO;
import com.zjht.unified.service.ctx.EntityDepService;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Data
public class NNRel {
    private ClsDf fromCls;
    private ClsDf toCls;
    private FieldDf from;
    private FieldDf to;
    private Integer type;
    private String script;
    private String guid;

    /**
     * 将ClsRelationDO转为NNRel
     * from和to从fieldIdFrom和fieldIdTo衍生，从TaskContext的clazzGUIDMap里的ClazzDefCompositeDO.clazzIdFieldDefList轮询查到，然后将ClazzDefCompositeDO转成对应的ClsDf
     * @param rel ClsRelationDO对象
     * @param ctx TaskContext对象
     * @return 转换后的NNRel对象
     */
    public static NNRel from(ClsRelationDO rel, TaskContext ctx) {
        NNRel nnRel = new NNRel();

        // 获取ClsRelationDO中的fieldIdFrom和fieldIdTo
        String fieldIdFrom = rel.getFieldIdFrom();
        String fieldIdTo = rel.getFieldIdTo();

       // 从TaskContext的clazzGUIDMap中获取ClazzDefCompositeDO
        EntityDepService entityDepService= SpringUtils.getBean(EntityDepService.class);

        // 查找fieldIdFrom对应的FieldDefCompositeDO
        FieldDefCompositeDO fieldFrom = entityDepService.getFieldDefByGuid(ctx, fieldIdFrom);

        // 查找fieldIdTo对应的FieldDefCompositeDO
        FieldDefCompositeDO fieldTo = entityDepService.getFieldDefByGuid(ctx, fieldIdTo);

        // 如果找到对应的FieldDefCompositeDO，则设置from和to
        if (fieldFrom != null) {
            ClazzDefCompositeDO clazzDefCompositeDO = entityDepService.getClsDefByGuid(ctx, fieldFrom.getClassGuid());
            nnRel.fromCls=(ClsDf.from(clazzDefCompositeDO,ctx));
        }

        if (fieldTo != null) {
            ClazzDefCompositeDO clazzDefCompositeDO = entityDepService.getClsDefByGuid(ctx, fieldFrom.getClassGuid());
            nnRel.toCls=(ClsDf.from(clazzDefCompositeDO,ctx));
        }

        // 设置其他属性
        nnRel.setType(rel.getRel());
        nnRel.setGuid(rel.getGuid());

        return nnRel;
    }
}
