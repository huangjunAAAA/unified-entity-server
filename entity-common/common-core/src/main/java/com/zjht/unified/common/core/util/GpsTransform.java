package com.zjht.unified.common.core.util;


import com.wukong.core.weblog.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class GpsTransform {

    static String api = "http://gps.market.alicloudapi.com/gps-transform";


    public static PointInfo trans(String appcode, Double x, Double y, String from ,String to){
        ArrayList<PointInfo> pointInfos = new ArrayList<>();
        pointInfos.add(new PointInfo(x,y));
        List<PointInfo> ret = trans(appcode, pointInfos, from, to);
        return ret.get(0);
    }
    public static List<PointInfo> trans(String appcode, List<PointInfo> pointInfos, String from ,String to){

        String location = "";
        for(PointInfo p:pointInfos){
            location+=";"+p.getLon()+","+p.getLat();
        }
        location = location.substring(1);
        Headers.Builder builder = new Headers.Builder();
        builder.add("Authorization", "APPCODE " + appcode);
        String url = api+"?from="+from+"&to="+to+"&location="+location;
        String s = HttpUtils.get(url, builder.build(),null).getMsg();
        log.info("gps transform url:"+url+" ,  ret:"+s);
        ResOutPut outPut = JsonUtil.parse(s,ResOutPut.class);
        List<PointInfo> lst = new ArrayList<>();
        outPut.showapi_res_body.resultList.stream().forEach(item->{
            PointInfo p = new PointInfo();
            p.setLon(item.output.get(0));
            p.setLat(item.output.get(1));
            lst.add(p);
        });

        return lst;
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PointInfo {

        /**
         * 经度
         */
        private Double lon;
        /**
         * 纬度
         */
        private Double lat;
    }

    public static class ResOutPut {

        public ReBbody showapi_res_body;

        public static class ReBbody{
            public List<ResultList> resultList;
        }

        public static class ResultList{
            public List<Double> input;
            public List<Double> output;
        }
    }


}
