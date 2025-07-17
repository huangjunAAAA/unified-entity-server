package com.zjht.unified.jsengine.v8.utils;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClassProp;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstComputedPropName;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstFunction;
import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstKeyValueProp;
import com.caoccao.javet.swc4j.ast.expr.*;
import com.caoccao.javet.swc4j.ast.expr.lit.*;
import com.caoccao.javet.swc4j.ast.interfaces.*;
import com.caoccao.javet.swc4j.ast.module.Swc4jAstExportDecl;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstAssignPatProp;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstBindingIdent;
import com.caoccao.javet.swc4j.ast.pat.Swc4jAstObjectPat;
import com.caoccao.javet.swc4j.ast.stmt.*;
import com.caoccao.javet.swc4j.ast.ts.Swc4jAstTsTypeRef;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.span.Swc4jSpan;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.*;

public class TsBlockParser {

    public static Map<String, TsBlock> parseTScript(String code) throws Exception {
        Swc4j swc4j = new Swc4j();
        URL specifier = new URL("file:///"+ UUID.randomUUID().toString()+".ts");
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
            List<String> vars = getHeadSymbol(block, script);
            tsBlock.setDeclVars(vars);
            tsBlock.setBody(getSym(block.getSpan(), script));
            detectDepsForStmt(block, tsBlock.getDeps(),script);
            if(CollectionUtils.isEmpty(tsBlock.getDeclVars())){
                tsBlocks.put(tsBlock.getBody(), tsBlock);
            }else {
                for (Iterator<String> iterator = tsBlock.getDeclVars().iterator(); iterator.hasNext(); ) {
                    String varName = iterator.next();
                    tsBlocks.put(varName, tsBlock);
                }
            }
        }
        return tsBlocks;
    }

    public static List<String> getHeadSymbol(ISwc4jAst stmt,String script){
        List<String> heads=new ArrayList<>();
        if(stmt instanceof Swc4jAstVarDecl) {
            Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) stmt;
            for (Swc4jAstVarDeclarator varDeclarator : varDecl.getDecls()) {
                ISwc4jAstPat name = varDeclarator.getName();
                if (name instanceof Swc4jAstBindingIdent){
                    String varName = ((Swc4jAstBindingIdent) name).getId().getSym();
                    heads.add(varName);
                }else if(name instanceof Swc4jAstObjectPat){
                    Swc4jAstObjectPat objectPat = (Swc4jAstObjectPat) name;
                    for (ISwc4jAstObjectPatProp objectPatProp : objectPat.getProps()) {
                        if(objectPatProp instanceof Swc4jAstAssignPatProp){
                            Swc4jAstAssignPatProp assignPatProp = (Swc4jAstAssignPatProp) objectPatProp;
                            String varName = assignPatProp.getKey().getId().getSym();
                            heads.add(varName);
                        }
                    }
                }
            }
        }else if(stmt instanceof Swc4jAstFnDecl){
            Swc4jAstFnDecl fnDecl = (Swc4jAstFnDecl) stmt;
            heads.add(fnDecl.getIdent().getSym());
        }else if(stmt instanceof Swc4jAstClassDecl){
            Swc4jAstClassDecl classDecl = (Swc4jAstClassDecl) stmt;
            heads.add(classDecl.getIdent().getSym());
        }
        return heads;
    }

    private static void detectDepsForExpr(ISwc4jAstExpr expr, List<String> deps, String script) {
        if (expr instanceof Swc4jAstBinExpr) {
            // 二元运算表达式，如：a + b 或 x > 5
            Swc4jAstBinExpr binExpr = (Swc4jAstBinExpr) expr;
            detectDepsForExpr(binExpr.getLeft(), deps, script);
            detectDepsForExpr(binExpr.getRight(), deps, script);
        } else if (expr instanceof Swc4jAstIdent) {
            // 标识符引用，如：x 或 console
            Swc4jAstIdent ident = (Swc4jAstIdent) expr;
            deps.add(ident.getSym());
        } else if (expr instanceof Swc4jAstCallExpr) {
            // 函数调用，如：func() 或 obj.method()
            Swc4jAstCallExpr callExpr = (Swc4jAstCallExpr) expr;
            if(callExpr.getCallee() instanceof Swc4jAstIdent){
                deps.add(((Swc4jAstIdent) callExpr.getCallee()).getSym());
            }else{
                detectDepsForStmt(callExpr.getCallee(), deps, script);
            }
            callExpr.getArgs().forEach(arg -> detectDepsForExpr(arg.getExpr(), deps, script));
            if(callExpr.getTypeArgs().isPresent()){
                callExpr.getTypeArgs().get().getParams().forEach(param -> detectDepsForTypeAnn(param, deps, script));
            }
        } else if (expr instanceof Swc4jAstMemberExpr) {
            // 成员访问，如：obj.prop 或 arr[0]
            Swc4jAstMemberExpr memberExpr = (Swc4jAstMemberExpr) expr;
            detectDepsForExpr(memberExpr.getObj(), deps, script);
            detectDepsForStmt(memberExpr.getProp(), deps, script);
        } else if (expr instanceof Swc4jAstUnaryExpr) {
            // 一元运算，如：!true 或 -x
            Swc4jAstUnaryExpr unaryExpr = (Swc4jAstUnaryExpr) expr;
            detectDepsForExpr(unaryExpr.getArg(), deps, script);
        } else if (expr instanceof Swc4jAstArrowExpr) {
            // 箭头函数，如：() => x 或 (a) => { return a + 1 }
        } else if (expr instanceof Swc4jAstFunction) {
            // 函数表达式，如：function() { return x; }
        } else if (expr instanceof Swc4jAstArrayLit) {
            // 数组字面量，如：[1, 2, 3] 或 [x, y]
            Swc4jAstArrayLit arrayLit = (Swc4jAstArrayLit) expr;
            arrayLit.getElems().forEach(element -> {
                if (element.isPresent()) {
                    Swc4jAstExprOrSpread el = element.get();
                    detectDepsForExpr(el.getExpr(), deps, script);
                }
            });
        } else if (expr instanceof Swc4jAstObjectLit) {
            // 对象字面量，如：{ key: value } 或 { a, b }
            Swc4jAstObjectLit objectLit = (Swc4jAstObjectLit) expr;
            objectLit.getProps().forEach(prop -> {
                if (prop instanceof ISwc4jAstExpr) {
                    detectDepsForExpr((ISwc4jAstExpr) prop, deps, script);
                } else if (prop instanceof Swc4jAstKeyValueProp) {
                    detectDepsForExpr(((Swc4jAstKeyValueProp) prop).getValue(), deps, script);
                } else if (prop instanceof Swc4jAstSpreadElement) {
                    detectDepsForExpr(((Swc4jAstSpreadElement) prop).getExpr(), deps, script);
                }
            });
        } else if (expr instanceof Swc4jAstAssignExpr) {
            // 赋值表达式，如：a = 5 或 obj.prop = x
            Swc4jAstAssignExpr assignExpr = (Swc4jAstAssignExpr) expr;
            detectDepsForExpr(assignExpr.getRight(), deps, script);
        } else if (expr instanceof Swc4jAstAwaitExpr) {
            // await表达式，如：await promise
            Swc4jAstAwaitExpr awaitExpr = (Swc4jAstAwaitExpr) expr;
            detectDepsForExpr(awaitExpr.getArg(), deps, script);
        } else if (expr instanceof Swc4jAstCondExpr) {
            // 条件表达式，如：x ? y : z
            Swc4jAstCondExpr condExpr = (Swc4jAstCondExpr) expr;
            detectDepsForExpr(condExpr.getTest(), deps, script);
            detectDepsForExpr(condExpr.getCons(), deps, script);
            detectDepsForExpr(condExpr.getAlt(), deps, script);
        } else if (expr instanceof Swc4jAstClassExpr) {
            // 类表达式，如：class { }
            Swc4jAstClassExpr classExpr = (Swc4jAstClassExpr) expr;
            classExpr.getChildNodes().forEach(memberExpr -> {
                detectDepsForStmt(memberExpr, deps, script);
            });
        } else if (expr instanceof Swc4jAstFnExpr) {
            // 函数表达式，如：function foo() { }
        } else if (expr instanceof Swc4jAstMetaPropExpr) {
            // 元属性，如：new.target
        } else if (expr instanceof Swc4jAstNewExpr) {
            // new表达式，如：new Class()
            Swc4jAstNewExpr newExpr = (Swc4jAstNewExpr) expr;
            detectDepsForExpr(newExpr.getCallee(), deps, script);
            if(newExpr.getArgs().isPresent())
                newExpr.getArgs().get().forEach(arg -> detectDepsForExpr(arg.getExpr(), deps, script));
        } else if (expr instanceof Swc4jAstOptChainExpr) {
            // 可选链，如：obj?.prop
            Swc4jAstOptChainExpr optChainExpr = (Swc4jAstOptChainExpr) expr;
            detectDepsForStmt(optChainExpr.getBase(), deps, script);
        } else if (expr instanceof Swc4jAstParenExpr) {
            // 括号表达式，如：(x + y)
            Swc4jAstParenExpr parenExpr = (Swc4jAstParenExpr) expr;
            detectDepsForExpr(parenExpr.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstSpreadElement) {
            // 展开元素，如：[...arr]
            Swc4jAstSpreadElement spreadElement = (Swc4jAstSpreadElement) expr;
            detectDepsForExpr(spreadElement.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstSuperPropExpr) {
            // super属性，如：super.method()
        } else if (expr instanceof Swc4jAstTaggedTpl) {
            // 带标签的模板，如：tag`text`
            Swc4jAstTaggedTpl taggedTpl = (Swc4jAstTaggedTpl) expr;
            detectDepsForExpr(taggedTpl.getTag(), deps, script);
            detectDepsForExpr(taggedTpl.getTpl(), deps, script);
        } else if (expr instanceof Swc4jAstTpl) {
            // 模板字符串，如：`text`
            Swc4jAstTpl tpl = (Swc4jAstTpl) expr;
            tpl.getExprs().forEach(e -> detectDepsForExpr(e, deps, script));
        } else if (expr instanceof Swc4jAstThisExpr) {
            // this关键字，如：this
        } else if (expr instanceof Swc4jAstTsAsExpr) {
            // 类型断言，如：value as Type
            Swc4jAstTsAsExpr tsAsExpr = (Swc4jAstTsAsExpr) expr;
            detectDepsForExpr(tsAsExpr.getExpr(), deps, script);
            detectDepsForTypeAnn(tsAsExpr.getTypeAnn(), deps, script);
        } else if (expr instanceof Swc4jAstTsNonNullExpr) {
            // 非空断言，如：value!
            Swc4jAstTsNonNullExpr tsNonNullExpr = (Swc4jAstTsNonNullExpr) expr;
            detectDepsForExpr(tsNonNullExpr.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstTsTypeAssertion) {
            // 类型转换，如：<Type>value
            Swc4jAstTsTypeAssertion tsTypeAssertion = (Swc4jAstTsTypeAssertion) expr;
            detectDepsForTypeAnn(tsTypeAssertion.getTypeAnn(), deps, script);
            detectDepsForExpr(tsTypeAssertion.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstTsInstantiation) {
            // 泛型实例化，如：func<Type>
            Swc4jAstTsInstantiation tsInstantiation = (Swc4jAstTsInstantiation) expr;
            detectDepsForExpr(tsInstantiation.getExpr(), deps, script);
            tsInstantiation.getTypeArgs().getParams().forEach(typeArg -> detectDepsForTypeAnn(typeArg, deps, script));
        } else if (expr instanceof Swc4jAstTsSatisfiesExpr) {
            // satisfies操作符，如：value satisfies Type
            Swc4jAstTsSatisfiesExpr tsSatisfiesExpr = (Swc4jAstTsSatisfiesExpr) expr;
            detectDepsForExpr(tsSatisfiesExpr.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstTsConstAssertion) {
            // const断言，如：as const
            Swc4jAstTsConstAssertion tsConstAssertion = (Swc4jAstTsConstAssertion) expr;
            detectDepsForExpr(tsConstAssertion.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstUpdateExpr) {
            // 更新操作，如：x++ 或 --y
            Swc4jAstUpdateExpr updateExpr = (Swc4jAstUpdateExpr) expr;
            detectDepsForExpr(updateExpr.getArg(), deps, script);
        } else if (expr instanceof Swc4jAstYieldExpr) {
            // yield表达式，如：yield value
        } else if (expr instanceof Swc4jAstJsxElement) {
            // JSX元素，如：<Component />
        } else if (expr instanceof Swc4jAstJsxEmptyExpr) {
            // JSX空表达式
        } else if (expr instanceof Swc4jAstJsxExprContainer) {
            // JSX表达式容器，如：{expr}
        } else if (expr instanceof Swc4jAstJsxFragment) {
            // JSX片段，如：<>...</>
        } else if (expr instanceof Swc4jAstJsxMemberExpr) {
            // JSX成员引用，如：Component.SubComponent
        } else if (expr instanceof Swc4jAstJsxNamespacedName) {
            // JSX命名空间名称，如：ns:Component
        } else if (expr instanceof Swc4jAstJsxSpreadChild) {
            // JSX展开子元素，如：{...props}
            Swc4jAstJsxSpreadChild jsxSpreadChild = (Swc4jAstJsxSpreadChild) expr;
            detectDepsForExpr(jsxSpreadChild.getExpr(), deps, script);
        } else if (expr instanceof Swc4jAstJsxText) {
            // JSX文本节点
        } else if (expr instanceof Swc4jAstNull || expr instanceof Swc4jAstBool || expr instanceof Swc4jAstNumber || expr instanceof Swc4jAstStr || expr instanceof Swc4jAstBigInt) {
            // 原始字面量（null, boolean, number, string, bigint）不引入依赖
        } else if (expr instanceof Swc4jAstExprOrSpread) {
            // 表达式或展开，如：...arr
            Swc4jAstExprOrSpread exprOrSpread = (Swc4jAstExprOrSpread) expr;
            detectDepsForExpr(exprOrSpread.getExpr(), deps, script);
        } else {
            // 未知表达式类型，可抛出异常或记录日志
            System.err.println("Unsupported expression type: " + expr.getClass().getName());
        }
    }

    private static void detectDepsForTypeAnn(ISwc4jAstTsType typeAnn, List<String> deps,String script){
        if(typeAnn instanceof Swc4jAstTsTypeRef){
            String sym = getSym(typeAnn.getSpan(), script);
            deps.add(sym);
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
            detectDepsForDecl(varDecl.getDecls(), deps, script);
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
        }else if(stmt instanceof Swc4jAstUsingDecl){
            Swc4jAstUsingDecl usingDecl = (Swc4jAstUsingDecl) stmt;
            detectDepsForDecl(usingDecl.getDecls(), deps, script);
        }else if(stmt instanceof Swc4jAstExportDecl){
            Swc4jAstExportDecl exportDecl = (Swc4jAstExportDecl) stmt;
            detectDepsForStmt(exportDecl.getDecl(), deps, script);
        }else if(stmt instanceof Swc4jAstMemberExpr){
            Swc4jAstMemberExpr memberExpr = (Swc4jAstMemberExpr) stmt;
            detectDepsForExpr(memberExpr.getObj(), deps, script);
            detectDepsForStmt(memberExpr.getProp(), deps, script);
        }else if(stmt instanceof Swc4jAstComputedPropName){
            Swc4jAstComputedPropName computedPropName = (Swc4jAstComputedPropName) stmt;
            detectDepsForExpr(computedPropName.getExpr(), deps, script);
        }else if(stmt instanceof Swc4jAstClassDecl){
            Swc4jAstClassDecl classDecl = (Swc4jAstClassDecl) stmt;
            classDecl.getClazz().getBody().forEach(member -> detectDepsForStmt(member, deps, script));
            if(classDecl.getClazz().getSuperClass().isPresent()){
                detectDepsForExpr(classDecl.getClazz().getSuperClass().get(), deps, script);
            }
        }else if(stmt instanceof Swc4jAstClassProp){
            Swc4jAstClassProp classProp = (Swc4jAstClassProp) stmt;
            if(classProp.getValue().isPresent()){
                detectDepsForExpr(classProp.getValue().get(), deps, script);
            }
        }
    }

    private static void detectDepsForDecl(List<Swc4jAstVarDeclarator> decls, List<String> deps,String script){
        for (Swc4jAstVarDeclarator varDeclarator : decls) {
            Optional<ISwc4jAstExpr> init = varDeclarator.getInit();
            if(init==null || !init.isPresent())
                continue;
            if(init.get() instanceof Swc4jAstArrowExpr)
                continue;
            if(init.get() instanceof Swc4jAstFunction)
                continue;
            detectDepsForExpr(init.get(),deps,script);
        }
    }

    private static String getSym(Swc4jSpan span, String script){
        return script.substring(span.getStart(), span.getEnd());
    }
}
