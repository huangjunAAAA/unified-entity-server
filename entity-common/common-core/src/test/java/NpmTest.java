
import com.zjht.unified.common.core.util.ProcessPump;

import java.io.File;

public class NpmTest {

    public static void main(String[] args) throws Exception {
        Process p = Runtime.getRuntime().exec(new String[]{"cmd","/c","npm i"}, null, new File("f:/tmp/test"));
        ProcessPump pp=new ProcessPump(p);
        pp.start(null,null);
        p.waitFor();
        System.out.println("exit now");
    }
}
