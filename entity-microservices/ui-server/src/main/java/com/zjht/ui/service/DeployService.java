package com.zjht.ui.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zjht.ui.entity.UiPage;
import com.zjht.ui.utils.NoQuotesJsonUtils;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.R;
import com.zjht.unified.common.core.util.*;
import com.zjht.ui.dto.UiPageCompositeDTO;
import com.zjht.ui.entity.Fileset;
import com.zjht.ui.entity.UiPrj;
import com.zjht.unified.domain.exchange.RoutingInfo;
import com.zjht.unified.domain.exchange.Script;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
    private IUiPrjService uiPrjService;

    @Resource
    private IUiPageService uiPageService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${workdir:f:/tmp}")
    private String workdir;

    private ConcurrentHashMap<Long,WorkingEnv> workingDirs=new ConcurrentHashMap<>();


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class WorkingEnv{
        private String workdir;
        @JsonIgnore
        private ProcessPump devProcess;
        private String runningEnv;
        private Long pid;
        private Long prjId;
        private String trait;

        public void clear(){
            devProcess=null;
            runningEnv=null;
            pid=null;
        }
    }

    private static void shutdownWorkingEnv(WorkingEnv workingEnv){
        if(workingEnv.devProcess!=null && workingEnv.devProcess.getProc().isAlive()){
            workingEnv.devProcess.getProc().destroy();
            return;
        }
        if(workingEnv.getPid()!=null){
            killPid(workingEnv.getPid());
        }
    }




    private boolean isWorkingEnvValid(WorkingEnv workingEnv){
        if(workingEnv==null)
            return false;
        if(workingEnv.runningEnv==null){
            log.info("runningEnv is null:"+workingEnv.getWorkdir());
            return false;
        }
        if(workingEnv.devProcess!=null){
            if(workingEnv.devProcess.getProc().isAlive())
                return true;
            else{
                log.info("devProcess is dead:"+workingEnv.getWorkdir());
                return false;
            }
        }

        if(workingEnv.pid==null){
            log.info("pid is null:"+workingEnv.getWorkdir());
            return false;
        }

        return isPidExist(workingEnv.pid);
    }

    private void persistWorkingEnv(WorkingEnv workingEnv){
        log.info("persist workdir:"+workingEnv.getWorkdir()+", prj id:"+workingEnv.getPrjId());
        workingDirs.put(workingEnv.prjId,workingEnv);
        String weData = JsonUtilUnderline.toJson(workingEnv);
        redisTemplate.opsForHash().put(Constants.VITE_IN_RUNNING,workingEnv.prjId+"",weData);
    }


    public static boolean killPid(long pid){
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command = os.contains("win")? "taskkill /F /PID " + pid: "kill -9 " + pid;
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPidExist(long pid) {
        String os = System.getProperty("os.name").toLowerCase();
        String command = os.contains("win")
                ? "tasklist /FI \"PID eq " + pid + "\""  // Windows命令
                : "ps -p " + pid;                        // Linux命令

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            // 解析输出判断结果
            StringBuilder output = new StringBuilder();
            boolean ret= reader.lines().map(l->{
                output.append(l).append("\n");
                return l;
            }).anyMatch(line ->
                    line.contains("" + pid) && !line.contains("grep"));
            reader.close();
            log.info(pid+" isPidExist:"+ret);
            if(!ret){
                log.info(output.toString());
            }
            return ret;
        } catch (IOException e) {
            return false;
        }
    }

    private void destroyWorkingEnv(WorkingEnv workingEnv){
        workingDirs.remove(workingEnv.workdir);
    }

    public R<String> devRun(Long prjId,Boolean restart){
        synchronized (prjId.toString()) {
            compilePages(prjId);
            renderRoute(prjId);
            inflate(prjId);
            initNodeModule(prjId);
            UiPrj prj = uiPrjService.getById(prjId);
            String nodejs = "nvm use " + prj.getNodejsVer() + "\n";
            SynchronousQueue<String> runningPort = new SynchronousQueue<>();
            StringBuilder debugInfo = new StringBuilder();
            StringBuilder errInfo = new StringBuilder();
            WorkingEnv wr = createWorkingDir(prjId);
            boolean isValid = false;
            if(restart!=null && restart){
                shutdownWorkingEnv(wr);
            }else{
                isValid = isWorkingEnvValid(wr);
                log.info(wr.getWorkdir()+" isValid:" + isValid);
                // check trait;
                if(compareTrait(wr,prjId)){
                    isValid=false;
                    log.info(wr.getWorkdir()+" trait comparison:" + isValid);
                }
            }

            if (!isValid) {
                try {
                    wr.clear();
                    String dir = workdir + prj.getWorkDir();
                    String cmd = nodejs + "npm run dev -- --host 0.0.0.0";
                    Process p = OsType.runCmd(cmd, new File(dir));
                    if (p == null) {
                        destroyWorkingEnv(wr);
                        return R.fail("unable to run dev process");
                    }
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
            } else {
                return R.ok(wr.runningEnv);
            }

            try {
                String ss = runningPort.poll(10, TimeUnit.SECONDS);
                if (ss != null) {
                    wr.runningEnv = ss;
                    Number pid = getProcessPid(wr.devProcess.getProc());
                    if(pid!=null) {
                        wr.setPid(pid.longValue());
                        setTrait(wr,prjId);
                        persistWorkingEnv(wr);
                    }
                    return R.ok(ss);
                } else {
                    destroyWorkingEnv(wr);
                    return R.ok(errInfo.toString());
                }
            } catch (InterruptedException e) {

            }

            return R.fail(debugInfo + "|" + errInfo);
        }
    }

    private boolean compareTrait(WorkingEnv wr, Long prjId) {
        return !wr.trait.equals(computeTrait(prjId));
    }

    private void setTrait(WorkingEnv wr, Long prjId) {
        wr.trait = computeTrait(prjId);
    }

    private String computeTrait(Long prjId) {
        Fileset f=filesetService.getOne(new LambdaQueryWrapper<Fileset>()
                .eq(Fileset::getBelongtoId, prjId)
                .eq(Fileset::getBelongtoType, Constants.FILE_TYPE_PROJECT_EXTRA)
                .eq(Fileset::getPath, "package.json"));
        if(f!=null)
            return f.getContent();
        else
            return "";
    }

    public static Number getProcessPid(Process p) {
        if (p.getClass().getName().equals("java.lang.UNIXProcess"))
            try {
                return (Number) FieldUtils.readDeclaredField(p, "pid", true);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        return null;
    }

    public R<String> dryRun(Long prjId){
        return R.ok();
    }

    public R<String> deploy(Long prjId){
        return R.ok();
    }


    public void compilePages(Long prjId){
        synchronized (prjId.toString()) {
            log.info("compile project:" + prjId);
            UiPageCompositeDTO param = new UiPageCompositeDTO();
            param.setRprjId(prjId);
            List<UiPageCompositeDTO> pages = uiPageCompositeService.selectList(param);
            pages.forEach(p -> {
                Fileset targetFile = filesetService.getOne(new LambdaQueryWrapper<Fileset>().eq(Fileset::getBelongtoId, p.getId())
                        .eq(Fileset::getBelongtoType, Constants.FILE_TYPE_PAGE)
                        .eq(Fileset::getPath, p.getPath()));
                renderPage(p, targetFile);
            });
        }
    }

    public void initNodeModule(Long prjId){
        synchronized (prjId.toString()) {
            WorkingEnv workingEnv = createWorkingDir(prjId);
            log.info("init node_module for project:" + prjId + ", working dir:" + workingEnv.workdir);
            UiPrj prj = uiPrjService.getById(prjId);
            try {
                String nodejs = "nvm use " + prj.getNodejsVer() + "\n";
                String cmd = nodejs + " npm config set registry http://wukong.zjht100.com:10003/ \n npm i";
                Process p = OsType.runCmd(cmd, new File(workingEnv.workdir));
                new ProcessPump(p).start(null, null);
                p.waitFor();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public R<String> inflate(Long prjId){
        synchronized (prjId.toString()) {
            WorkingEnv workingEnv = createWorkingDir(prjId);
            log.info("inflate project:" + prjId + ", working dir:" + workingEnv.workdir);

            List<Fileset> pfiles = filesetService.list(new LambdaQueryWrapper<Fileset>()
                    .eq(Fileset::getBelongtoId, prjId));

            List<UiPage> pages = uiPageService.list(new LambdaQueryWrapper<UiPage>().eq(UiPage::getRprjId, prjId));
            List<Long> ids=pages.stream().map(p->p.getId()).collect(Collectors.toList());
            List<Fileset> pfiles2=filesetService.list(new LambdaQueryWrapper<Fileset>()
                    .eq(Fileset::getBelongtoType, Constants.FILE_TYPE_PAGE)
                    .in(Fileset::getBelongtoId,ids));
            pfiles.addAll(pfiles2);

            Set<String> pfSet = pfiles.stream().map(pf -> pf.getPath()).collect(Collectors.toSet());
            File fdir = new File(workingEnv.workdir);
            // clean dir
            if (fdir.exists())
                FileSetUtils.traverseDirDeepNoRoot(fdir.listFiles(), (f) -> {
                    try {
                        String fpath = f.getAbsolutePath().replace(workdir + File.separator, "");
                        if (pfSet.contains(fpath)) {
                            log.info("delete project file:" + fpath);
                            f.delete();
                        }
                    } catch (Exception e) {

                    }
                    return null;
                });

            traversePrjectFiles(prjId, f -> {
                writeFile(fdir.getAbsolutePath(), f);
                return null;
            });

            return R.ok();
        }
    }

    public void renderRoute(Long prjId) {
        synchronized (prjId.toString()) {
            Fileset sf = filesetService.getOne(new LambdaQueryWrapper<Fileset>()
                    .eq(Fileset::getBelongtoId, prjId).eq(Fileset::getBelongtoType, Constants.FILE_TYPE_PROJECT_ROUTE));

            if (sf == null) {
                throw new RuntimeException("prj has no route file:" + prjId);
            }

            List<UiPage> pages = uiPageService.list(new LambdaQueryWrapper<UiPage>().eq(UiPage::getRprjId, prjId));
            StaticRoutor sr = new StaticRoutor();

            if (StringUtils.isEmpty(sf.getPath())) {
                throw new RuntimeException("route file has no path:" + sf.getId() + ", prj id:" + prjId);
            }

            // 计算路径有几个父目录
            String[] p1 = sf.getPath().split("/");
            String[] p2 = sf.getPath().split("\\\\");
            int actual = Math.max(p1.length, p2.length) - 1;
            StringBuilder pfx = new StringBuilder();
            for (int i = 0; i < actual; i++) {
                pfx.append("../");
            }
            pages.forEach(p -> {
                if (StringUtils.isNotBlank(p.getRoute())) {
                    RoutingInfoInternal ri = JsonUtilUnderline.readValue(p.getRoute(), RoutingInfoInternal.class);
                    ri.component = "() => import('" + pfx + ri.component + "')";
                    sr.routes.add(ri);
                }
            });
            StringBuilder rf = new StringBuilder();
            rf.append("import { createRouter, createWebHistory } from 'vue-router'\n");
            rf.append("const router=createRouter(\n").append(NoQuotesJsonUtils.toJson(sr)).append(")\n");
            rf.append("export default router");
            if (sf == null) {
                UiPrj prj = uiPrjService.getById(prjId);
                sf = new Fileset();
                sf.setBelongtoId(prjId);
                sf.setPath("src/router/index.ts");
                sf.setBelongtoType(Constants.FILE_TYPE_PROJECT_ROUTE);
                sf.setStorageType(prj.getStorageType());
                sf.setStatus(Constants.STATUS_CONFIGURE);
            }
            sf.setContent(rf.toString());
            filesetService.saveOrUpdate(sf);
        }
    }

    private void writeFile(String workdir, Fileset f){
        String af=workdir+File.separator+FileSetUtils.translatePath(f.getPath());
        File rf = new File(af);
        if(rf.exists()){
            String oldContent = FileUtil.readString(af, Charset.forName("utf8"));
            if(oldContent.trim().equals(f.getContent().trim()))
                return;
        }else{
            rf.getParentFile().mkdirs();
        }
        log.info("file updated:"+f.getPath());
        FileUtil.writeString(f.getContent(),rf, Charset.forName("utf8"));
    }


    private static String sanitizeScriptTag(String tag){
        return tag;
    }

    private void renderPage(UiPageCompositeDTO page, Fileset fFile){

        if(fFile==null)
            return;
        StringBuilder pf=new StringBuilder();
        Map<String, String> vueParts = ScriptUtils.parseVueFile(fFile.getContent());

        // 加入template部分
        pf.append(vueParts.get("templateTag")).append("\n");
        pf.append(vueParts.get("template")).append("\n").append("</template>").append("\n");

        // 加入 component的script部分
        log.info("page component size:"+(page.getPageIdUiComponentList()==null?0:page.getPageIdUiComponentList().size()));

        Map<String, StringBuilder> scripts=new HashMap<>();

        page.getPageIdUiComponentList().forEach(c->{
            String ss = c.getPluginScript();
            if(!StringUtils.isEmpty(ss)) {
                Map<String, String> mapping = ScriptUtils.readProperties(c.getPluginData());
                if(!mapping.isEmpty()) {
                    StrSubstitutor sub = new StrSubstitutor(mapping);
                    ss = sub.replace(ss);
                }
                Map<String, String> ssParts = ScriptUtils.parseScript(ss);
                String ssActural = ssParts.get("script");
                if(StringUtils.isNotEmpty(ssActural)){
                    String scriptTag = ssParts.get("scriptTag");
                    scriptTag=sanitizeScriptTag(scriptTag);
                    StringBuilder sb = scripts.getOrDefault(scriptTag,new StringBuilder());
                    sb.append("\n").append(ssActural);
                    scripts.put(scriptTag,sb);
                }
            }
        });

        scripts.forEach((tag,script)->{
            Pair<String, List<String>> sParts = ScriptUtils.parseImports(script.toString());
            List<String> imports = ScriptUtils.mergeImports(sParts.getValue());
            pf.append(tag).append("\n");
            imports.forEach(i->{
                pf.append(i).append("\n");
            });
            pf.append(sParts.getKey());
            pf.append("\n</script>").append("\n");
        });


        // 处理事件
        page.getPageIdUiComponentList().forEach(c->{
            if(c.getComponentIdUiEventHandleList()!=null) {
                c.getComponentIdUiEventHandleList().forEach(eh->{
                    List<Script> ss= JsonUtilUnderline.parseArray(eh.getContent(),Script.class);
                    if(ss!=null) {
                        ss.forEach(s -> {
                            if(Constants.SCRIPT_FRONT.equals(s.getType())){
                                int splitIdx = s.getMethod().indexOf("(");
                                String methodName=splitIdx==-1?s.getMethod():s.getMethod().substring(0,splitIdx);
                                String paramLst=splitIdx==-1?"()":s.getMethod().substring(splitIdx+1,s.getMethod().length()-1);
                                pf.append("const ").append(methodName).append("= function").append(paramLst).append("{\n")
                                        .append(s.getContent()).append("\n}").append("\n");
                            };
                        });
                    }
                });
            }
        });

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

        UiPrj prj = uiPrjService.getById(prjId);

        FileSetUtils.traverseDirDeepNoRoot(new File(path).listFiles(),f->{
            if(f.isDirectory())
                return f;
           String rPath=FileSetUtils.translatePath(f.getPath().substring(path.length()+1));
           if(rPath.startsWith("node_modules")||rPath.startsWith("dist"))
               return f;
           if(rPath.startsWith("src/views"))
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

        return R.ok();
    }


    private <T> List<T> traversePrjectFiles(Long prjId, Function<Fileset,T> func){
        List<Fileset> pfiles = filesetService.list(new LambdaQueryWrapper<Fileset>()
                .ne(Fileset::getPath, Constants.FILE_TYPE_PROJECT_NODE_MODULE)
                .eq(Fileset::getBelongtoId, prjId));

        List<T> ret=new ArrayList<>();
        pfiles.forEach(f->{
            T c=func.apply(f);
            ret.add(c);
        });
        return ret;
    }

    private WorkingEnv createWorkingDir(Long prjId){
        synchronized (prjId.toString()) {
            WorkingEnv wr = workingDirs.get(prjId);
            if (wr == null) {
                Object wrData = redisTemplate.opsForHash().get(Constants.VITE_IN_RUNNING, prjId + "");
                if (wrData != null) {
                    wr = JsonUtilUnderline.parse(wrData.toString(), WorkingEnv.class);
                    workingDirs.put(prjId, wr);
                    return wr;
                }
            }
            if (wr == null) {
                UiPrj prj = uiPrjService.getById(prjId);
                wr = new WorkingEnv();
                wr.setWorkdir(sanitizeWorkDir(workdir + prj.getWorkDir()));
                wr.setPrjId(prjId);
                new File(wr.workdir).mkdirs();
                workingDirs.put(prjId, wr);
            }
            return wr;
        }
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

        System.out.println("ff\\ff".split("\\\\").length);
    }

}
