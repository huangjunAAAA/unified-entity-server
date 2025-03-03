package com.zjht.unified.feign;

import com.zjht.unified.feign.model.ReturnMap;
import com.zjht.unified.feign.model.XxlJobGroup;
import com.zjht.unified.feign.model.XxlJobInfo;
import com.zjht.unified.feign.model.ReturnT;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "job-admin", configuration = IgnoreValidateFormDataConfiguration.class)
public interface RemoteXXL {
    @PostMapping(value="/xxl-job-admin/jobinfo/add",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT addJob(XxlJobInfo jobInfo);
    @PostMapping(value="/xxl-job-admin/jobinfo/update",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT updateJob(XxlJobInfo jobInfo);
    @PostMapping("/xxl-job-admin/jobinfo/remove")
    ReturnT removeJob(@RequestParam int id);
    @PostMapping("/xxl-job-admin/jobgroup/pageList")
    ReturnMap<XxlJobGroup> listJobGroup(@RequestParam Integer start, @RequestParam Integer length, @RequestParam String appname, @RequestParam String title);
    @PostMapping("/xxl-job-admin/jobinfo/pageList")
    ReturnMap<XxlJobInfo> listJobInfo(@RequestParam int start, @RequestParam int length, @RequestParam int jobGroup, @RequestParam String executorHandler, @RequestParam String author);
    @PostMapping(value="/xxl-job-admin/jobgroup/save",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT addJobGroup(XxlJobGroup jobGroup);
    @PostMapping("/xxl-job-admin/jobinfo/start")
    ReturnT startJob(@RequestParam Integer id);
}
