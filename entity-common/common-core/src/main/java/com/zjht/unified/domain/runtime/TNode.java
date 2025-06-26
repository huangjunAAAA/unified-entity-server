package com.zjht.unified.domain.runtime;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *  实体类
 *
 *      guid = generateGuid();
 *     private nodeType?: string;
 *     private nodeData?: any;
 *     private id?: number;
 *     root?: TNode;
 *     parent?: TNode;
 *     type?: string;
 *     subtype?: string;
 *     children?: TNode[];
 *
 * @author Chill
 */
@Data
@ApiModel(value = "TNode 内部类", description = "")
public class TNode {

	private static final long serialVersionUID = -5535941004277809904L;


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
	private TNode parent;

	/**
	 * 根节点
	 */
	@ApiModelProperty(value = "根节点")
	private TNode root;
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


	@ApiModelProperty(value = "子节点")
	private List<TNode> children;

}