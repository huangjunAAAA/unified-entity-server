import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.swc4j.Swc4j;
import com.zjht.unified.jsengine.v8.utils.TsBlock;
import com.zjht.unified.jsengine.v8.utils.TsBlockParser;


import java.net.URL;
import java.util.*;


public class V8Test {


    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();


    public static void main(String[] args) throws Exception {
// Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
// Prepare a TypeScript code snippet.
        String code ="let x1=tag`这是一个模板字符串，其中可以包含${expression}等表达式`;\n" +
                "x26=<Clsdf>x2;\n" +
                "x27-func<ClsDf>();\n" +
                "let x2=x > 5;\n" +
                "let x3=a+b;\n" +
                "let x4=change4fB7U;\n" +
                "func();\n" +
                "x5.testMethod(x2,x3);\n" +
                "x6=obj.prop;\n" +
                "x7=obje[0];\n" +
                "x8=jb[kk];\n" +
                "x9=-y;\n" +
                "x10=(a) => { return a + 1 }\n" +
                "x11=[c,d,5];\n" +
                "x12={e,f};\n" +
                "x13={g:h}\n" +
                "x14.b=x11;\n" +
                "x16=await func();\n" +
                "x17=a1?a2:a3;\n" +
                "class testClass extends T2Class {\n" +
                "\tt=1+t2;\n" +
                "}\n" +
                "x18=new testClass();\n" +
                "x19=new testClass;\n" +
                "x20=a?.b;\n" +
                "x21=(x+y());\n" +
                "x21={...gg}\n" +
                "x22=[...arr,a1,a2]\n" +
                "x23=`这是一个模板字符串，其中可以包含${expression}等表达式`\n" +
                "x24=c as ClsDf;\n" +
                "x25=ttt!;\n" +
                "const value = 123 satisfies N1; // 验证 value 是否符合 N1 类型，但保留其原始类型\n" +
                "const colors = [\"red\", \"green\", \"blue\"] as const;\n" +
                "x29=i++;\n" +
                "const result = sum1(...numbers);" ;
        Map<String, TsBlock> tsBlocks = TsBlockParser.parseTScript( code);
        for (Map.Entry<String, TsBlock> entry : tsBlocks.entrySet()) {
            TsBlock ts = entry.getValue();
            System.out.println(ts.getBody()+"  ->  "+ts.getDeps());
        }

    }


}
