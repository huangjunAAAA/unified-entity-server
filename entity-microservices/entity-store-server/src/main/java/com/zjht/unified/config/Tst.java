package com.zjht.unified.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tst {
    public static void main(String[] args) {
        String root="F:\\work\\linkis_1.4";
        List<File> files = findFileInDir(new File(root), "distribution.xml");


        StringBuilder pl=new StringBuilder();
        for (Iterator<File> iterator = files.iterator(); iterator.hasNext(); ) {
            File f =  iterator.next();
            String g=getpName(f.getAbsolutePath());
            if(g!=null){
                if(pl.length()>0)
                    pl.append(",");
                pl.append("!:").append(g);
            }
        }
        System.out.println(pl);
    }

    public static String getpName(String path){
        path=path.replace("\\src\\main\\assembly","");
        String[] parts = path.split("\\\\");
        if(path.contains("linkis-engineconn-plugins")){
            if((path.contains("jdbc")||path.contains("spark")||path.contains("hive")||path.contains("python")||path.contains("shell"))&&!path.contains("scala"))
                return "linkis-engineplugin-"+parts[parts.length-1];
            else
                return null;
        }else
            return parts[parts.length-1];
    }


    public static List<File> findFileInDir(File root, String filename){
        if(root==null||!root.isDirectory())
            return new ArrayList<>();
        File[] files = root.listFiles();
        List<File> ret=new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(file.getName().equals(filename)){
                ret.add(root);
            }else{
                List<File> subRet = findFileInDir(file, filename);
                ret.addAll(subRet);
            }
        }
        return ret;
    }
}
