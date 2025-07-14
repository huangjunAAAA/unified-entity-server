import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.expr.Swc4jAstArrowExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstExpr;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDecl;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstVarDeclarator;
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
                "import { updateCfgEntity, listCfgEntity, deleteCfgEntity } from \"@/utils/cfgentity\";\n" +
                "\n" +
                "import { deleteStaticVariable, setStaticVariable, listStaticVariable } from \"@/utils/cfgstatics\";\n" +
                "\n" +
                "import { MethodParam, FieldDef, MethodDef, NNRel, FieldDefExt, BaseClsDef, CslRelationDef, MethodDefExt, MethodParamExt, ClsDf, ClsDfExt } from \"@/utils/svrobjutil\";\n" +
                "\n" +
                "import { ref, onMounted, watch } from \"vue\";\n" +
                "\n" +
                "import { NT_FSM, listDefTree, NT_UNKNOWN, deleteTree, NT_SENTINEL, listTreeRoot, NT_CLAZZ, createEntityByGuid, listEntity, getEntity, createEntity, saveTree, listAllEntityByClsGuid, type UiTreeNode, updateEntity, TNode, deleteEntity, traverseTree, NT_VIEW } from \"@/utils/svrobjutil2\";\n" +
                "\n" +
                "import { listUiPage, AttachmentRelDef, createAttachmentRelDef, AttachmentRelDefExt, updateAttachmentRelDef, AttachmentRelDefCondition, listAttachmentRelDef, deleteAttachmentRelDef } from \"@/utils/attachnodes\";\n" +
                "\n" +
                "import { getCommonDataP, getCommonDataformData, getCommonDataform, getCommonDataG } from \"@/api/commonApi\";\n" +
                "\n" +
                "import { toFsmDef, FsmQueryCondition, getFsmDefInfo, listFsm, FsmCondition, createFsmDef, FsmDef } from \"@/utils/fsm\";\n" +
                "\n" +
                "import { httpForm, httpLogin, httpJson, httpGet } from \"@/utils/httputil\";\n" +
                "\n" +
                "import { updateSentinelDef, listSentinelDef, SentinelDef, deleteSentinel, getSentinelInfo, createSentinelDef, SentinelDefCondition } from \"@/utils/sentinel\";\n" +
                "\n" +
                "import { type  CallBack } from \"@/api/commonApi/type\";\n" +
                "\n" +
                "import { getDomain, setExecPrj, getExecPrj, generateGuid, setDomain, getOperatingPrj, deployUePrj, execBackScript, getOperatingDomain, setOperatingPrj, setOperatingDomain } from \"@/utils/prjenv\";\n" +
                "\n" +
                "import { listClass, getClassInfo, getClassByName, deleteClass, createClass, BaseClsDefCondition, getClassByGuid } from \"@/utils/clsextra\";\n" +
                "\n" +
                "import { listUiPrj, deleteUePrj, linkPrj, UePrj, UiPrjCondition, UiPrj, getUiPrjInfo, listUePrj, UePrjCondition, deleteUiPrj, createUiPrj, createUnifiedPrj, createUePrj, getUePrj } from \"@/utils/prjs\";\n" +
                "\n" +
                "// fsm.ts\n" +
                "\n" +
                "// attachnodes.ts\n" +
                "\n" +
                "// prjenv.ts\n" +
                "\n" +
                "// svrobjutil.ts\n" +
                "\n" +
                "// svrobjutil2.ts\n" +
                "\n" +
                "// prjs.ts\n" +
                "\n" +
                "// cfgentity.ts\n" +
                "\n" +
                "// cfgstatics.ts\n" +
                "\n" +
                "// sentinel.ts\n" +
                "\n" +
                "// clsextra.ts\n" +
                "\n" +
                "const nameAlESGlLP =ref(\"\")\n" +
                "const nameQFnBmJqe =ref(\"\")\n" +
                "const selecto7ck3Q5W =ref(\"\");\n" +
                " const optionso7ck3Q5W =ref([]); \n" +
                "\n" +
                "               \n" +
                "               \n" +
                " \n" +
                "const selectlhFB9bWo =ref(\"\");\n" +
                " const optionslhFB9bWo =ref([]); \n" +
                "\n" +
                "               \n" +
                "               \n" +
                " \n" +
                "const nameTjSz58Ae =ref(\"\")\n" +
                "const namepaOq0Pj9 =ref(\"\")\n" +
                "const selecteT4furbk =ref(\"\");\n" +
                " const optionseT4furbk =ref([]); \n" +
                "const changehAHv7 = async ()=>{\n" +
                "                   let classGuid = selecteT4furbk.value;\n" +
                "    await refreshClass(classGuid);\n" +
                "            }\n" +
                "               \n" +
                " \n" +
                "const selectdpuf1pOu =ref(\"\");\n" +
                " const optionsdpuf1pOu =ref([]); \n" +
                "\n" +
                "               \n" +
                "               \n" +
                " \n" +
                "const selectGhQb5vNf =ref(\"\");\n" +
                " const optionsGhQb5vNf =ref([]); \n" +
                "const changel4BIv = async ()=>{\n" +
                "                   let classGuid = selecteT4furbk.value;\n" +
                "    let methodGuid = selectGhQb5vNf.value;\n" +
                "\n" +
                "    refreshClass(classGuid, methodGuid);\n" +
                "            }\n" +
                "               \n" +
                " \n" +
                "const nameU3XYOAkR =ref(\"\")\n" +
                "const nameL43BkzSj =ref(\"\")\n" +
                "const nameNyFPoz8C =ref(\"\")\n" +
                "const nameiy8QbBp4 =ref(\"\")\n" +
                "const selectakMzpKv8 =ref(\"\");\n" +
                " const optionsakMzpKv8 =ref([]); \n" +
                "\n" +
                "               \n" +
                "               \n" +
                " \n" +
                " const descU44a3st7 =ref(\"\")\n" +
                "const selectcNTpRQ9G =ref(\"\");\n" +
                " const optionscNTpRQ9G =ref([]); \n" +
                "\n" +
                "               \n" +
                "               \n" +
                " //定义所有界面元素的别名\n" +
                "let editIDEUrl = nameAlESGlLP;\n" +
                "let editIDEName = nameQFnBmJqe;\n" +
                "let editCurExecPrj = nameTjSz58Ae;\n" +
                "let editCurOperatingPrj = namepaOq0Pj9;\n" +
                "let dropdownSelectUePrj = selecto7ck3Q5W;\n" +
                "let dropdownOptionUePrj = optionso7ck3Q5W;\n" +
                "let dropdownSelectUiPrj = selectlhFB9bWo;\n" +
                "let dropdownOptionUiPrj = optionslhFB9bWo;\n" +
                "let editClassNumbers = nameU3XYOAkR;\n" +
                "let dropdownSelectClass = selecteT4furbk;\n" +
                "let dropdownOptionClass = optionseT4furbk;\n" +
                "let editFieldNumbers = nameL43BkzSj;\n" +
                "let dropdownSelectField = selectdpuf1pOu;\n" +
                "let dropdownOptionField = optionsdpuf1pOu;\n" +
                "let editMethodNumbers = nameNyFPoz8C;\n" +
                "let dropdownSelectMethod = selectGhQb5vNf;\n" +
                "let dropdownOptionMethod = optionsGhQb5vNf;\n" +
                "let editParaNumbers = nameiy8QbBp4;\n" +
                "let dropdownSelectPara = selectakMzpKv8;\n" +
                "let dropdownOptionPara = optionsakMzpKv8;\n" +
                "let editMethodBody = descU44a3st7;\n" +
                "\n" +
                "//限制条件：调用此函数前，需要确保已经执行过setDomain()\n" +
                "/**\n" +
                " * 异步获取项目列表项，并拼接下拉菜单可使用的显示文本\n" +
                " * \n" +
                " * 此函数用于根据不同的条件获取项目列表每个项目项包含项目的ID和创建时间\n" +
                " * 它通过调用传入的列表获取函数，并根据列表范围条件（全部、用户、IDE）来筛选和格式化项目项\n" +
                " * \n" +
                " * @param getListFunc 一个函数，用于获取项目列表数据: listUePrj 或 listUiPrj\n" +
                " * @param equals 用于精确匹配的查询条件, listUePrj 或 listUiPrj使用的参数\n" +
                " * @param like 用于模糊查询的条件, listUePrj 或 listUiPrj使用的参数\n" +
                " * @param inCondition 用于IN查询的条件, listUePrj 或 listUiPrj使用的参数\n" +
                " * @param listScope 列表范围，可以是\"user\"（默认）、\"all\" 或 \"ide\"，用于确定获取哪些项目\n" +
                " * @param ideID IDE的ID，默认为5，用于区分IDE项目和其他项目\n" +
                " * @returns 返回一个数组，包含格式化后的项目列表项\n" +
                " */\n" +
                "async function getPrjListItem(getListFunc:Function, equals : any, like : any, inCondition : any, listScope:string = \"user\", ideID:int = 5){\n" +
                "    let prjItems = [];\n" +
                "    let result = await getListFunc(equals, 1, 10000, like, inCondition);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取项目列表失败：\" + result.message);\n" +
                "        return prjItems;\n" +
                "    }\n" +
                "    let prjLstPage = result.data;\n" +
                "    \n" +
                "    try{\n" +
                "        if(prjLstPage.length <=0){\n" +
                "            alert(\"当前没有任何项目！\");\n" +
                "            return prjItems;\n" +
                "        }\n" +
                "\n" +
                "        for(let i = 0; i < prjLstPage.length; i++){\n" +
                "            // 对于\"all\"范围且ID小于ideID的项目，标记为保留项目\n" +
                "            if(listScope === \"all\" && prjLstPage[i].id < ideID){\n" +
                "                prjItems.push({\n" +
                "                    name: \"id=\" + prjLstPage[i].id + \"(保留*) | name=\" + prjLstPage[i].name + \" | createTime=\" + prjLstPage[i].createTime,\n" +
                "                    value: prjLstPage[i].id\n" +
                "                })\n" +
                "            }\n" +
                "            // 对于\"ide\"或\"all\"范围且ID等于ideID的项目，标记为IDE项目\n" +
                "            if((listScope === \"ide\" || listScope === \"all\") && prjLstPage[i].id === ideID){\n" +
                "                prjItems.push({\n" +
                "                    name: \"id=\" + prjLstPage[i].id + \"(IDE*) | name=\" + prjLstPage[i].name + \" | createTime=\" + prjLstPage[i].createTime,\n" +
                "                    value: prjLstPage[i].id\n" +
                "                })\n" +
                "            }\n" +
                "            // 对于ID大于ideID的项目，直接添加到列表中\n" +
                "            if(prjLstPage[i].id > ideID){\n" +
                "                prjItems.push({\n" +
                "                    name: \"id=\" + prjLstPage[i].id + \" | name=\" + prjLstPage[i].name + \" | createTime=\" + prjLstPage[i].createTime,\n" +
                "                    value: prjLstPage[i].id\n" +
                "                })\n" +
                "            }\n" +
                "        }\n" +
                "        return prjItems;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        alert(\"获取UE项目列表失败：\" + error);\n" +
                "        return prjItems;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 异步获取类列表项\n" +
                " * 该函数根据提供的条件参数，获取并构造类列表项数组\n" +
                " * @param equals 查询条件，用于精确查询，listClass使用\n" +
                " * @param like 查询条件，用于模糊查询，listClass使用\n" +
                " * @param inCondition 查询条件，用于指定范围查询，listClass使用\n" +
                " * @returns 返回一个Promise，解析为类列表项数组\n" +
                " */\n" +
                "async function getClassListItem(equals : any, like : any, inCondition : any){\n" +
                "    // 初始化类列表项数组\n" +
                "    let classItems = [];\n" +
                "    // 调用listClass函数，获取类列表数据\n" +
                "    let result = await listClass(equals, 1, 10000, like, inCondition);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取类列表失败：\" + result.message);\n" +
                "        return classItems;\n" +
                "    }\n" +
                "    let classLstPage = result.data;\n" +
                "    try{\n" +
                "        // 检查获取的类列表是否为空\n" +
                "        if(classLstPage.length <=0){\n" +
                "            // 如果列表为空，则弹出提示并返回空数组\n" +
                "            alert(\"该项目还没有定义类！\");\n" +
                "            return classItems;\n" +
                "        }\n" +
                "\n" +
                "        // 遍历类列表数据，构造类列表项数组\n" +
                "        for(let i = 0; i < classLstPage.length; i++){\n" +
                "            // 将每个类的信息格式化后添加到列表项数组中\n" +
                "            let parentTag = \"\";\n" +
                "            if(classLstPage[i].parentGuid != undefined){\n" +
                "                result = await getClassByGuid(classLstPage[i].parentGuid);\n" +
                "                if(result.code != 200){\n" +
                "                    alert(\"获取父类信息失败！\" + classLstPage[i].parentGuid);\n" +
                "                }\n" +
                "                let parentClassObject = result.data;\n" +
                "                if(parentClassObject != undefined){\n" +
                "                    parentTag = \"(基类id=\" + parentClassObject.id + \":\" + parentClassObject.name + \")\";\n" +
                "                }\n" +
                "            }\n" +
                "\n" +
                "            classItems.push(\n" +
                "                {\n" +
                "                    name: parentTag +\n" +
                "                          \"id=\" + classLstPage[i].id + \n" +
                "                          \" | 类名=\" + classLstPage[i].name + \n" +
                "                          \" | 中文名=\" + classLstPage[i].nameZh +\n" +
                "                          \" | 创建时间=\" + classLstPage[i].createTime + \n" +
                "                          \" | Guid=\" + classLstPage[i].guid\n" +
                "                          ,\n" +
                "                    value: classLstPage[i].guid\n" +
                "                }\n" +
                "            );\n" +
                "        }\n" +
                "        // 返回构造完成的类列表项数组\n" +
                "        return classItems;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取类列表失败：\" + error);\n" +
                "        return classItems;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "async function addParentAttriItems(classObject:any, attribute:any, attriItems:any[], tableSymbol:string){\n" +
                "    let result = await listClass({}, 1, 10000,{}, {}).data;\n" +
                "    const attriType = [\"任意类型\", \"基础类型\", \"普通类\", \"结构\", \"脚本\", \"树节点\"];\n" +
                "\n" +
                "    if(result.code !== 200){\n" +
                "        alert(\"获取类列表失败：\" + result.message);\n" +
                "        alert(\"属性\" + attribute.name + \"的属性类型\" + attribute.type + \"不存在！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let parentClassObject = result.data;\n" +
                "    for(let i = 0; i < parentClassObject.length; i++){\n" +
                "        if(attribute.type != parentClassObject[i].guid) continue;\n" +
                "        // 找到属性的父类\n" +
                "        tableSymbol += \"━\";\n" +
                "        for(let attri of parentClassObject[i].fieldDfs){            // 遍历父类的属性\n" +
                "            if(attriType[attri.nature] === \"基础类型\"){           // 属性是基础类型\n" +
                "                attriItems.push({\n" +
                "                    name: tableSymbol + \"id=\" + attri.id + \n" +
                "                        \" | 属性名=\" + attribute.name + \".\" + attri.name + \n" +
                "                        \" | 类型= \" + attri.type + \n" +
                "                        \" | 类别=\"  + attriType[attri.nature] + \n" +
                "                        \" | 默认值=\" + attri.defaultValue,\n" +
                "                    value: attribute.guid\n" +
                "                })\n" +
                "            }else if(attriType[attri.nature] === \"普通类\"){       //属性是普通类，继续深挖\n" +
                "                attriItems.push({                                 // 显式添加一行普通类属性作为标记\n" +
                "                    name: tableSymbol + \"id=\" + attri.id + \n" +
                "                        \" | 属性名=\" + attri.name + \n" +
                "                        \" | 类型= \" + attri.type + \n" +
                "                        \" | 类别=\"  + attriType[attri.nature] + \n" +
                "                        \" | 默认值=\" + attri.defaultValue +\n" +
                "                        \" | createTime=\" + attri.createTime + \n" +
                "                        \" | 所属类id=\" + attri.clazzId,\n" +
                "                    value: attri.guid\n" +
                "                })\n" +
                "                await addParentAttriItems(classObject, attri, attriItems, tableSymbol);     //继续深挖\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * 异步获取类的属性列表项\n" +
                " * @param classGuid 類的ID，用于查询类信息\n" +
                " * @returns 返回一个Promise，解析为类的属性列表项数组\n" +
                " */\n" +
                "async function getAttributeListItem(classGuid:any){\n" +
                "    // 初始化类列表项数组\n" +
                "    let attriItems = [];\n" +
                "    // 定义属性类型的数组，用于后续根据属性性质索引类型字符串\n" +
                "    const attriType = [\"任意类型\", \"基础类型\", \"普通类\", \"结构\", \"脚本\", \"树节点\"];\n" +
                "    \n" +
                "    try{\n" +
                "        // 调用getClassByGuid函数，获取类列表数据\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return attriItems;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象为undefined，则抛出错误\n" +
                "        if(classObject === undefined){\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        // 遍历类对象的字段深度优先搜索结果\n" +
                "        for(let attri of classObject.fieldDfs){\n" +
                "            // 构建属性列表项并添加到数组中\n" +
                "            if(attriType[attri.nature] === \"基本类型\"){         //属性是基础类型\n" +
                "                attriItems.push({\n" +
                "                    name: \"id=\" + attri.id + \n" +
                "                        \" | 属性名=\" + attri.name + \n" +
                "                        \" | 类型= \" + attri.type + \n" +
                "                        \" | 类别=\"  + attriType[attri.nature] + \n" +
                "                        \" | 默认值=\" + attri.defaultValue +\n" +
                "                        \" | createTime=\" + attri.createTime + \n" +
                "                        \" | 所属类id=\" + attri.clazzId,\n" +
                "                    value: attri.guid\n" +
                "                })\n" +
                "            }\n" +
                "            else if(attriType[attri.nature] === \"普通类\"){ \n" +
                "                await addParentAttriItems(classObject, attri, arrtiItems, \"┝\"); //通过递归的方式获取普通类属性的所有成员的选项清单\n" +
                "            }\n" +
                "        }\n" +
                "        // 返回属性列表项数组\n" +
                "        return attriItems;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取属性列表失败：\" + error);\n" +
                "        return attriItems;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 异步获取指定类的方法列表\n" +
                " * @param classGuid 類的标识符\n" +
                " * @returns 返回一个包含类方法信息的数组，如果类不存在或获取失败，返回空数组\n" +
                " */\n" +
                "async function getMethodListItem(classGuid:any){\n" +
                "    // 初始化类方法列表项数组\n" +
                "    let methodItems = [];\n" +
                "    // 定义方法类型数组，用于后续的方法类型描述\n" +
                "    const methodType = [\"构造方法\", \"普通方法\"];\n" +
                "    \n" +
                "    try{\n" +
                "        // 调用getClassByGuid函数，获取类对象数据\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return methodItems;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象不存在，抛出异常\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"类不存在\" + classGuid);\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        // 遍历类对象的方法，构造方法列表项\n" +
                "        for(let method of classObject.methodDfs){\n" +
                "            methodItems.push({\n" +
                "                // 拼接方法信息字符串，包括方法的ID、名称、类型等信息\n" +
                "                name: \"id=\" + method.id + \n" +
                "                      \" | 方法名=\" + method.name + \n" +
                "                      \" | 类型= \" + method.type + \n" +
                "                      \" | 类别=\" + methodType[method.type] + \n" +
                "                      \" | createTime=\" + method.createTime + \n" +
                "                      \" | 所属类id=\" + method.clazzId,\n" +
                "                value: method.guid\n" +
                "            });\n" +
                "        }\n" +
                "        // 返回构造好的方法列表项数组\n" +
                "        return methodItems;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取方法列表失败：\" + error);\n" +
                "        return methodItems;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "async function getParaListItem(classGuid:any, methodGuid:any){\n" +
                "    try{\n" +
                "        // 初始化类方法列表项数组\n" +
                "        let paraItems = [];\n" +
                "        \n" +
                "        // 调用getClassByGuid函数，获取类对象数据\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return paraItems;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象不存在，抛出异常\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"类不存在\" + classGuid);\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        debugger;\n" +
                "\n" +
                "        // 遍历类对象的方法，构造方法列表项\n" +
                "        for(let method of classObject.methodDfs){\n" +
                "            if(method.guid === methodGuid){\n" +
                "                let order = 0;\n" +
                "                for(let para of method.params){\n" +
                "                    paraItems.push({\n" +
                "                        name: \"(\" + (order+1) + \") \" + para.name + \" : \" + para.type + ((para.defaultVal === undefined) ? \"\" : \"=\" + para.defaultVal),\n" +
                "                        value: para.guid\n" +
                "                    });\n" +
                "                    order++;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        // 返回构造好的方法列表项数组\n" +
                "        return paraItems;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取参数列表失败：\" + error);\n" +
                "        return paraItems;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "async function getMethodBody(classGuid:any, methodGuid:any){\n" +
                "    // 初始化类方法列表项数组\n" +
                "    try{\n" +
                "        // 调用getClassByGuid函数，获取类对象数据\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"getMethodBody获取类信息失败：\" + result.message);\n" +
                "            return \"\";\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象不存在，抛出异常\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"getMethodBody类不存在\" + classGuid);\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        debugger;\n" +
                "        // 遍历类对象的方法，返回方法体\n" +
                "        for(let method of classObject.methodDfs){\n" +
                "            if(method.guid === methodGuid){\n" +
                "                let showBodyText = method.body;\n" +
                "                showBodyText = showBodyText.replace(/\\\\n/g, \"\");\n" +
                "                showBodyText = showBodyText.replace(/;/g, \";\\n\");\n" +
                "                return showBodyText;\n" +
                "            }\n" +
                "        }\n" +
                "    \n" +
                "        //返回错误提示\n" +
                "        throw new Error(\"错误：没找到方法体！\");\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取方法体内容失败：\" + error);\n" +
                "        return \"\";\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class PromptChain {\n" +
                "  constructor() {\n" +
                "    this.data = {};\n" +
                "  }\n" +
                "  \n" +
                "  ask(question, key) {\n" +
                "    this.data[key] = prompt(question);\n" +
                "    return this; // 返回自身支持链式\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "async function refreshClass(refreshClassGuid:string = \"\", refreshMethodGuid:string = \"\"){\n" +
                "    try{\n" +
                "        //先清空所有的内容，然后根据传入的参数显示指定的类和指定的方法内容\n" +
                "        dropdownSelectClass.value = \"请选择实体类\";\n" +
                "        dropdownOptionClass.value.length = 0;\n" +
                "        dropdownSelectField.value = \"请选择参数\";\n" +
                "        dropdownOptionField.value.length = 0;\n" +
                "        editClassNumbers.value = \"类：\";\n" +
                "        editFieldNumbers.value = \"类属性\";\n" +
                "        dropdownSelectMethod.value = \"请选择实体方法\";\n" +
                "        dropdownOptionMethod.value.length = 0;\n" +
                "        dropdownSelectPara.value = \"请选择方法参数\";\n" +
                "        dropdownOptionPara.value.length = 0;\n" +
                "        editMethodBody.value = \"请输入方法体内容\";\n" +
                "        editMethodNumbers.value = \"类方法：\";\n" +
                "        editParaNumbers.value = \"参数清单：\";\n" +
                "        \n" +
                "        let classGuid:string;\n" +
                "        let methodGuid:string;\n" +
                "    \n" +
                "        //无论如何先刷新类列表\n" +
                "        let classLst = await getClassListItem({}, {}, {});\n" +
                "        if(classLst.length === 0){\n" +
                "            dropdownSelectClass.value = \"该项目还没有定义类！\";\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        dropdownOptionClass.value = classLst;\n" +
                "        editClassNumbers.value = \"类：\" + classLst.length;\n" +
                "\n" +
                "        //如果指定了要刷新属性的类guid，则仅刷新该类，否则默认刷新类列表中得一个类\n" +
                "        classGuid = (refreshClassGuid === \"\") ? classLst[0].value : refreshClassGuid;\n" +
                "        dropdownSelectClass.value = classGuid;\n" +
                "\n" +
                "        if(classGuid === \"\"){\n" +
                "            alert(\"请选择类！\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        let attriItems = await getAttributeListItem(classGuid);\n" +
                "        if(attriItems.length === 0){\n" +
                "            dropdownSelectField.value = \"类还没有定义属性！ classGuid=\" + classGuid;\n" +
                "        }\n" +
                "        else{\n" +
                "            dropdownOptionField.value = attriItems;\n" +
                "            dropdownSelectField.value = attriItems[0].value;\n" +
                "        }\n" +
                "        editFieldNumbers.value = \"类属性：\" + attriItems.length;\n" +
                "\n" +
                "        let methodItems = await getMethodListItem(classGuid);\n" +
                "        //显示原始获得的方法清单\n" +
                "        let paraItems = [];\n" +
                "        if(methodItems.length === 0){\n" +
                "            dropdownSelectMethod.value = \"类还没有定义方法！ classGuid=:\" + classGuid;\n" +
                "            dropdownSelectPara.value = \"无参数\";\n" +
                "        }\n" +
                "        else{\n" +
                "            methodGuid = (refreshMethodGuid === \"\")? methodItems[0].value : refreshMethodGuid;\n" +
                "            dropdownOptionMethod.value = methodItems;\n" +
                "            dropdownSelectMethod.value = methodGuid;\n" +
                "\n" +
                "            paraItems = await getParaListItem(classGuid, methodGuid);\n" +
                "            if(paraItems.length === 0){\n" +
                "                dropdownSelectPara.value = \"无参数\"; \n" +
                "            }\n" +
                "            else{\n" +
                "                dropdownOptionPara.value = paraItems;\n" +
                "                dropdownSelectPara.value = paraItems[0].value;\n" +
                "            }\n" +
                "\n" +
                "            editMethodBody.value = \"\";\n" +
                "            editMethodBody.value = await getMethodBody(classGuid, methodGuid);\n" +
                "        }\n" +
                "        editMethodNumbers.value = \"类方法：\" + methodItems.length;\n" +
                "        editParaNumbers.value = \"参数清单：\" + paraItems.length;\n" +
                "    }\n" +
                "    catch (error) {\n" +
                "        alert(\"刷新类内容出错！\\n\" + error);\n" +
                "    }       \n" +
                "}\n" +
                "\n" +
                "async function getMethodDefination(classGuid: string, methodGuid: string) { \n" +
                "    // 初始化类方法列表项数组\n" +
                "    let methodDefinationText = \"\";\n" +
                "    \n" +
                "    try{\n" +
                "        // 调用getClassByGuid函数，获取类对象数据\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"getMethodBody获取类信息失败：\" + result.message);\n" +
                "            return \"\";\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象不存在，抛出异常\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"类不存在\" + classGuid);\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        if(methodGuid === \"\"){\n" +
                "            alert(\"请选择方法！\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        // 遍历类对象的方法，构造方法定义内容\n" +
                "        for(let method of classObject.methodDfs){\n" +
                "            if(method.guid === methodGuid){\n" +
                "                methodDefinationText += method.name + \"(\";\n" +
                "                for(let param of method.params){\n" +
                "                    methodDefinationText += param.name + \":\" + param.type + \", \";\n" +
                "                }\n" +
                "                methodDefinationText = methodDefinationText.substring(0, methodDefinationText.length - 2) + \")\";\n" +
                "                return methodDefinationText;\n" +
                "            }\n" +
                "        }\n" +
                "        // 返回构造好的方法列表项数组\n" +
                "        return \"\";\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取方法列表失败：\" + error);\n" +
                "        return ;\n" +
                "    }    \n" +
                "}\n" +
                "const btnFuMQV = async ()=>{\n" +
                "                debugger;\n" +
                "    editIDEUrl.value = \"http://uui.test.zjht100.com\";\n" +
                "    \n" +
                "    let IDEid = 5;      //Ue项目：IDE_0.0.0.1, id=5\n" +
                "    let domainUrl = editIDEUrl.value;\n" +
                "    setDomain(domainUrl);\n" +
                "    let ideDomainURL = getDomain();\n" +
                "    if(ideDomainURL === domainUrl){\n" +
                "        let Items = await getPrjListItem(listUePrj, {}, {}, {}, \"all\", IDEid)\n" +
                "        dropdownOptionUePrj.value = Items;\n" +
                "        editIDEName.value = Items.find((Item) => Item.value === IDEid).name;\n" +
                "        \n" +
                "        dropdownOptionUiPrj.value = await getPrjListItem(listUiPrj, {}, {}, {}, \"all\", 7)\n" +
                "\n" +
                "        let curExecPrjID = getExecPrj();\n" +
                "        let curOperatingPrjID = getOperatingPrj();\n" +
                "\n" +
                "        let prjLstPageNow = await listUePrj({id: curExecPrjID}, 1, 10000, {}, {});\n" +
                "        editCurExecPrj.value = prjLstPageNow.data[0].id + \" : \" + prjLstPageNow.data[0].name;\n" +
                "        prjLstPageNow = await listUePrj({id: curOperatingPrjID}, 1, 10000, {}, {});\n" +
                "        editCurOperatingPrj.value = prjLstPageNow.data[0].id + \" : \" + prjLstPageNow.data[0].name;\n" +
                "    }\n" +
                "    else {\n" +
                "        alert(\"无法连接IDE服务器【\" + domainUrl + \"】\");\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnoY3bx = async ()=>{\n" +
                "                let NewPrjName = prompt(\"请输入新业务模型的名称（同时创建同名的Ue和Ui项目）：\", \"请使用英文\");\n" +
                "    if(NewPrjName === null) return;\n" +
                "    if(NewPrjName.length === 0){\n" +
                "        alert(\"请输入有效的业务模型名称！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let IDEid = 5;      //Ue项目：IDE_0.0.0.1, id=5\n" +
                "    let prjLstPage = await listUePrj({name: NewPrjName}, 1, 10000, {}, {});\n" +
                "    if(prjLstPage.data.length != 0){\n" +
                "        alert(\"该项目:\" + NewPrjName + \"已存在!\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let result = await createUnifiedPrj(\n" +
                "        {name: NewPrjName},\n" +
                "        {\n" +
                "            name: NewPrjName,\n" +
                "            workDir: NewPrjName\n" +
                "        },\n" +
                "        1\n" +
                "    )\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"创建项目失败！\" + result.msg);\n" +
                "        return;\n" +
                "    }\n" +
                "    alert(\"创建项目：\" + NewPrjName + \"\\n目录: \\\\\" + NewPrjName);\n" +
                "\n" +
                "    //刷新Ue和Ui两个下拉菜单\n" +
                "    dropdownOptionUePrj.value = await getPrjListItem(listUePrj, {}, {}, {}, \"ide\", IDEid);\n" +
                "    dropdownOptionUiPrj.value = await getPrjListItem(listUiPrj, {}, {}, {}, \"ide\", 7);  \n" +
                "        }\n" +
                "               \n" +
                "const btnLFCvh = async ()=>{\n" +
                "                await refreshClass(); \n" +
                "  \n" +
                "        }\n" +
                "               \n" +
                "const btnfM8UA = async ()=>{\n" +
                "                let selectedUePrjId = dropdownSelectUePrj.value;\n" +
                "    let IDEUeId = 5;\n" +
                "    let IDEUiID = 7;\n" +
                "\n" +
                "    let prjSelected = await listUePrj({id:selectedUePrjId}, 1, 10000, {}, {});\n" +
                "    if(prjSelected.data.length === 0){\n" +
                "        alert(\"没有找到项目：\" + selectedUePrjId);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    if(!confirm(\"确认要删除Ue项目：\" + prjSelected.data[0].name + \"吗？（且同时删除同名的Ui项目）\")) return;\n" +
                "    let result = await deleteUePrj(selectedUePrjId);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"删除Ue项目失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    let prjUi = await listUiPrj({name:prjSelected.data[0].name}, 1, 10000, {}, {})\n" +
                "    if(prjUi.data.length > 1){        //找到同名的Ui项目\n" +
                "        let ItemString = \"\";\n" +
                "        for(let i = 0; i < prjUi.data.length; i++){\n" +
                "            ItemString += prjUi.data[i].name + \"(\" + prjUi.data[i].id + \")\\n\";\n" +
                "        }\n" +
                "        let deleteID = prompt(\"找到多个重名的Ui项目：\\n\" + ItemString + \"\\n\" + \"请选择要删除的Ui项目ID：\");\n" +
                "        result = await deleteUiPrj(deleteID);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"删除Ui项目失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        alert(\"已删除同名Ui项目之一:\" + deleteID  + \"\\n\");\n" +
                "    }\n" +
                "    else if(prjUi.data.length === 1){\n" +
                "        result = await deleteUiPrj(prjUi.data[0].id);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"删除Ui项目失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        alert(\"已删除同名Ui项目的ID=\" + prjUi.data[0].id);\n" +
                "    }\n" +
                "    else{\n" +
                "        alert(\"没有同名的Ui项目！\");\n" +
                "    }\n" +
                "\n" +
                "    //刷新Ue和Ui两个下拉菜单\n" +
                "    dropdownOptionUePrj.value = await getPrjListItem(listUePrj, {}, {}, {}, \"ide\", IDEUeId);\n" +
                "    dropdownOptionUiPrj.value = await getPrjListItem(listUiPrj, {}, {}, {}, \"ide\", IDEUiID)  \n" +
                "        }\n" +
                "               \n" +
                "const btnE4Lop = async ()=>{\n" +
                "                let selectedUiPrjId = dropdownSelectUiPrj.value;\n" +
                "    let IDEUeId = 5;\n" +
                "    let IDEUiID = 7;\n" +
                "\n" +
                "    let prjSelected = await listUiPrj({id:selectedUiPrjId}, 1, 10000, {}, {});\n" +
                "    if(prjSelected.data.length === 0){\n" +
                "        alert(\"没有找到Ui项目：\" + selectedUiPrjId);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    if(!confirm(\"确认要删除Ui项目：\" + prjSelected.data[0].name + \"吗？（且同时删除同名的Ue项目）\")) return;\n" +
                "\n" +
                "    //确认删除选中的项目\n" +
                "    let result = await deleteUiPrj(selectedUiPrjId);     //删除Ui项目\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"删除Ui项目失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    //确认删除同名的Ue项目\n" +
                "    let prjUe = await listUePrj({name:prjSelected.data[0].name}, 1, 10000, {}, {});\n" +
                "    if(prjUe.data.length > 1){        //找到多个同名的Ue项目\n" +
                "        debugger;\n" +
                "        let ItemString = \"\";\n" +
                "        for(let i = 0; i < prjUe.data.length; i++){\n" +
                "            ItemString += prjUe.data[i].name + \"(\" + prjUe.data[i].id + \")\\n\";\n" +
                "        }\n" +
                "        let deleteID = prompt(\"找到多个重名的Ue项目：\\n\" + ItemString + \"\\n\" + \"请选择要删除的Ue项目ID：\")\n" +
                "        result = await deleteUePrj(deleteID);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"删除Ue项目失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        alert(\"已删除同名Ue项目之一:\" + deleteID  + \"\\n\");\n" +
                "    }\n" +
                "    else if(prjUe.data.length === 1){         //只找到一个同名的Ue项目\n" +
                "        result = await deleteUePrj(prjUe.data[0].id);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"删除Ue项目失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        alert(\"已删除同名Ue项目的ID=\" + prjUe.data[0].id);\n" +
                "    }\n" +
                "    else{\n" +
                "        alert(\"没有同名的Ue项目！\");\n" +
                "    }\n" +
                "\n" +
                "    //刷新UI和UE下拉菜单\n" +
                "    dropdownOptionUiPrj.value = await getPrjListItem(listUiPrj, {}, {}, {}, \"ide\", IDEUiID);\n" +
                "    dropdownOptionUePrj.value = await getPrjListItem(listUePrj, {}, {}, {}, \"ide\", IDEUeId);  \n" +
                "        }\n" +
                "               \n" +
                "const btnHbGCp = async ()=>{\n" +
                "                let curExecPrjID = getExecPrj();\n" +
                "    let curOperatingPrjID = getOperatingPrj();\n" +
                "\n" +
                "    let prjLstPage = await listUePrj({id: curExecPrjID}, 1, 10000, {}, {});\n" +
                "    editCurExecPrj.value = prjLstPage.data[0].id + \" : \" + prjLstPage.data[0].name;\n" +
                "    prjLstPage = await listUePrj({id: curOperatingPrjID}, 1, 10000, {}, {});\n" +
                "    editCurOperatingPrj.value = prjLstPage.data[0].id + \" : \" + prjLstPage.data[0].name;  \n" +
                "        }\n" +
                "               \n" +
                "const btngiXqD = async ()=>{\n" +
                "                let parentClassGuid = (dropdownOptionClass.value.length > 0) ? dropdownSelectClass.value : \"\";\n" +
                "\n" +
                "    let newClassInfo = new PromptChain()\n" +
                "    .ask(\"请输入类名（仅限英文）：\", \"className\")\n" +
                "    .ask(\"请输入类的中文名称：\", \"classNameZh\");\n" +
                "\n" +
                "    if(newClassInfo.data.className === null) return;\n" +
                "    newClassInfo.data.className = newClassInfo.data.className.trim();\n" +
                "    newClassInfo.data.classNameZh = newClassInfo.data.classNameZh.trim();\n" +
                "    if(newClassInfo.data.className.length === 0){\n" +
                "        alert(\"类名不能为空！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let newClass;\n" +
                "    let result;\n" +
                "    if(parentClassGuid.length > 0){\n" +
                "        result = await getClassByGuid(parentClassGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"没有找到类：\" + parentClassGuid);\n" +
                "            return;\n" +
                "        }\n" +
                "        let parentClassObjct = result.data;\n" +
                "        if(confirm(\"希望新建的类从:\\nid=\" + parentClassObjct.id + \" | 类名=\" + parentClassObjct.name + \"\\n继承吗？\")){\n" +
                "            result = await createClass(newClassInfo.data.className, newClassInfo.data.classNameZh, {parentGuid : parentClassGuid});\n" +
                "        }\n" +
                "        else{\n" +
                "            result = await createClass(newClassInfo.data.className, newClassInfo.data.classNameZh);\n" +
                "        }\n" +
                "    }else{\n" +
                "        result = await createClass(newClassInfo.data.className, newClassInfo.data.classNameZh);\n" +
                "    }\n" +
                "\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"创建类失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    newClass = result.data;\n" +
                "    if(newClass === undefined){\n" +
                "        alert(\"创建类失败！\");\n" +
                "        return;\n" +
                "    } \n" +
                "    result = await newClass.setType(\"User\");\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"设置类类型失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    result = await newClass.setModifer(\"public\");\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"设置类可修改性失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    alert(\"创建类成功！\" +\n" +
                "        \"\\n类型 =\" + (newClass.type === \"User\" ? \"用户类\" : \"系统类\") +\n" +
                "        \"\\nid =\" + newClass.id + \n" +
                "        \"\\n可修改性 =\" + newClass.modifer + \n" +
                "        \"\\n类名 =\" + newClass.name + \n" +
                "        \"\\n类名中文 =\" + newClass.nameZh +\n" +
                "        \"\\nguid =\" + newClass.guid + \n" +
                "        \"\\n父类guid = \" + newClass.parentGuid + \n" +
                "        \"\\n创建时间 = \" + newClass.createTime + \n" +
                "        \"\\n修改时间 = \" + newClass.updateTime + \n" +
                "        \"\\n创建者 = \" + newClass.createUser +\n" +
                "        \"\\n修改者 = \" + newClass.updateUser\n" +
                "    );\n" +
                "\n" +
                "    debugger;\n" +
                "    await refreshClass(newClass.guid);  \n" +
                "        }\n" +
                "               \n" +
                "const btnGBdlz = async ()=>{\n" +
                "                let classGuid = dropdownSelectClass.value;\n" +
                "\n" +
                "    if(classGuid === \"\"){\n" +
                "        alert(\"请选择类！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let result = await getClassByGuid(classGuid);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取类信息失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    let classObject = result.data;\n" +
                "\n" +
                "    // 如果类对象不存在，抛出异常\n" +
                "    if(classObject === undefined){\n" +
                "        alert(\"类不存在:\" + classGuid);\n" +
                "        console.log(\"类不存在\" + classGuid);\n" +
                "        throw new error(\"类不存在\" + classGuid);\n" +
                "    }\n" +
                "\n" +
                "    if(confirm(\"确定要删除类吗？\" + classObject.name)){\n" +
                "        result = await deleteClass({guid: classGuid});\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"删除类失败：\" + result.message);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    await refreshClass();  \n" +
                "        }\n" +
                "               \n" +
                "const btnpUAB0 = async ()=>{\n" +
                "                try{\n" +
                "        let classGuid = dropdownSelectClass.value;\n" +
                "\n" +
                "        if(classGuid === \"\"){\n" +
                "            alert(\"请选择类！\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象不存在，抛出异常\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"类不存在:\" + classGuid);\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        let attributeGuid = dropdownSelectField.value;\n" +
                "        if(attributeGuid === \"\"){\n" +
                "            alert(\"请选择属性！\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        for(let attribute of classObject.fieldDfs){\n" +
                "            if(attribute.guid === attributeGuid){\n" +
                "                if(confirm(\"确定要删除属性\" + attribute.name + \"吗？\")){\n" +
                "                    result = await classObject.deleteField(attribute.name);\n" +
                "                    if(result.code != 200){\n" +
                "                        alert(\"删除属性失败：\" + result.message);\n" +
                "                    }\n" +
                "                    await refreshClass(classGuid);\n" +
                "                }\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    alert(\"没有找到属性！\");\n" +
                "    }\n" +
                "    catch(e){\n" +
                "        alert(\"删除属性时出现问题:\" + e);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnrgMaf = async ()=>{\n" +
                "                // 获取选中的类GUID\n" +
                "    let classGuid = dropdownSelectClass.value;\n" +
                "    // 获取选中的方法GUID\n" +
                "    let methodGuid = dropdownSelectMethod.value;\n" +
                "\n" +
                "    // 检查是否选择了类\n" +
                "    if(classGuid === \"\"){\n" +
                "        alert(\"请选择类！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    // 根据类GUID获取类对象\n" +
                "    let result = await getClassByGuid(classGuid);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取类信息失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    let classObject = result.data;\n" +
                "\n" +
                "    // 如果类对象不存在，抛出异常\n" +
                "    if(classObject === undefined){\n" +
                "        alert(\"类不存在:\" + classGuid);\n" +
                "        console.log(\"类不存在\" + classGuid);\n" +
                "        throw new error(\"类不存在\" + classGuid);\n" +
                "    }\n" +
                "\n" +
                "    // 遍历类对象的方法，构造方法定义内容\n" +
                "    for(let method of classObject.methodDfs){\n" +
                "        if(method.guid === methodGuid){\n" +
                "            let methodDefinationText = \"\";\n" +
                "            methodDefinationText += method.name + \"(  \";\n" +
                "            for(let param of method.params){\n" +
                "                methodDefinationText += param.name + \":\" + param.type + \", \";\n" +
                "            }\n" +
                "            // 移除最后一个逗号和空格\n" +
                "            methodDefinationText = methodDefinationText.substring(0, methodDefinationText.length - 2) + \")\";\n" +
                "\n" +
                "            // 确认是否删除方法\n" +
                "            if(confirm(\"确定要删除方法吗？\\n\" + methodDefinationText)){\n" +
                "                // 调用类对象的删除方法函数，并刷新类视图\n" +
                "                result = await classObject.deleteMethod(method.name);\n" +
                "                if(result.code != 200){\n" +
                "                    alert(\"删除方法失败：\" + result.message);\n" +
                "                }\n" +
                "                await refreshClass(classGuid);\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    // 如果方法不存在，显示警告\n" +
                "    alert(\"要删除的方法不存在！\\n\" + methodGuid);  \n" +
                "        }\n" +
                "               \n" +
                "const btnBqPvE = async ()=>{\n" +
                "                let classGuid = dropdownSelectClass.value;\n" +
                "\n" +
                "    if(classGuid === \"\"){\n" +
                "        alert(\"请选择类！\");\n" +
                "        return;\n" +
                "    }\n" +
                "    \n" +
                "    let typeBasicList = [\n" +
                "        \"string\",\n" +
                "        \"number\"\n" +
                "        ];\n" +
                "    let attriNatureList = [\n" +
                "        \"任意类型\",\n" +
                "        \"基础类型\",\n" +
                "        \"普通类\",\n" +
                "        \"结构\",\n" +
                "        \"脚本\",\n" +
                "        \"树节点\"\n" +
                "        ];\n" +
                "\n" +
                "    let typeList = typeBasicList;\n" +
                "\n" +
                "    typeList = typeList.join(\", \") + \"\\n\";\n" +
                "    let result  = await listClass({}, 1, 10000,{}, {});\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取类列表失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    let classList = result.data;\n" +
                "    let attriNature = undefined;        //0:任意类型; 1:基础类型; 2:普通类; 3:结构; 4:脚本; 5:树节点\n" +
                "    for(let i = 0; i < classList.length; i++){\n" +
                "        if(classGuid === classList[i].guid) continue;\n" +
                "        typeList.push(\"用户自定义类：\" + classList[i].name);\n" +
                "        typeList += classList[i].name + \"\\n\";\n" +
                "    }\n" +
                "\n" +
                "    let newAttribute = new PromptChain()\n" +
                "    .ask(\"请输入属性名称(仅限英文）：\", \"name\")\n" +
                "    .ask(\"请输入显示名称：\", \"displayName\")\n" +
                "    .ask(\"请输入属性类型：\\n\" + typeListText, \"type\")\n" +
                "    .ask(\"请输入默认值：\", \"initValue\");\n" +
                "\n" +
                "    if(newAttribute.data.name === null) return;\n" +
                "    if(newAttribute.data.name.trim() === \"\") return;\n" +
                "    if(newAttribute.data.type !== null){\n" +
                "        if(typeBasicList.includes(newAttribute.data.type)){\n" +
                "            attriNature = 1;\n" +
                "        }\n" +
                "        else{\n" +
                "            attriNature = typeList.includes(newAttribute.data.type) ? 2 : undefined;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    alert(\n" +
                "        \"属性名称:\" + newAttribute.data.name + \"\\n\" +\n" +
                "        \"显示名称:\" + newAttribute.data.displayName + \"\\n\" + \n" +
                "        \"属性类型:\" + newAttribute.data.type + \"\\n\" + \n" +
                "        \"默认值:\" + newAttribute.data.initValue + \"\\n\" +\n" +
                "        \"类别：\" + ((newAttribute.data.attriNature == undefined) ? \"null\" : attriNatureList[newAttribute.data.attriNature]) \n" +
                "    );\n" +
                "\n" +
                "    debugger;\n" +
                "    try{\n" +
                "        result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"未找到该类！\")\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        result = await classObject.addField(\n" +
                "            newAttribute.data.displayName,\n" +
                "            newAttribute.data.name, \n" +
                "            newAttribute.data.type\n" +
                "        );\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"添加属性失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let attributeObject = result.data;\n" +
                "        if(attributeObject == undefined){\n" +
                "            alert(\"添加属性失败！\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        if(newAttribute.data.initValue !== null){\n" +
                "            result = await attributeObject.setInitValue(newAttribute.data.initValue);\n" +
                "            if(result.code != 200){\n" +
                "                alert(\"设置属性默认值失败：\" + result.message);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        classObject = result.data;\n" +
                "        result = await attributeObject.setSort(classObject.fieldDfs.length);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"设置属性排序失败：\" + result.message);\n" +
                "        }\n" +
                "\n" +
                "        attributeObject.nature = attriNature;\n" +
                "        result = await attributeObject.save();\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"保存属性失败：\" + result.message);\n" +
                "        }\n" +
                "\n" +
                "        await refreshClass(classGuid);\n" +
                "    }\n" +
                "    catch (e) {\n" +
                "        alert(\"添加属性失败！\" + e.message);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnTIfVz = async ()=>{\n" +
                "                let classGuid = dropdownSelectClass.value;\n" +
                "\n" +
                "    if(classGuid === \"\"){\n" +
                "        alert(\"请选择类！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let inputMethodText = new PromptChain()\n" +
                "    .ask(\"请输入方法定义：\\n格式：方法名称(参数:类型,参数:类型=默认值,……)\\n方法定义符合JavaScript语言格式要求：\\n\", \"func\")\n" +
                "    .ask(\"请输入方法体：\\n格式：{方法体}\\n\", \"funcBody\")\n" +
                "\n" +
                "    if(inputMethodText.data.funcBody === null) return;\n" +
                "    if(inputMethodText.data.func === \"\"){\n" +
                "        alert(\"方法名不能为空！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    //解析方法名和参数\n" +
                "    let funcName = inputMethodText.data.func.split(\"(\")[0];\n" +
                "    if(funcName === \"\"){\n" +
                "        alert(\"方法名不能为空！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let funcParamsExpress = inputMethodText.data.func.split(\"(\")[1].split(\")\")[0].split(\",\");\n" +
                "    let funcParamsList = [];\n" +
                "    for(let i = 0; i < funcParamsExpress.length; i++){\n" +
                "        funcParamsExpress[i] = funcParamsExpress[i].trim();\n" +
                "        funcParamsList.push({\n" +
                "            paraName: funcParamsExpress[i].split(\"=\")[0].trim().split(\":\")[0].trim(),\n" +
                "            paraType: funcParamsExpress[i].split(\"=\")[0].trim().split(\":\")[1].trim(),\n" +
                "            paraDefaultValue: (funcParamsExpress[i].split(\"=\")[1] != undefined) ? funcParamsExpress[i].split(\"=\")[1].trim() : undefined\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    try{\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "        if(classObject === undefined){\n" +
                "            alert(\"未找到该类！\")\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        debugger;\n" +
                "        result = await classObject.createMethod(funcName, inputMethodText.data.funcBody);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"添加方法失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let methodObject = result.data;\n" +
                "        if(methodObject === undefined){\n" +
                "            alert(\"添加方法失败！\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        alert(\"添加方法成功！\" + methodObject.guid + \"\\n\" + methodObject.name);\n" +
                "        for(let i = 0; i < funcParamsList.length; i++){\n" +
                "            result = await methodObject.addParam({\n" +
                "                \"name\": funcParamsList[i].paraName,\n" +
                "                \"type\": funcParamsList[i].paraType,\n" +
                "                \"defaultVal\": funcParamsList[i].paraDefaultValue,\n" +
                "            });\n" +
                "            if(result.code != 200){\n" +
                "                alert(\"添加参数失败：\" + result.message);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        await refreshClass(classGuid, methodObject.guid);\n" +
                "    }\n" +
                "    catch (e) {\n" +
                "        alert(\"添加方法失败！\" + e.message);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnGNy1I = async ()=>{\n" +
                "                let inputMethodText = new PromptChain()\n" +
                "    .ask(\"请需要添加的参数清单，各参数间用\\\",\\\"分割，一次可以添加多个参数（包括默认值）：\\n格式：参数1:类型1, 参数2:类型2=默认值,……\\n参数定义符合JavaScript语言格式要求：\\n\", \"paralist\")\n" +
                "\n" +
                "    if(inputMethodText.data.paralist === null) return;\n" +
                "    if(inputMethodText.data.paralist.trim === \"\"){\n" +
                "        alert(\"什么参数也没有写！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    //解析参数\n" +
                "    let funcParamsExpress = inputMethodText.data.paralist.split(\",\");\n" +
                "    let funcParamsList = [];\n" +
                "    for(let i = 0; i < funcParamsExpress.length; i++){\n" +
                "        funcParamsExpress[i] = funcParamsExpress[i].trim();\n" +
                "        funcParamsList.push({\n" +
                "            paraName: funcParamsExpress[i].split(\"=\")[0].trim().split(\":\")[0].trim(),\n" +
                "            paraType: funcParamsExpress[i].split(\"=\")[0].trim().split(\":\")[1].trim(),\n" +
                "            paraDefaultValue: (funcParamsExpress[i].split(\"=\")[1] != undefined) ? funcParamsExpress[i].split(\"=\")[1].trim() : undefined\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    let classGuid = dropdownSelectClass.value;\n" +
                "    let methodGuid = dropdownSelectMethod.value;\n" +
                "    let result = await getClassByGuid(classGuid);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取类信息失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    let classObject = result.data;\n" +
                "    if(classObject === undefined){\n" +
                "        alert(\"未找到该类！\")\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    // 遍历类对象的所有方法，找到当前方法的对象\n" +
                "    let methodObject = undefined;\n" +
                "    for(let method of classObject.methodDfs){\n" +
                "        if(method.guid === methodGuid){\n" +
                "            methodObject = method; \n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    if(methodObject === undefined){\n" +
                "        alert(\"没找到方法\" + methodGuid);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    for(let i = 0; i < funcParamsList.length; i++){\n" +
                "        result = await methodObject.addParam({\n" +
                "            \"name\": funcParamsList[i].paraName,\n" +
                "            \"type\": funcParamsList[i].paraType,\n" +
                "            \"defaultVal\": funcParamsList[i].paraDefaultValue,\n" +
                "        });\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"添加参数失败：\" + result.message);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    await refreshClass(classGuid, methodObject.guid);  \n" +
                "        }\n" +
                "               \n" +
                "const btnJIHw4 = async ()=>{\n" +
                "                // 获取用户选择的类、方法和参数的GUID\n" +
                "    let classGuid = dropdownSelectClass.value;\n" +
                "    let methodGuid = dropdownSelectMethod.value;\n" +
                "    let parameterGuid = dropdownSelectPara.value;\n" +
                "\n" +
                "    // 检查是否选择了类，如果没有，则提示用户并返回\n" +
                "    if(classGuid === \"\"){\n" +
                "        alert(\"请选择类！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    // 根据GUID获取类对象\n" +
                "    let result = await getClassByGuid(classGuid);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"获取类信息失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    let classObject = result.data;\n" +
                "\n" +
                "    // 如果类对象不存在，抛出异常\n" +
                "    if(classObject === undefined){\n" +
                "        alert(\"类不存在:\" + classGuid);\n" +
                "        console.log(\"类不存在\" + classGuid);\n" +
                "        throw new error(\"类不存在\" + classGuid);\n" +
                "    }\n" +
                "\n" +
                "    // 遍历类对象的方法，通过类→方法→参数，找到指定的参数并删除之\n" +
                "    for(let method of classObject.methodDfs){\n" +
                "        if(method.guid != methodGuid) continue;\n" +
                "        for(let param of method.params){\n" +
                "            if(param.guid != parameterGuid) continue;\n" +
                "\n" +
                "            // 构建参数定义文本，用于确认对话框\n" +
                "            let paramDefinationText = \"\";\n" +
                "            paramDefinationText += param.name + \" : \" + param.type + ((param.defaultVal === undefined) ? \"\" : \"=\" + param.defaultVal);\n" +
                "            // 提示用户确认是否删除参数\n" +
                "            if(confirm(\"确定要删除参数吗？\\n\" + paramDefinationText)){\n" +
                "                // 调用方法删除参数，并刷新类视图\n" +
                "                result = await method.deleteParam(param.name);\n" +
                "                if(result.code != 200){\n" +
                "                    alert(\"删除参数失败：\" + result.message);\n" +
                "                }\n" +
                "                await refreshClass(classGuid, methodGuid);\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    // 如果指定的参数不存在，显示错误提示\n" +
                "    alert(\"要删除的参数不存在！\\n方法guid=\" + methodGuid + \"\\n参数guid=\" + parameterGuid);  \n" +
                "        }\n" +
                "               \n" +
                "const btnyKPlG = async ()=>{\n" +
                "                let classGuid = dropdownSelectClass.value;\n" +
                "\n" +
                "    if(classGuid === \"\"){\n" +
                "        alert(\"请选择类！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let methodGuid = dropdownSelectMethod.value;\n" +
                "    if(methodGuid === \"\"){\n" +
                "        alert(\"请选择方法！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "        // 初始化类方法列表项数组\n" +
                "    try{\n" +
                "        // 调用getClassByGuid函数，获取类对象数据\n" +
                "        let result = await getClassByGuid(classGuid);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"获取类信息失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let classObject = result.data;\n" +
                "\n" +
                "        // 如果类对象不存在，抛出异常\n" +
                "        if(classObject === undefined){\n" +
                "            console.log(\"类不存在\" + classGuid);\n" +
                "            throw new error(\"类不存在\" + classGuid);\n" +
                "        }\n" +
                "\n" +
                "        // 遍历类对象的方法，返回方法体\n" +
                "        for(let method of classObject.methodDfs){\n" +
                "            if(method.guid === methodGuid){\n" +
                "                result = await method.setBody(editMethodBody.value);\n" +
                "                if(result.code != 200){\n" +
                "                    alert(\"保存方法失败：\" + result.message);\n" +
                "                    return;\n" +
                "                }\n" +
                "                alert(\"保存成功！\\n{\\n\" + editMethodBody.value + \"\\n}\");\n" +
                "                refreshClass(classGuid, methodGuid);\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        // 捕获异常，弹出错误提示并返回空数组\n" +
                "        alert(\"获取方法失败：\" + error);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnZLVXJ = async ()=>{\n" +
                "                let selectedPrjId = dropdownSelectUePrj.value; \n" +
                "\n" +
                "    if(selectedPrjId.length == 0){\n" +
                "        alert(\"请先选择一个项目！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let prjLstPage = await listUePrj({id: selectedPrjId}, 1, 10000, {}, {});\n" +
                "    if(prjLstPage.data.length != 1){\n" +
                "        alert(\"没有找到项目：\" + selectedPrjId);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    console.log(\"找到项目:\" + prjLstPage.data[0].id + \"项目名：\" + prjLstPage.data[0].name);\n" +
                "    try{ \n" +
                "        setOperatingPrj(prjLstPage.data[0].id);\n" +
                "        console.log(\"设置可建模编辑的统一实体项目为：\"+ prjLstPage.data[0].id + \"项目名：\" + prjLstPage.data[0].name + \"\\n\");\n" +
                "\n" +
                "        let curExecPrjID = getExecPrj();\n" +
                "        let curOperatingPrjID = getOperatingPrj();\n" +
                "\n" +
                "        let prjLstPageNow = await listUePrj({id: curExecPrjID}, 1, 10000, {}, {});\n" +
                "        editCurExecPrj.value = prjLstPageNow.data[0].id + \" : \" + prjLstPageNow.data[0].name;\n" +
                "        prjLstPageNow = await listUePrj({id: curOperatingPrjID}, 1, 10000, {}, {});\n" +
                "        editCurOperatingPrj.value = prjLstPageNow.data[0].id + \" : \" + prjLstPageNow.data[0].name;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        alert(\"切换建模项目时发生了问题：\", error);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btngkSa0 = async ()=>{\n" +
                "                let NewPrjName = prompt(\"请输入新业务模型(UE)的名称：\", \"请使用英文\");\n" +
                "    if(NewPrjName === null){\n" +
                "        return;\n" +
                "    }\n" +
                "    if(NewPrjName.trim().length === 0){\n" +
                "        alert(\"请输入有效的业务模型(UE)名称！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let IDEid = 5;      //Ue项目：IDE_0.0.0.1, id=5\n" +
                "    let prjLstPage = await listUePrj({name: NewPrjName}, 1, 10000, {}, {});\n" +
                "    if(prjLstPage.data.length != 0){\n" +
                "        alert(\"该项目:\" + NewPrjName + \"已存在!\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let result = await createUePrj({name: NewPrjName});\n" +
                        " let f1=function(){};\n"+
                "    if(result.code != 200){\n" +
                "        alert(\"创建UE项目失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    alert(\"创建UE项目：\" + NewPrjName + \"成功！\");\n" +
                "\n" +
                "    //刷新Ue下拉菜单\n" +
                "    dropdownOptionUePrj.value = await getPrjListItem(listUePrj, {}, {}, {}, \"ide\", IDEid);  \n" +
                "        }\n" +
                "               \n" +
                "const btn6YFSn = async ()=>{\n" +
                "                let NewUiPrjName = prompt(\"请输入新的前端交互项目(UI)名称：\", \"请使用英文\");\n" +
                "    if(NewUiPrjName === null){\n" +
                "        return;\n" +
                "    }\n" +
                "    if(NewUiPrjName.trim().length === 0){\n" +
                "        alert(\"请输入有效的前端交互项目(UI)名称！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let IDEUiid = 7+IDEid;      //Ui项目：IDE_0.0.0.1, id=7\n" +
                "    let prjLstPage = await listUiPrj({name: NewUiPrjName}, 1, 10000, {}, {});\n" +
                "    if(prjLstPage.data.length != 0){\n" +
                "        alert(\"该项目:\" + NewUiPrjName + \"已存在!\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    let result = await createUiPrj({name: NewUiPrjName, workDir : \"\\\\\"+NewUiPrjName}, 1);\n" +
                "    if(result.code != 200){\n" +
                "        alert(\"创建UI项目失败：\" + result.message);\n" +
                "        return;\n" +
                "    }\n" +
                "    alert(\"创建UI项目：\" + NewUiPrjName + \"\\n目录: \\\\\" + NewUiPrjName);\n" +
                "\n" +
                "    //刷新Ui下拉菜单\n" +
                "    dropdownOptionUiPrj.value = await getPrjListItem(listUiPrj, {}, {}, {}, \"ide\", IDEUiid);  \n" +
                "        }\n" +
                "               \n" +
                "const btnyvGSC = async ()=>{\n" +
                "              \n" +
                "        }\n" +
                "               \n" +
                "const btnMd9Gy = async ()=>{\n" +
                "                let selectedPrjId = dropdownSelectUePrj.value;   //Ue项目列表 \n" +
                "\n" +
                "    if(dropdownOptionUePrj.value.length == 0){\n" +
                "        alert(\"请先创建一个UE项目！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    if(selectedPrjId.length == 0){\n" +
                "        alert(\"请先选择一个UE项目！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    //虽然selectedPrjId是选中的UE项目的id，但是还是要查询一下真伪，然后用查到的id去部署\n" +
                "    let prjLstPage = await listUePrj({id: selectedPrjId}, 1, 10000, {}, {});\n" +
                "    if(prjLstPage.data.length != 1){\n" +
                "        alert(\"没有找到项目：\" + selectedPrjId);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    try{ \n" +
                "        let result = await setExecPrj(prjLstPage.data[0].id);           //setExecPrj(prjId)是异步方法的，所以需要等待其完成\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"设置执行项目失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        let curExecPrjID = getExecPrj();\n" +
                "        let prjLstPageNow = await listUePrj({id: curExecPrjID}, 1, 10000, {}, {});\n" +
                "        if(prjLstPageNow.code != 200){\n" +
                "            alert(\"没有找到当前执行的统一实体项目！\");\n" +
                "            return;\n" +
                "        }\n" +
                "        editCurExecPrj.value = prjLstPageNow.data[0].id + \" : \" + prjLstPageNow.data[0].name;\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        alert(\"部署时发生了问题：\", error);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnpIiy1 = async ()=>{\n" +
                "                let selectedPrjId = dropdownSelectUePrj.value;   //Ue项目列表 \n" +
                "\n" +
                "    if(dropdownOptionUePrj.value.length == 0){\n" +
                "        alert(\"请先创建一个UE项目！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    if(selectedPrjId.length == 0){\n" +
                "        alert(\"请先选择一个UE项目！\");\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    //虽然selectedPrjId是选中的UE项目的id，但是还是要查询一下真伪，然后用查到的id去部署\n" +
                "    let prjLstPage = await listUePrj({id: selectedPrjId}, 1, 10000, {}, {});\n" +
                "    if(prjLstPage.data.length != 1){\n" +
                "        alert(\"没有找到项目：\" + selectedPrjId);\n" +
                "        return;\n" +
                "    }\n" +
                "\n" +
                "    try{ \n" +
                "        //仅部署，不默认设置为当前运行的项目\n" +
                "        let result = await deployUePrj(prjLstPage.data[0].id, true);\n" +
                "        if(result.code != 200){\n" +
                "            alert(\"部署项目失败：\" + result.message);\n" +
                "            return;\n" +
                "        }\n" +
                "        alert(\"项目：\" + prjLstPage.data[0].name + \" 部署成功！\");\n" +
                "    }\n" +
                "    catch(error){\n" +
                "        alert(\"部署时发生了问题：\", error);\n" +
                "    }  \n" +
                "        }\n" +
                "               \n" +
                "const btnmtTju = async ()=>{\n" +
                "              \n" +
                "        }\n" +
                "               \n" +
                "const btnoe5oN = async ()=>{\n" +
                "              \n" +
                "        }\n" +
                "               \n" +
                "\n" ;
// Prepare a script name.
        URL specifier = new URL("file:///abc.ts");
// Prepare an option with script name and media type.
        Swc4jParseOptions options = new Swc4jParseOptions()
                .setSpecifier(specifier)
                .setMediaType(Swc4jMediaType.TypeScript)
                // Set capture ast.
                .setCaptureAst(true)
                .setParseMode(Swc4jParseMode.Module);
// Parse the code.
        Swc4jParseOutput output = swc4j.parse(code, options);
// Print the tokens.
        System.out.println("/*********************************************");
        System.out.println("         The ast is as follows.");
        System.out.println("*********************************************/");
        ISwc4jAstProgram<? extends ISwc4jAst> ast = output.getProgram();
        System.out.println(output.getProgram().toDebugString());


        String script = code;
        LinkedHashMap<String, TsBlock> tsBlocks = new LinkedHashMap<>();
        for (ISwc4jAst block : ast.getBody()) {

            if(block instanceof Swc4jAstVarDecl){
                Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) block;
                TsBlock tsBlock = new TsBlock();
                tsBlock.setBody(getSym(varDecl.getSpan(), script));
                for (Swc4jAstVarDeclarator varDeclarator : varDecl.getDecls()) {
                    String varName=getSym(varDeclarator.getName().getSpan(), script);
                    tsBlock.getDeclVars().add(varName);
                    tsBlocks.put(varName, tsBlock);
                    Optional<ISwc4jAstExpr> init = varDeclarator.getInit();
                    if(init==null || init.isPresent() && init.get() instanceof Swc4jAstArrowExpr){
                        continue;
                    }
                    List<Object> deps = FieldValueExtractor.extractVal(init.get(), "sym");
                    deps.forEach(dep -> {
                        tsBlock.getDeps().add(dep.toString());
                    });
                }
            }
        }
    }

    private static Map<String, TsBlock> parseTScript(String code) throws Exception {
        Swc4j swc4j = new Swc4j();
        URL specifier = new URL("file:///"+UUID.randomUUID().toString()+".ts");
// Prepare an option with script name and media type.
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

            if(block instanceof Swc4jAstVarDecl){
                Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) block;
                TsBlock tsBlock = new TsBlock();
                tsBlock.setBody(getSym(varDecl.getSpan(), script));
                for (Swc4jAstVarDeclarator varDeclarator : varDecl.getDecls()) {
                    String varName=getSym(varDeclarator.getName().getSpan(), script);
                    tsBlock.getDeclVars().add(varName);
                    tsBlocks.put(varName, tsBlock);
                    Optional<ISwc4jAstExpr> init = varDeclarator.getInit();
                    if(init==null || init.isPresent() && init.get() instanceof Swc4jAstArrowExpr){
                        continue;
                    }
                    List<Object> deps = FieldValueExtractor.extractVal(init.get(), "sym");
                    deps.forEach(dep -> {
                        tsBlock.getDeps().add(dep.toString());
                    });
                }
            }
        }
        return tsBlocks;
    }

    private static String getSym(Swc4jSpan  span, String script){
        return script.substring(span.getStart(), span.getEnd());
    }

    private static void fillDeps(ISwc4jAst node, Set<String> deps){
        if(node instanceof Swc4jAstVarDecl){
            Swc4jAstVarDecl varDecl = (Swc4jAstVarDecl) node;
            for (Swc4jAstVarDeclarator varDeclarator : varDecl.getDecls()) {
                Optional<ISwc4jAstExpr> init = varDeclarator.getInit();
                if(init==null || init.isPresent() && init.get() instanceof Swc4jAstArrowExpr){
                    continue;
                }
                List<Object> depVals = FieldValueExtractor.extractVal(init.get(), "sym");
                depVals.forEach(depVal -> {
                    deps.add(depVal.toString());
                });
            }
        }
    }

    @V8Function(name = "new")
    public V8Value newInstance(V8Value... args) throws Exception {
        V8Value v8Value = new JavetProxyConverter().toV8Value(getRuntime(), new TestProxyObject());
        if (v8Value instanceof V8ValueObject) {
            V8ValueObject value = (V8ValueObject) v8Value;
            V8ValueFunction m2Function = (V8ValueFunction) getRuntime()
                    .getExecutor("(function() { return function(x) { return this.name + x; }; })()")
                    .execute();

            value.set("m2", m2Function);

        }
        return v8Value;
    }


    public static Object exec(String script) {
        try {
            V8Runtime v8Runtime = getRuntime();
            V8Value tst = new JavetProxyConverter().toV8Value(v8Runtime, new TestProxyObject());
            v8Runtime.getGlobalObject().set("tst", tst);
            Object o = v8Runtime.getExecutor(script).executeObject();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static V8Runtime getRuntime() {
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            consoleInterceptor.register(v8Runtime.getGlobalObject());

            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("V8Test", v8ValueObject);
                V8Test v8Test = new V8Test();
                v8ValueObject.bind(v8Test);
            }
            return v8Runtime;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public void testM() {

        ExecutorService executor = Executors.newFixedThreadPool(2);

//        exec(
//                "var a =ClassUtils.new(\"ClassA\");\n" +
//                        "var b  = ClassUtils.new(\"ClassA\")\n;" +
//                        "a.f1 = b ;\n" +
//                        "b.f2 = 3;\n" +
//                        "console.log(\"111111111111111111111111\");\n"+
//                        "console.log(\"\"+a.f1.f2);"

//                "var a = ClassUtils.newPersist(\"ClassA\");"+
//                "a.name = \"张三new\";"+
//                "var a = ClassUtils.new(\"Device\",1,2);"+
//                        " //console.log(a.power_on()); \n"+
//                        " console.log(`a.pv = ${a.pv}`);" +
//                        " console.log(`a.name.archiveStatus = ${a.deviceName.archiveStatus}`);"+
//                        " console.log(`a.deviceName + 1 = ${a.deviceName + 1}`);"+
//                        " console.log(`a.deviceName.eval = ${a.deviceName.eval}`);"+
//                        " console.log(`a.deviceName.currentvalue is ${a.deviceName }`);"+
//                        " console.log(`a.deviceName.lastvalue is  ${a.deviceName.lastValue}`);"+
//                        " console.log(`a.deviceName.lastEv is  ${a.deviceName.lastEv}`);"+
//
//                        " console.log(` exec a.deviceName + 1   ${a.deviceName = a.deviceName + 1 }`);"+
//
//                        " console.log(`a.deviceName.currentvalue is ${a.deviceName }`);"+
//                        " console.log(`a.deviceName.lastvalue is  ${a.deviceName.lastValue}`);"+
//                        " console.log(`a.deviceName.lastEv is  ${a.deviceName.lastEv}`);"
//                "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                        "myDevice.power_on(); \n" +
//                        "myDevice.deviceName = '新name';\n"
//                , taskContext);

//        exec(
//                "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                        "myDevice.power_off(); "
//                , taskContext);

//        Runnable task1 = () -> {
//            exec(
//                    "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                            "myDevice.power_on(); \n" +
//                            "myDevice.deviceName = '新name';\n",
////                            "myDevice.deviceStatus = '新name';\n",
//                    taskContext
//            );
//        };
//
//        Runnable task2 = () -> {
//            exec(
//                    "var myDevice = ClassUtils.newPersist('Device'); \n" +
//                            "myDevice.power_off();",
//                    taskContext
//            );
//        };
//
//        executor.submit(task1);
//        executor.submit(task2);
//        executor.shutdown();
    }

    public void testExec() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> {
            exec(
                    "1+1"

            );
        };

        Runnable task2 = () -> {
            exec(
                    "1+2"
            );
        };

        executor.submit(task1);
        executor.submit(task2);
        executor.shutdown();
    }


//    private static void test() {
//        String json = "{\n" +
//                "        \"clazzList\": [\n" +
//                "            {\n" +
//                "                \"id\": 19,\n" +
//                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                \"name\": \"Device\",\n" +
//                "                \"nameZh\": \"设备类\",\n" +
//                "                \"type\": \"1\",\n" +
//                "                \"prjId\": 2,\n" +
//                "                \"tbl\": \"device\",\n" +
//                "                \"persistent\": 0,\n" +
//                "                \"version\": \"1\",\n" +
//                "                \"pvAttr\": \"\",\n" +
//                "                \"constructor\": \"  constructor(name, type) {\\r\\n    this.name = name;\\r\\n    this.type = type;\\r\\n  }\",\n" +
//                "                \"prjGuid\": \"49e40d23-f3f4-11ef-bac4-8csdasbcbca77\",\n" +
//                "                \"prjVer\": \"deviceversion\",\n" +
//                "                \"clazzIdFieldDefList\": [\n" +
//                "                    {\n" +
//                "                        \"id\": 20,\n" +
//                "                        \"name\": \"deviceName\",\n" +
//                "                        \"type\": \"String\",\n" +
//                "                        \"nature\": 1,\n" +
//                "                        \"initValue\": \"测试设备1\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 2,\n" +
//                "                        \"tblCol\": \"device_name\",\n" +
//                "                        \"displayName\": \"设备名称\",\n" +
//                "                        \"cachable\": -1,\n" +
//                "                        \"defaultLock\": \"lock\",\n" +
//                "                        \"classGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"guid\": \"97e4b32c-96aa-4a80-9647-3a90ea0751db\",\n" +
//                "                        \"archiveStatus\": 0\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 21,\n" +
//                "                        \"name\": \"deviceStatus\",\n" +
//                "                        \"type\": \"String\",\n" +
//                "                        \"nature\": 1,\n" +
//                "                        \"initValue\": \"offline\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 2,\n" +
//                "                        \"tblCol\": \"device_status\",\n" +
//                "                        \"displayName\": \"设备状态\",\n" +
//                "                        \"cachable\": 0,\n" +
//                "                        \"defaultLock\": \"lock\",\n" +
//                "                        \"classGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"guid\": \"f0bc2053-d1c9-4cbd-8121-4514d4b7f968\",\n" +
//                "                        \"archiveStatus\": 0\n" +
//                "                    }\n" +
//                "                ],\n" +
//                "                \"clazzIdMethodDefList\": [\n" +
//                "                    {\n" +
//                "                        \"id\": 17,\n" +
//                "                        \"name\": \"power_on\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = 'online';  // 修改实例属性\\r\\n    console.log(this.pvAttr);\\r\\n    console.log(`${this.deviceName} 已启动`);\\r\\n    return this.deviceStatus;\\r\\n  }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 0,\n" +
//                "                        \"description\": \"改变pvAttr值为online\",\n" +
//                "                        \"displayName\": \"启动设备\",\n" +
//                "                        \"guid\": \"7e290ff8-069f-46ed-97e4-ffe42f25a001\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": []\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 18,\n" +
//                "                        \"name\": \"power_off\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = 'offline'; // 修改实例属性\\r\\n    console.log(this.pvAttr);\\r\\n    console.log(`${this.deviceName} 已关闭`);\\r\\n    return this.deviceStatus;\\r\\n  }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 0,\n" +
//                "                        \"description\": \"改变pvAttr值为offline\",\n" +
//                "                        \"displayName\": \"关闭设备\",\n" +
//                "                        \"guid\": \"dbc34bd7-a0d4-49c9-9227-1c58b5f5bdee\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": []\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 21,\n" +
//                "                        \"name\": \"consfunc1\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = a;   this.deviceName = b ;  console.log('构造方法 consfunc1 执行') ; }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 1,\n" +
//                "                        \"description\": \"构造方法1\",\n" +
//                "                        \"displayName\": \"构造方法1\",\n" +
//                "                        \"guid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": [\n" +
//                "                            {\n" +
//                "                                \"id\": 1,\n" +
//                "                                \"name\": \"a\",\n" +
//                "                                \"type\": \"String\",\n" +
//                "                                \"sort\": 1,\n" +
//                "                                \"description\": \"参数 a\",\n" +
//                "                                \"methodId\": 21,\n" +
//                "                                \"guid\": \"guid-a1234567-89ab-cdef-0123-456789abcdef\",\n" +
//                "                                \"methodGuid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\"\n" +
//                "                            },\n" +
//                "                            {\n" +
//                "                                \"id\": 2,\n" +
//                "                                \"name\": \"b\",\n" +
//                "                                \"type\": \"Integer\",\n" +
//                "                                \"sort\": 2,\n" +
//                "                                \"description\": \"参数 b\",\n" +
//                "                                \"methodId\": 21,\n" +
//                "                                \"guid\": \"guid-b1234567-89ab-cdef-0123-456789abcdef\",\n" +
//                "                                \"methodGuid\": \"dbc3sdasd7-a0d4-49c9-9227-1c58b5f5bdee\"\n" +
//                "                            }\n" +
//                "                        ]\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"id\": 22,\n" +
//                "                        \"name\": \"consfunc2\",\n" +
//                "                        \"body\": \"{\\r\\n    this.deviceStatus = a;   console.log('构造方法 consfunc2 执行') ; }\",\n" +
//                "                        \"clazzId\": 19,\n" +
//                "                        \"prjId\": 90519240,\n" +
//                "                        \"type\": 1,\n" +
//                "                        \"description\": \"构造方法2\",\n" +
//                "                        \"displayName\": \"构造方法2\",\n" +
//                "                        \"guid\": \"e8f2a3bc-1234-5678-9101-1a2b3c4d5e6f\",\n" +
//                "                        \"clazzGuid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\",\n" +
//                "                        \"methodIdMethodParamList\": [\n" +
//                "                            {\n" +
//                "                                \"id\": 31,\n" +
//                "                                \"name\": \"a\",\n" +
//                "                                \"type\": \"string\",\n" +
//                "                                \"sort\": 1,\n" +
//                "                                \"description\": \"参数 a，用于设置 deviceStatus\",\n" +
//                "                                \"methodId\": 22,\n" +
//                "                                \"guid\": \"f7a9d5e2-7890-4567-1234-abcdef123456\",\n" +
//                "                                \"methodGuid\": \"e8f2a3bc-1234-5678-9101-1a2b3c4d5e6f\"\n" +
//                "                            }\n" +
//                "                        ]\n" +
//                "                    }\n" +
//                "                ]\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"sentinelDefList\": [\n" +
//                "            {\n" +
//                "                \"id\": 4,\n" +
//                "                \"name\": \"设备开门\",\n" +
//                "                \"body\": \"var myDevice = ClassUtils.newPersist('Device'); \\r\\nmyDevice.power_on(); \\r\\nmyDevice.deviceName = '新name';\\r\\n// setTimeout(() => { myDevice.power_off();}, 10000);  \",\n" +
//                "                \"cron\": \"0 * * * * ?\",\n" +
//                "                \"concurrent\": 0,\n" +
//                "                \"abort\": \"运行3小时后关闭\",\n" +
//                "                \"prjId\": 2,\n" +
//                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939867\"\n" +
//                "            },\n" +
//                "            {\n" +
//                "                \"id\": 5,\n" +
//                "                \"name\": \"设备关门\",\n" +
//                "                \"body\": \"var myDevice = ClassUtils.newPersist('Device'); \\r\\nmyDevice.power_off(); \",\n" +
//                "                \"cron\": \"0 * * * * ?\",\n" +
//                "                \"concurrent\": 0,\n" +
//                "                \"abort\": \"运行3小时后关闭\",\n" +
//                "                \"prjId\": 2,\n" +
//                "                \"guid\": \"b65f7c18-732e-4d3c-bf21-cabc14939868\"\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"uePrj\": {\n" +
//                "            \"id\": 2,\n" +
//                "            \"name\": \"deviceprj\",\n" +
//                "            \"uiPrjId\": 2,\n" +
//                "            \"version\": \"deviceversion\",\n" +
//                "            \"guid\": \"49e40d23-f3f4-11ef-bac4-8csdasbcbca77\"\n" +
//                "        }\n" +
//                "    }";
//
//        PrjSpecDO prjSpecDO = JSON.parseObject(json, PrjSpecDO.class);
////        TaskController bean = SpringUtils.getBean(TaskController.class);
////        bean.run(prjSpecDO);
//
//
//        RtRedisObjectStorageService rtRedisObjectStorageService = SpringUtils.getBean(RtRedisObjectStorageService.class);
//        TaskContext ctx = SpringUtils.getBean(RtContextService.class).startNewSession(prjSpecDO, System.currentTimeMillis() + "");
//        rtRedisObjectStorageService.initSpecDefinition(ctx, prjSpecDO);
//        rtRedisObjectStorageService.initializeInstances(ctx, prjSpecDO);
//        V8EngineService engineService = SpringUtils.getBean(V8EngineService.class);
//        engineService.testM(ctx);
//    }


}
