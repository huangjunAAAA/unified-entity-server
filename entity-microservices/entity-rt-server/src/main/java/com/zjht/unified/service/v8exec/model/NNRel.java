package com.zjht.unified.service.v8exec.model;

import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.simple.ClsRelationDO;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.Data;

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
        Map<String, ClazzDefCompositeDO> clazzGUIDMap = ctx.getClazzGUIDMap();

        // 遍历clazzGUIDMap，查找fieldIdFrom和fieldIdTo对应的ClazzDefCompositeDO
        for (Map.Entry<String, ClazzDefCompositeDO> entry : clazzGUIDMap.entrySet()) {
            ClazzDefCompositeDO clazzDefCompositeDO = entry.getValue();

            // 查找fieldIdFrom对应的FieldDefCompositeDO
            FieldDefCompositeDO fieldFrom = clazzDefCompositeDO.getClazzIdFieldDefList().stream()
                .filter(field -> field.getGuid().equals(fieldIdFrom))
                .findFirst()
                .orElse(null);

            // 查找fieldIdTo对应的FieldDefCompositeDO
            FieldDefCompositeDO fieldTo = clazzDefCompositeDO.getClazzIdFieldDefList().stream()
                .filter(field -> field.getGuid().equals(fieldIdTo))
                .findFirst()
                .orElse(null);

            // 如果找到对应的FieldDefCompositeDO，则设置from和to
            if (fieldFrom != null) {
                nnRel.fromCls=(ClsDf.from(clazzDefCompositeDO,ctx));
            }
            if (fieldTo != null) {
                nnRel.toCls=(ClsDf.from(clazzDefCompositeDO,ctx));
            }
        }

        // 设置其他属性
        nnRel.setType(rel.getRel());
        nnRel.setGuid(rel.getGuid());

        return nnRel;
    }
}
