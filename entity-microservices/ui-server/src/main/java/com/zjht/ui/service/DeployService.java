package com.zjht.ui.service;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.utils.NoQuotesJsonUtils;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.*;
import com.zjht.ui.dto.FilesetCompositeDTO;
import com.zjht.ui.dto.UiPageCompositeDTO;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.entity.UiPrj;
import com.zjht.unified.domain.exchange.RoutingInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeployService {
    @Resource
    private IUiPageCompositeService uiPageCompositeService;
    @Resource
    private IFilesetService filesetService;
    @Resource
    private IUiPrjService iUiPrjService;

    @Resource
    private IUiPageService uiPageService;

    @Value("${workdir:f:/tmp}")
    private String workdir;

    private ConcurrentHashMap<Long,WorkingEnv> workingDirs=new ConcurrentHashMap<>();


    @AllArgsConstructor
    private class WorkingEnv{
        private String workdir;
        private ProcessPump devProcess;
        private String runningEnv;
    }

    public R<String> devRun(Long prjId){
        compilePages(prjId);
        renderRoute(prjId);
        inflate(prjId);
        initNodeModule(prjId);
        UiPrj prj = iUiPrjService.getById(prjId);
        String nodejs="nvm use "+prj.getNodejsVer()+"\n";
        SynchronousQueue<String> runningPort=new SynchronousQueue<>();
        StringBuilder debugInfo=new StringBuilder();
        StringBuilder errInfo=new StringBuilder();
        WorkingEnv wr = workingDirs.get(prjId);
        if(wr.devProcess==null||wr.runningEnv==null) {
            try {
                String dir = workdir + prj.getWorkDir();
                String cmd = nodejs + "npm run dev -- --host 0.0.0.0";
                Process p = OsType.runCmd(cmd, new File(dir));
                wr.devProcess = new ProcessPump(p);
                wr.devProcess.start(l -> {
                    debugInfo.append(l).append("\n");
                    if (l.trim().contains("Network")) {
                        try {
                            runningPort.offer(l.trim(), 1, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {

                        }
                    }
                }, l -> {
                    errInfo.append(l).append("\n");
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }else{
            return R.ok(wr.runningEnv);
        }

        try {
            String ss = runningPort.poll(10, TimeUnit.SECONDS);
            if(ss!=null) {
                wr.runningEnv=ss;
                return R.ok(ss);
            }else
                return R.ok(errInfo.toString());
        } catch (InterruptedException e) {

        }

        return R.fail(debugInfo+"|"+errInfo);
    }

    public R<String> dryRun(Long prjId){
        return R.ok();
    }

    public R<String> deploy(Long prjId){
        return R.ok();
    }


    public void compilePages(Long prjId){
        log.info("compile project:"+prjId);
        UiPageCompositeDTO param=new UiPageCompositeDTO();
        param.setRprjId(prjId);
        List<UiPageCompositeDTO> pages = uiPageCompositeService.selectList(param);
        pages.forEach(p->{
            renderPage(p);
        });
    }

    public void initNodeModule(Long prjId){
        String dir = getWorkingDir(prjId);
        log.info("init node_module for project:"+prjId+", working dir:"+dir);
        UiPrj prj = iUiPrjService.getById(prjId);
        try {
            String nodejs="nvm use "+prj.getNodejsVer()+"\n";
            String cmd=nodejs+" npm config set registry http://wukong.zjht100.com:10003/ \n npm i";
            Process p = OsType.runCmd(cmd, new File(dir));
            new ProcessPump(p).start(null,null);
            p.waitFor();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    public R<String> inflate(Long prjId){
        String dir = getWorkingDir(prjId);
        log.info("inflate project:"+prjId+", working dir:"+dir);

        List<Fileset> pfiles = filesetService.list(new LambdaQueryWrapper<Fileset>()
                .eq(Fileset::getBelongtoId, prjId));
        Set<String> pfSet=pfiles.stream().map(pf->pf.getPath()).collect(Collectors.toSet());
        File fdir = new File(dir);
        // clean dir
        FileSetUtils.traverseDirDeepNoRoot(fdir.listFiles(),(f)->{
            try {
                String fpath = f.getAbsolutePath().replace(workdir + File.separator, "");
                if(pfSet.contains(fpath)){
                    log.info("delete project file:"+fpath);
                    f.delete();
                }
            }catch (Exception e){

            }
            return null;
        });

        traversePrjectFiles(prjId,f->{
            writeFile(fdir.getAbsolutePath(), f);
            return null;
        });

        return R.ok();
    }

    private void renderRoute(Long prjId) {
        Fileset sf = filesetService.getOne(new LambdaQueryWrapper<Fileset>()
                .eq(Fileset::getBelongtoId, prjId).eq(Fileset::getBelongtoType, Constants.FILE_TYPE_PROJECT_ROUTE));
        List<UiPage> pages = uiPageService.list(new LambdaQueryWrapper<UiPage>().eq(UiPage::getRprjId, prjId));
        StaticRoutor sr=new StaticRoutor();


        pages.forEach(p->{
            if(StringUtils.isNotBlank(p.getRoute())) {
                RoutingInfoInternal ri = JsonUtilUnderline.readValue(p.getRoute(), RoutingInfoInternal.class);
                ri.component="() => import('"+ri.component+"')";
                sr.routes.add(ri);
            }
        });
        StringBuilder rf=new StringBuilder();
        rf.append("import { createRouter, createWebHistory } from 'vue-router'\n");
        rf.append("const router=createRouter(\n").append(NoQuotesJsonUtils.toJson(sr)).append(")\n");
        rf.append("export default router");
        if(sf==null){
            UiPrj prj = iUiPrjService.getById(prjId);
            sf=new Fileset();
            sf.setBelongtoId(prjId);
            sf.setPath("src/router/index.ts");
            sf.setBelongtoType(Constants.FILE_TYPE_PROJECT_ROUTE);
            sf.setStorageType(prj.getStorageType());
            sf.setStatus(Constants.STATUS_CONFIGURE);
        }
        sf.setContent(rf.toString());
        filesetService.saveOrUpdate(sf);
    }

    private void writeFile(String workdir, Fileset f){
        String af=workdir+File.separator+FileSetUtils.translatePath(f.getPath());
        File rf = new File(af);
        if(rf.exists()){
            String oldContent = FileUtil.readString(af, Charset.forName("utf8"));
            if(oldContent.trim().equals(f.getContent().trim()))
                return;
        }
        log.info("file updated:"+f.getPath());
        FileUtil.writeString(f.getContent(),rf, Charset.forName("utf8"));
    }


    private void renderPage(UiPageCompositeDTO page){

        if(CollectionUtils.isEmpty(page.getBelongtoIdFilesetList()))
            return;
        StringBuilder pf=new StringBuilder();
        FilesetCompositeDTO fFile = page.getBelongtoIdFilesetList().get(0);
        Map<String, String> vueParts = ScriptUtils.parseVueFile(fFile.getContent());

        // 加入template部分
        pf.append(vueParts.get("templateTag")).append("\n");
        pf.append(vueParts.get("template")).append("\n").append("</template>").append("\n");

        pf.append(vueParts.get("scriptTag")).append("\n");
        // 加入 component的script部分
        log.info("page component size:"+(page.getPageIdUiComponentList()==null?0:page.getPageIdUiComponentList().size()));
        page.getPageIdUiComponentList().forEach(c->{
            String ss = c.getPluginScript();
            if(!StringUtils.isEmpty(ss)) {
                Map<String, String> mapping = ScriptUtils.readProperties(c.getPluginData());
                if(!mapping.isEmpty()) {
                    StrSubstitutor sub = new StrSubstitutor(mapping);
                    ss = sub.replace(ss);
                }
            }
            Map<String, String> ssParts = ScriptUtils.parseVueFile(ss);
            String pluginData = ssParts.get("script");
            pf.append(pluginData).append("\n");
        });

        pf.append("\n</script>").append("\n");

//        // 加入最后的 init-end部分
//        String ss = vueParts.get("script");
//        int iStart = ScriptUtils.findTagStart(ss, "//init-end");
//        if(iStart!=-1){
//            pf.append(ss.substring(iStart));
//        }

        // 加入style部分
        pf.append(vueParts.get("styleTag")).append("\n");
        pf.append(vueParts.get("style")).append("\n").append("</style>");
        fFile.setContent(pf.toString());
        filesetService.updateById(fFile);
    }

    public R<String> syncWithSvr(Long prjId,String path){

        Map<String,Fileset> files=new HashMap<>();
        traversePrjectFiles(prjId,f->{
            files.put(FileSetUtils.translatePath(f.getPath()),f);
            return null;
        });

        UiPrj prj = iUiPrjService.getById(prjId);

        FileSetUtils.traverseDirDeepNoRoot(new File(path).listFiles(),f->{
            if(f.isDirectory())
                return f;
           String rPath=FileSetUtils.translatePath(f.getPath().substring(path.length()+1));
           if(rPath.startsWith("node_modules")||rPath.startsWith("dist"))
               return f;
            Fileset sf = files.remove(rPath);
            if(sf==null){
                sf=new Fileset();
                sf.setBelongtoId(prjId);
                sf.setPath(rPath);
                if(rPath.toLowerCase().indexOf("route")==-1)
                    sf.setBelongtoType(Constants.FILE_TYPE_PROJECT_EXTRA);
                else
                    sf.setBelongtoType(Constants.FILE_TYPE_PROJECT_ROUTE);
                sf.setStorageType(prj.getStorageType());
                sf.setContent(FileUtil.readString(f,Charset.forName("utf8")));
                sf.setStatus(Constants.STATUS_CONFIGURE);
                filesetService.save(sf);
            }else{
                sf.setContent(FileUtil.readString(f,Charset.forName("utf8")));
                filesetService.updateById(sf);
            }
           return f;
        });

        filesetService.removeByIds(files.values());

        return R.ok();
    }


    private <T> List<T> traversePrjectFiles(Long prjId, Function<Fileset,T> func){
        List<Fileset> pfiles = filesetService.list(new LambdaQueryWrapper<Fileset>()
                .ne(Fileset::getPath, Constants.FILE_TYPE_PROJECT_NODE_MODULE)
                .eq(Fileset::getBelongtoId, prjId));

        UiPageCompositeDTO param=new UiPageCompositeDTO();
        param.setRprjId(prjId);
        List<UiPageCompositeDTO> pages = uiPageCompositeService.selectList(param);


        List<T> ret=new ArrayList<>();
        pfiles.forEach(f->{
            T c=func.apply(f);
            ret.add(c);
        });

        pages.forEach(p->{
            p.getBelongtoIdFilesetList().forEach(f->{
                T c=func.apply(f);
                ret.add(c);
            });
            p.getPageIdUiComponentList().forEach(com->{
                com.getBelongtoIdFilesetList().forEach(f->{
                    T c=func.apply(f);
                    ret.add(c);
                });
            });
        });
        return ret;
    }

    private String getWorkingDir(Long prjId){
        WorkingEnv wr = workingDirs.get(prjId);
        if(wr==null){
            UiPrj prj = iUiPrjService.getById(prjId);
            wr = new WorkingEnv(sanitizeWorkDir(workdir+prj.getWorkDir()),null,null);
            new File(wr.workdir).mkdirs();
            workingDirs.put(prjId,wr);
        }
        return wr.workdir;
    }

    private String sanitizeWorkDir(String dir){
        return FileSetUtils.translatePath(dir);
    }



    @Data
    private static class StaticRoutor{
        @JsonSerialize(using = NoQuotesJsonUtils.NoQuotesSerializer.class)
        private String history="createWebHistory(import.meta.env.BASE_URL)";

        private List<RoutingInfoInternal> routes=new ArrayList<>();
    }

    @Data
    private static class RoutingInfoInternal {
        @JsonSerialize(using = NoQuotesJsonUtils.SingleQuotesSerializer.class)
        private String path;
        @JsonSerialize(using = NoQuotesJsonUtils.SingleQuotesSerializer.class)
        private String name;
        @JsonSerialize(using = NoQuotesJsonUtils.NoQuotesSerializer.class)
        private String component;
        private RoutingInfo.Meta meta;

    }

    public static void main(String[] args) {
        StaticRoutor sr=new StaticRoutor();
        RoutingInfoInternal ri = new RoutingInfoInternal();
        ri.setPath("/");
        ri.setName("home");
        ri.setComponent("Home.vue");
        ri.component="() => import('"+ri.component+"')";
        ri.meta=new RoutingInfo.Meta();
        ri.meta.setTitle("首页");
        ri.meta.setNoNeedLogin(true);
        sr.getRoutes().add(ri);
        String json = NoQuotesJsonUtils.toJson(sr);
        System.out.println(json);
    }

}
