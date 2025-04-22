package com.zjht.ui.vo;

import com.zjht.ui.entity.UiPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = UiPage.class)
public class UiPageCompositeVO extends UiPageVo{
  private List<UiComponentCompositeVO> pageIdUiComponentList;
  private UiLayoutCompositeVO layoutIdUiLayoutComposite;
}