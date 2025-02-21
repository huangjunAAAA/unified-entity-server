package com.zjht.unified.common.core.util;



import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListExtractionUtils<T,K> {
    public List<T> extractNew(List<T> newLst, List<T> oldLst, Function<T,K> id){
        if(oldLst==null){
            return newLst;
        }
        if(CollectionUtils.isEmpty(newLst))
            return null;
        Map<K, T> oldMap = oldLst.stream().collect(Collectors.toMap(t -> id.apply(t), Function.identity()));
        List<T> copy=new ArrayList<>(newLst);
        List<T> nList = copy.stream().filter(t -> id.apply(t) == null || !oldMap.containsKey(id.apply(t))).collect(Collectors.toList());
        return nList;
    }

    public List<T> extractUpdate(List<T> newLst, List<T> oldLst, Function<T,K> id){
        if(oldLst==null)
            return new ArrayList<>();
        if(CollectionUtils.isEmpty(newLst))
            return null;
        Map<K, T> oldMap = oldLst.stream().collect(Collectors.toMap(t -> id.apply(t), Function.identity()));
        List<T> copy=new ArrayList<>(newLst);
        List<T> nList = copy.stream().filter(t -> id.apply(t) != null && oldMap.containsKey(id.apply(t))).collect(Collectors.toList());
        return nList;
    }

    public List<K> extractDel(List<T> newLst, List<T> oldLst, Function<T,K> id){
        if(oldLst==null)
            return new ArrayList<>();
        Map<K, T> oldMap = oldLst.stream().collect(Collectors.toMap(t -> id.apply(t), Function.identity()));
        if(!CollectionUtils.isEmpty(newLst)) {
            newLst.forEach(t -> {
                K tid = id.apply(t);
                oldMap.remove(tid);
            });
        }
        return new ArrayList<K>(oldMap.keySet());
    }

    public List<T> extractDelObj(List<T> newLst, List<T> oldLst, Function<T,K> id){
        if(oldLst==null)
            return new ArrayList<>();
        Map<K, T> oldMap = oldLst.stream().collect(Collectors.toMap(t -> id.apply(t), Function.identity()));
        if(!CollectionUtils.isEmpty(newLst)) {
            newLst.forEach(t -> {
                K tid = id.apply(t);
                oldMap.remove(tid);
            });
        }
        return new ArrayList<T>(oldMap.values());
    }
}
