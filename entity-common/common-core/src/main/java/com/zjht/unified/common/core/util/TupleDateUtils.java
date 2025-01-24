package com.zjht.unified.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.domain.store.TupleDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class TupleDateUtils {

    public static void main(String[] args) {
        String s1="TIME-a1-b2:2019";
        String s2="c1-k1";
        String s="TIME:2019";
        convert(s);
        convert(s1);
        convert(s2);
    }

    public static TupleDate convert(String dt){

        TupleDate ret=new TupleDate();
        if(dt.startsWith("TIME")){
            int mark1 = dt.indexOf(":");
            String groups = dt.substring(0, mark1);

            String timeValue=dt.substring(mark1+1);
            String timePattern=null;
            for (Iterator<String> iterator = Constants.TIME_SLICE.iterator(); iterator.hasNext(); ) {
                String tp =  iterator.next();
                if(timeValue.length()==tp.length()){
                    timePattern=tp;
                    break;
                }
            }
            if(timePattern!=null){
                SimpleDateFormat sdf=new SimpleDateFormat(timePattern);
                try {
                    Date d = sdf.parse(timeValue);
                    return new TupleDate(timePattern,d,new ArrayList<>(Arrays.asList(groups.split("-"))));
                } catch (ParseException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }else{
            String[] parts=dt.split("-");
            ret.setConditions(new ArrayList<>(Arrays.asList(parts)));
        }
        return ret;
    }

    public static String getTimePattern(String slice){
        switch (slice.toLowerCase()){
            case "year":
                return Constants.TIME_SLICE.get(0);
            case "month":
                return Constants.TIME_SLICE.get(0);
            case "day":
                return Constants.TIME_SLICE.get(0);
            case "hour":
                return Constants.TIME_SLICE.get(0);
        }
        return null;
    }

    public static String genTupleString(String timeTag, String timelessTag){
        if(StringUtils.isEmpty(timelessTag))
            return "TIME:"+timeTag;
        return "TIME-"+timelessTag+":"+timeTag;
    }

    public static String genTupleString(List<String> groups){
        return StrUtil.join("-",groups.iterator());
    }

    public static String toString(TupleDate td){
        StringBuilder sb=new StringBuilder();
        if(!StringUtils.isEmpty(td.getTimeslice())||td.getActualDate()!=null){
            sb.append("TIME");
        }
        if(!CollectionUtils.isEmpty(td.getConditions())){
            if(sb.length()>0){
                sb.append("-");
            }
            sb.append(genTupleString(td.getConditions()));
        }
        if(td.getActualDate()!=null){
            SimpleDateFormat sdf=new SimpleDateFormat(td.getTimeslice());
            String pt = sdf.format(td.getActualDate());
            sb.append(":").append(pt);
        }
        return sb.toString();
    }
}
