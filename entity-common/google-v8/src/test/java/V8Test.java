import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.Swc4jAst;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstBinExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstStmt;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class V8Test {


    private static final IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>();


    public static void main(String[] args) throws Exception {
// Create an instance of swc4j.
        Swc4j swc4j = new Swc4j();
// Prepare a TypeScript code snippet.
        String code =
                "const i=6;\n" +
                        "let j=i+7;\n" +
                        "j+8+test();" +
                        "if(j>5)\n" +
                        "  i+j;\n" +
                        "\n" +
                        "function test(){\n" +
                        "  return i+j;\n" +
                        "}\n" +
                        "\n" +
                        "const tet=function(){\n" +
                        "  return i+j;\n" +
                        "}" ;
        Map<String, TsBlock> tsBlocks = parseTScript( code);
    }

    private static Map<String, TsBlock> parseTScript(String code) throws Exception {
        Swc4j swc4j = new Swc4j();
        URL specifier = new URL("file:///"+UUID.randomUUID().toString()+".ts");
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                // Set capture ast.
                .setCaptureAst(true)
                .setParseMode(Swc4jParseMode.Module);
        Swc4jParseOutput output = swc4j.parse(code, options);
        ISwc4jAstProgram<? extends ISwc4jAst> ast = output.getProgram();
        String script = code;
        LinkedHashMap<String, TsBlock> tsBlocks = new LinkedHashMap<>();
        for (ISwc4jAst block : ast.getBody()) {
            TsBlock tsBlock = new TsBlock();
            tsBlock.setDeclVars(getHeadSymbol(block, script));
            tsBlock.setBody(getSym(block.getSpan(), script));
            detectDepsForStmt(block, tsBlock.getDeps(),script);
            for (Iterator<String> iterator = tsBlock.getDeclVars().iterator(); iterator.hasNext(); ) {
                String varName =  iterator.next();
                tsBlocks.put(varName,tsBlock);
            }
        }
        return tsBlocks;
    }

    private static List<String> getHeadSymbol(ISwc4jAst stmt,String script){
        List<String> heads=new ArrayList<>();
        if(stmt instanceof Swc4jAstVarDecl) {
            Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) stmt;
            for (Swc4jAstVarDeclarator varDeclarator : varDecl.getDecls()) {
                String varName = getSym(varDeclarator.getName().getSpan(), script);
                heads.add(varName);
            }
        }else if(stmt instanceof Swc4jAstFnDecl){
            Swc4jAstFnDecl fnDecl = (Swc4jAstFnDecl) stmt;
            heads.add(fnDecl.getIdent().getSym());
        }
        return heads;
    }

    private static void detectDepsForExpr(ISwc4jAstExpr expr, List<String> deps,String script){
        if(expr instanceof Swc4jAstBinExpr){
            Swc4jAstBinExpr binExpr = (Swc4jAstBinExpr) expr;
            detectDepsForExpr(binExpr.getLeft(), deps, script);
            detectDepsForExpr(binExpr.getRight(), deps, script);
        }
    }

    private static void detectDepsForStmt(ISwc4jAst stmt, List<String> deps,String script){
        if(stmt instanceof Swc4jAstBlockStmt){
            Swc4jAstBlockStmt blockStmt = (Swc4jAstBlockStmt) stmt;
            for (ISwc4jAstStmt subStmt : blockStmt.getStmts()) {
                detectDepsForStmt(subStmt, deps, script);
            }
        }else if(stmt instanceof Swc4jAstVarDecl){
            Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) stmt;
            for (Swc4jAstVarDeclarator varDeclarator : varDecl.getDecls()) {
                Optional<ISwc4jAstExpr> init = varDeclarator.getInit();
                if(init==null || !init.isPresent())
                    continue;
                if(init.get() instanceof Swc4jAstArrowExpr)
                    continue;
                if(init.get() instanceof Swc4jAstFunction)
                    continue;
                detectDepsForExpr(init.get(),deps,script);
            }
        }else if(stmt instanceof Swc4jAstIfStmt){
            Swc4jAstIfStmt ifStmt = (Swc4jAstIfStmt) stmt;
            ISwc4jAstExpr test = ifStmt.getTest();
            detectDepsForExpr(test, deps,script);
            ISwc4jAstStmt continueBlock = ifStmt.getCons();
            detectDepsForStmt(continueBlock, deps, script);
        }else if(stmt instanceof Swc4jAstExprStmt){
            Swc4jAstExprStmt exprStmt = (Swc4jAstExprStmt) stmt;
            ISwc4jAstExpr expr=exprStmt.getExpr();
            detectDepsForExpr(expr, deps,script);
        }
    }

    private static String getSym(Swc4jSpan  span, String script){
        return script.substring(span.getStart(), span.getEnd());
    }
}
