package com.zjht.unified.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.wukong.core.weblog.utils.JsonUtil;

import com.zjht.unified.common.core.domain.misc.FieldLocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ObjectFieldLocatorUtil {
    public static JsonNode locate(JsonNode target, List<FieldLocator> locators){
        JsonNode tmp=target;
        for (Iterator<FieldLocator> iterator = locators.iterator(); iterator.hasNext(); ) {
            FieldLocator locator =  iterator.next();
            tmp=tmp.get(locator.getField());
            if(tmp==null)
                return null;
            if(StringUtils.isNotEmpty(locator.getIndex())){
                String[] idxList = locator.getIndex().split(",");
                for (int i = 0; i < idxList.length; i++) {
                    String s = idxList[i];
                    tmp=tmp.get(Integer.parseInt(s));
                }
            }
            if(StringUtils.isNotEmpty(locator.getFilter())){
                boolean find=false;
                Iterator<JsonNode> iter = tmp.elements();
                while(iter.hasNext()){
                    JsonNode tmp2 = iter.next();
                    if(eval(tmp2,locator.getFilter())){
                        find=true;
                        tmp=tmp2;
                        break;
                    }
                }
                if(!find)
                    return null;
            }
        }
        return tmp;
    }

    private static final Pattern VAR =Pattern.compile("([a-zA-Z_]+\\w*)");

    private static boolean eval(JsonNode node,String condition){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("Nashorn");
        try {
            Matcher m = VAR.matcher(condition);
            if(m.find()){
                for (int i = 0; i < m.groupCount(); i++) {
                    String v = m.group(i);
                    JsonNode binding = node.get(v);
                    engine.put(v,binding);
                }
            }
            Boolean ret = (Boolean) engine.eval(condition);
            return ret;
        } catch (ScriptException e) {
            log.error(e.getMessage(),e);
        }
        return false;
    }

    public static JsonNode locate(String json, List<FieldLocator> locators){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return locate(jsonNode,locators);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static List<Map<String,Object>> extractMapList(Object target){
        if(target instanceof List){
            return (List<Map<String, Object>>) target;
        }else if(target instanceof Map){
            for (Iterator iterator = ((Map) target).values().iterator(); iterator.hasNext(); ) {
                Object nextVal =  iterator.next();
                List<Map<String, Object>> vLst = extractMapList(nextVal);
                if(vLst!=null)
                    return vLst;
            }
        }else{
            Map<String, Object> tmap = new ObjectMapper()
                    .convertValue(target, new TypeReference<Map<String, Object>>() {});
            return extractMapList(tmap);
        }
        return null;
    }

    public static void main(String[] args) {
        Sample1 s1=new Sample1();
        s1.s3.add(new Sample3());
        s1.s3.add(new Sample3());
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                s1.s4[i][j]=new Sample4();
            }
        }
        String json = JsonUtil.toJson(s1);
        System.out.println(json);

//        List<FieldLocator> fl1=new ArrayList<>();
//        FieldLocator f11=new FieldLocator("id","int",null,null,null);
//        fl1.add(f11);
//        Object v1 = locate(json, fl1);
//        System.out.println("locator= "+GsonUtil.toJson(fl1)+" , v1= "+v1);
//
//        List<FieldLocator> fl2=new ArrayList<>();
//        FieldLocator f21=new FieldLocator("s2","object",null,null,null);
//        FieldLocator f22=new FieldLocator("s2field","string",null,null,null);
//        fl2.add(f21);
//        fl2.add(f22);
//        Object v2= locate(json,fl2);
//        System.out.println("locator= "+GsonUtil.toJson(fl2)+" , v2= "+v2);

        List<FieldLocator> fl3=new ArrayList<>();
        FieldLocator f31=new FieldLocator("s3","array",1, null,"id==3&&id==3");
        FieldLocator f32=new FieldLocator("s34","object",null,null,null);
        FieldLocator f33=new FieldLocator("s42","object",null,null,null);
        fl3.add(f31);
//        fl3.add(f32);
//        fl3.add(f33);
        Object v3= locate(json,fl3);
        System.out.println("locator= "+JsonUtil.toJson(fl3)+" , v3= "+v3);

//        List<FieldLocator> fl4=new ArrayList<>();
//        FieldLocator f41=new FieldLocator("s4","array",2, Arrays.asList(1,1),null);
//        FieldLocator f42=new FieldLocator("s42","object",null,null,null);
//        FieldLocator f43=new FieldLocator("id","int",null,null,null);
//        fl4.add(f41);
//        fl4.add(f42);
//        fl4.add(f43);
//        Object v4= locate(json,fl4);
//        System.out.println("locator= "+GsonUtil.toJson(fl4)+" , v4= "+v4);
    }

    private static class Sample1{
        private int id=1;
        private Sample2 s2=new Sample2();
        private List<Sample3> s3=new ArrayList<>();
        private Sample4[][] s4=new Sample4[2][2];
    }

    private static class Sample2{
        private int id=2;
        private String s2field="s2val";
    }

    private static class Sample3{
        private int id=3;
        private Sample4 s34=new Sample4();
    }

    private static class Sample4{
        private int id=5;
        private Sample2 s42= new Sample2();
    }
}
