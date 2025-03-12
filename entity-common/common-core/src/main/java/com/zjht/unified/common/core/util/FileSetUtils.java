package com.zjht.unified.common.core.util;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileSetUtils {

    public static <T> List<T> traverseDirDeepNoRoot(File[] files,Function<File,T> func){
        List<T> ret=new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            List<T> ss = traverseDirDeep(files[i], func);
            ret.addAll(ss);
        }
        return ret;
    }

    public static <T> List<T> traverseDirDeep(File root, Function<File,T> func){
        List<T> ret=new ArrayList<>();
        if(root==null)
            return ret;
        if(!root.isDirectory()){
            T r = func.apply(root);
            ret.add(r);
            return ret;
        }

        File[] subFiles = root.listFiles();
        List<T> slst = traverseDirDeepNoRoot(subFiles, func);
        ret.addAll(slst);
        T rr=func.apply(root);
        ret.add(rr);
        return ret;
    }

    public static String translatePath(String path){
        Character ss=File.separatorChar=='\\'?'/':'\\';
        return path.replace(ss,File.separatorChar);
    }

    public static void main(String[] args) {
        String p="F:/work/test2";
        System.out.println(translatePath(p));
    }

    public static Long getDirSize(String path){
        if(!new File(path).exists())
            return null;
        try {
            BasicFileAttributes attr = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
            long ss = attr.size();
            return ss;
        }catch (Exception e){

        }
        return null;
    }

}
