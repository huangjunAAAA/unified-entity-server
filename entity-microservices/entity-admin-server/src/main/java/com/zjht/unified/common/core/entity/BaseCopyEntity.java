package com.zjht.unified.common.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wukong.core.mp.base.BaseUserEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class BaseCopyEntity extends BaseUserEntity {

    /**
     * 创建人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "源ID")
    @Column(name = "original_id")
    private Long originalId;
}
