package com.zjht.unified.common.core.constants;

import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.runtime.TNode;
import com.zjht.unified.domain.simple.*;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreClazzDef {

    public static final String CLAZZ_TREE_NODE = "20877a07-962b-41c6-943c-6f085513b2fb";
    public static final String CLAZZ_SENTINEL = "f0c0a2f4-b0f2-4d0d-a0d2-b9f0f0f0f0f0";
    public static final String CLAZZ_CLSREL = "e6b49b1f-342e-4cda-b030-bae68b561e24";
    public static final String CLAZZ_DATAVIEW = "aa5053a7-9b7e-44f1-8eac-4189fcb71c0f";
    public static final String CLAZZ_DB_TABLE = "9f2ff099-3191-44df-9a1e-f73c5253fdc8";
    public static final String CLAZZ_FSM = "06ad2959-d7b2-4e76-a9a6-842112b0988c";

    public static final String NAME_TREE_NODE = "TNode";
    public static final String NAME_CLSREL = "ClsRel";
    public static final String NAME_DATAVIEW = "DataView";
    public static final String NAME_DB_TABLE = "DTbl";
    public static final String NAME_SENTINEL = "Sentinel";
    public static final String NAME_FSM = "Fsm";

    public static final String[] CORE_CLASS_GUID = {CLAZZ_TREE_NODE, CLAZZ_CLSREL, CLAZZ_DATAVIEW, CLAZZ_DB_TABLE,CLAZZ_SENTINEL,CLAZZ_FSM};
    public static final String[] CORE_CLASS_NAME = {NAME_TREE_NODE, NAME_CLSREL, NAME_DATAVIEW, NAME_DB_TABLE,NAME_SENTINEL,NAME_FSM};

    public static String getCoreClassName(String guid) {
        for (int i = 0; i < CORE_CLASS_GUID.length; i++) {
            if (CORE_CLASS_GUID[i].equalsIgnoreCase(guid)) {
                return CORE_CLASS_NAME[i];
            }
        }
        return null;
    }

    public static String getCoreClassGuid(String name) {
        for (int i = 0; i < CORE_CLASS_NAME.length; i++) {
            if (CORE_CLASS_NAME[i].equalsIgnoreCase(name)) {
                return CORE_CLASS_GUID[i];
            }
        }
        return null;
    }

    private static final Map<String, ClazzDefCompositeDO> coreClsMap = new HashMap<>();

    public static ClazzDefCompositeDO getCoreClassObject(String guid) {
        init();
        return coreClsMap.get(guid);
    }

    private static void init() {
        if (coreClsMap.isEmpty())
            synchronized (CoreClazzDef.class) {
                if(coreClsMap.isEmpty()){
                    coreClsMap.put(CLAZZ_TREE_NODE,convert(TNode.class));
                    coreClsMap.put(CLAZZ_CLSREL,convert(ClsRelationDO.class));
                    coreClsMap.put(CLAZZ_DATAVIEW,convert(ViewDefDO.class));
                    coreClsMap.put(CLAZZ_DB_TABLE,convert(DbtableAliasDO.class));
                    coreClsMap.put(CLAZZ_SENTINEL,convert(SentinelDefDO.class));
                    coreClsMap.put(CLAZZ_FSM,convert(FsmDefDO.class));
                }
            }
    }

    private static ClazzDefCompositeDO convert(Class<?> cls) {
        ClazzDefCompositeDO clazzDefCompositeDO = new ClazzDefCompositeDO();
        clazzDefCompositeDO.setClazzIdFieldDefList(new ArrayList<>());
        clazzDefCompositeDO.setClazzIdMethodDefList(new ArrayList<>());

        clazzDefCompositeDO.setName(cls.getSimpleName());
        clazzDefCompositeDO.setNameZh(cls.getSimpleName());
        clazzDefCompositeDO.setType("CORE");
        clazzDefCompositeDO.setVersion("1.0");
        clazzDefCompositeDO.setModifer("public");
        clazzDefCompositeDO.setInheritRead(1);
        clazzDefCompositeDO.setInheritWrite(1);
        clazzDefCompositeDO.setType("System");
        clazzDefCompositeDO.setTbl(StringUtils.toUnderScoreCase(cls.getSimpleName()));

        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            FieldDefCompositeDO fieldDefCompositeDO = new FieldDefCompositeDO();
            fieldDefCompositeDO.setName(field.getName());

            if(field.getType().isPrimitive()) {
                fieldDefCompositeDO.setNature(Integer.parseInt(FieldConstants.FIELD_TYPE_PRIMITIVE));
                fieldDefCompositeDO.setType(field.getType().getSimpleName());
            }else if (field.getType().equals(TNode.class)){
                fieldDefCompositeDO.setNature(Integer.parseInt(FieldConstants.FIELD_TYPE_TREENODE));
                fieldDefCompositeDO.setType(String.class.getSimpleName());
            }else if(field.getType().equals(List.class)){
                continue;
            }else{
                fieldDefCompositeDO.setNature(Integer.parseInt(FieldConstants.FIELD_TYPE_REGULAR_CLASS));
                fieldDefCompositeDO.setType(String.class.getSimpleName());
            }
            ApiModelProperty dname = field.getAnnotation(ApiModelProperty.class);
            if(dname!=null&&StringUtils.isNotEmpty(dname.value())){
                fieldDefCompositeDO.setDisplayName(dname.value());
            }else {
                fieldDefCompositeDO.setDisplayName(field.getName());
            }
            fieldDefCompositeDO.setCachable(0);

            clazzDefCompositeDO.getClazzIdFieldDefList().add(fieldDefCompositeDO);
        }

        return clazzDefCompositeDO;
    }
}
