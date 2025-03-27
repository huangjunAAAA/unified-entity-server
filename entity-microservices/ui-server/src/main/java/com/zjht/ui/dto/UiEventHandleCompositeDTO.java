package com.zjht.ui.dto;

import com.zjht.ui.entity.UiEventHandle;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = UiEventHandle.class)
public class UiEventHandleCompositeDTO extends UiEventHandle {
  private Long originalId;
}