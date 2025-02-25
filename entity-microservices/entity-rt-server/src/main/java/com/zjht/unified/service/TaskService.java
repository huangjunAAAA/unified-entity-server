package com.zjht.unified.service;


import com.zjht.unified.domain.composite.PrjSpecDO;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    public void stopTask(Long prjId){

    }

    public void startTask(PrjSpecDO spec){
        // 将所有的类属性值都放入redis

        // 将所有的类方法都放入map待用

        // 将初始化的实例放入redis

        // 生成哨兵和状态机的定时任务，通过XXL开始执行

    }
}
