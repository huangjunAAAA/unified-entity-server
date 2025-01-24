package com.zjht.unified.data.common.core.util.sort;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FieldComparator implements Comparator {

    private List<String> fields;

    private List<String> asc;

    public FieldComparator(String[] fields,String[] asc){
            this.fields=new ArrayList<>();
            this.fields.addAll(Arrays.asList(fields));
            this.asc=new ArrayList<>();
            this.asc.addAll(Arrays.asList(asc));
    }

    @Override
    public int compare(Object o1, Object o2) {
        int nullVal = cmpNull(o1, o2);
        if(nullVal<2)
            return nullVal;

        for (int i = 0; i < fields.size(); i++) {
            String sortOrder="asc";
            if(i<asc.size())
                sortOrder=asc.get(i);
            try{
                Object v1 = FieldUtils.readField(o1, fields.get(i));
                Object v2 = FieldUtils.readField(o2, fields.get(i));
                if(2==cmpNull(v1,v2)){
                    int compared = v1.toString().compareTo(v2.toString());
                    if(compared!=0){
                        if("desc".equalsIgnoreCase(sortOrder)){
                            compared=-compared;
                        }
                        return compared;
                    }
                }
            }catch (Exception e){

            }
        }
        return 0;
    }

    private int cmpNull(Object o1, Object o2){
        if(o1==null&&o2==null)
            return 0;
        if(o1==null)
            return 1;
        if(o2==null)
            return -1;
        return 2;
    }




}
