package com.zjht.ui.dto;

import com.zjht.ui.entity.Fileset;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "DTO", description = "",parent = Fileset.class)
public class FilesetCompositeDTO extends Fileset {
  private Long originalId;
}