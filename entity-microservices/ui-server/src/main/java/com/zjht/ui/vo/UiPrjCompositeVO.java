package com.zjht.ui.vo;

import com.zjht.ui.entity.UiPrj;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "VO", description = "",parent = UiPrj.class)
public class UiPrjCompositeVO extends UiPrjVo{
  private List<UiPageCompositeVO> rprjIdUiPageList;
  private GitStoreCompositeVO gitIdGitStoreComposite;
}