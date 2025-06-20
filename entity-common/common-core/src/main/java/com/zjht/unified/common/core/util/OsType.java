package com.zjht.unified.common.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;

@Slf4j
public class OsType {

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String getOSName(){
        if(isWindows())
            return "windows";
        else
            return "linux";
    }

    public static Process runCmd(String cmd, File dir){
        try {
            String[] run = null;
            if (isWindows()) {
                run = new String[]{"powershell", "-command", cmd};
            } else {
                run = new String[]{"/bin/bash", "-c", ". ~/.nvm/nvm.sh\n"+cmd};
            }
            log.info("run cmd@"+dir.getAbsolutePath()+", "+ Arrays.toString(run));
            return Runtime.getRuntime().exec(run,null,dir);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }
}
