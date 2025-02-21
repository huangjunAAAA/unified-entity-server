package com.zjht.unified.dto;

import com.zjht.unified.entity.ConfigGraph;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = ConfigGraph.class)
public class ConfigGraphCompositeDTO extends ConfigGraph {
  private Long originalId;
  private ClazzDefCompositeDTO nodeIdClazzDefComposite;
  public static final String NODEID_NODETYPE_CLAZZDEF_SK="sk-clazzDef-ConfigGraph-NODEID-nodeType";
  private FsmDefCompositeDTO nodeIdFsmDefComposite;
  public static final String NODEID_NODETYPE_FSMDEF_SK="sk-fsmDef-ConfigGraph-NODEID-nodeType";
  private SentinelDefCompositeDTO nodeIdSentinelDefComposite;
  public static final String NODEID_NODETYPE_SENTINELDEF_SK="sk-sentinelDef-ConfigGraph-NODEID-nodeType";
  private ViewDefCompositeDTO nodeIdViewDefComposite;
  public static final String NODEID_NODETYPE_VIEWDEF_SK="sk-viewDef-ConfigGraph-NODEID-nodeType";
}