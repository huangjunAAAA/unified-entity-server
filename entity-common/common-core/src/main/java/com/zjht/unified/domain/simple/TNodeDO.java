package com.zjht.unified.domain.simple;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class TNodeDO {

    private static final long serialVersionUID = 29382572398923L;

    /**
     *
     */
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * GUID
     */
    @ApiModelProperty(value = "GUID")
    private String guid;

    /**
     * 节点类型
     */
    @ApiModelProperty(value = "节点类型")
    private String nodeType;

    /**
     * 根实例节点ID
     */
    @ApiModelProperty(value = "根实例节点ID")
    private String nodeData;
    /**
     * 父节点ID
     */
    @ApiModelProperty(value = "父节点")
    private String parent;

    /**
     * 根节点
     */
    @ApiModelProperty(value = "根节点")
    private String root;
    /**
     * 项目ID
     */
    @ApiModelProperty(value = "项目ID")
    private Long prjId;
    /**
     * 用以区别各种树形结构
     */
    @ApiModelProperty(value = "用以区别各种树形结构")
    private String type;
    /**
     * 用以区别各种树形子结构
     */
    @ApiModelProperty(value = "用以区别各种树形子结构")
    private String subtype;

}
