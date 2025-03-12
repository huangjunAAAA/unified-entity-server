package com.zjht.ui.wrapper;

import com.wukong.core.mp.base.BaseEntity;
import com.wukong.core.util.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.*;
import java.util.stream.Collectors;
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.function.Function;


public class FilesetCompositeWrapper{
    public void visitComposite(FilesetCompositeDTO fileset, Consumer<BaseEntity> visitor){
        visitor.accept(fileset);
    }

    public static FilesetCompositeWrapper build() {
        return new FilesetCompositeWrapper();
    }

    public FilesetCompositeVO entityVO(FilesetCompositeDTO fileset){
        FilesetWrapper wrapper=FilesetWrapper.build();
        FilesetCompositeVO vo= (FilesetCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(fileset),new FilesetCompositeVO(),null);

        return vo;
    }
    
    public List<FilesetCompositeVO> entityVOList(List<FilesetCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<FilesetCompositeVO> pageVO(IPage<FilesetCompositeDTO> source){
        Page<FilesetCompositeVO> page = new Page<FilesetCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}