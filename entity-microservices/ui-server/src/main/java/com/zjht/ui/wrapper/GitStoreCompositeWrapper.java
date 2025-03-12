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


public class GitStoreCompositeWrapper{
    public void visitComposite(GitStoreCompositeDTO gitStore, Consumer<BaseEntity> visitor){
        visitor.accept(gitStore);
    }

    public static GitStoreCompositeWrapper build() {
        return new GitStoreCompositeWrapper();
    }

    public GitStoreCompositeVO entityVO(GitStoreCompositeDTO gitStore){
        GitStoreWrapper wrapper=GitStoreWrapper.build();
        GitStoreCompositeVO vo= (GitStoreCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(gitStore),new GitStoreCompositeVO(),null);

        return vo;
    }
    
    public List<GitStoreCompositeVO> entityVOList(List<GitStoreCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<GitStoreCompositeVO> pageVO(IPage<GitStoreCompositeDTO> source){
        Page<GitStoreCompositeVO> page = new Page<GitStoreCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}