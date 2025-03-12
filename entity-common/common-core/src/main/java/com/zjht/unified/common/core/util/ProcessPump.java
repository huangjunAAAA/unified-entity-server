package com.zjht.unified.common.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

@Slf4j
public class ProcessPump {
    private StreamPump out;
    private StreamPump err;
    private Process proc;
    private Consumer<String> outListener;
    private Consumer<String> errListener;

    public ProcessPump(Process proc) {
        this.proc = proc;
    }

    public Process getProc() {
        return proc;
    }

    public void start(Consumer<String> ol,Consumer<String> el) {
        outListener=ol;
        errListener=el;
        if (proc != null && proc.isAlive()) {
            if (out == null || !out.isAlive()) {
                out = new StreamPump(proc.getInputStream(),false);
                out.start();
            }
            if (err == null || !err.isAlive()) {
                err = new StreamPump(proc.getErrorStream(),true);
                err.start();
            }
        }
    }

    public void stop(){
        if(out!=null&&out.isAlive()){
            out.interrupt();
        }
        if(err!=null&&err.isAlive()){
            err.interrupt();
        }
    }


    private class StreamPump extends Thread {
        private StreamPump(InputStream in,boolean err) {
            this.in = in;
            this.errLevel=err;
        }

        private boolean errLevel;
        private InputStream in;

        @Override
        public void run() {
            while (!Thread.currentThread().interrupted()&&proc.isAlive()) {
                try {
                    String line = null;
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    while (null != (line = br.readLine())) {
                        if (errLevel) {
                            log.error(line);
                            if(errListener!=null){
                                errListener.accept(line);
                            }
                        }else {
                            log.info(line);
                            if(outListener!=null){
                                outListener.accept(line);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }
}
