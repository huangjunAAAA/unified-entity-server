package com.zjht.unified.common.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wukong.core.mp.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity基类
 * 
 * @author zjht
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "基类")
public class BaseSystemEntity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "组织id")
    private Long deptId;


    @ApiModelProperty(value = "创建用户")
    private String createBy;

    /**
     * 状态[1:正常]
     */
    @ApiModelProperty(value = "业务状态")
    @Column(name="status")
    private Integer status;

    /**
     * 创建人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "创建人")
    @Column(name = "create_id")
    @TableField("create_id")
    private Long createUser;

    /**
     * 更新人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "更新人")
    @Column(name="update_id")
    @TableField("update_id")
    private Long updateUser;


}
