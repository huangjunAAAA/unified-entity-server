package com.zjht.ui.vo;

import com.zjht.ui.entity.UiLayout;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = UiLayout.class)
public class UiLayoutCompositeVO extends UiLayoutVo{
}