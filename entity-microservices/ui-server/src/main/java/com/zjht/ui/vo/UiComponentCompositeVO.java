package com.zjht.ui.vo;

import com.zjht.ui.entity.UiComponent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = UiComponent.class)
public class UiComponentCompositeVO extends UiComponentVo{
  private List<FilesetCompositeVO> belongtoIdFilesetList;
  private List<UiEventHandleCompositeVO> componentIdUiEventHandleList;
}