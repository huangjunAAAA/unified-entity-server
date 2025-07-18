package com.zjht.ui.utils;

import java.util.*;
import java.util.regex.*;

public class VueSfcParser {
    // 描述符类：存储解析结果
    public static class SFCDescriptor {
        public SFCBlock template;
        public SFCBlock script;
        public List<SFCBlock> styles = new ArrayList<>();
        public List<SFCBlock> customBlocks = new ArrayList<>();
    }

    // 块信息类：存储标签类型、属性、内容
    public static class SFCBlock {
        public String type;
        public String content;
        public Map<String, String> attrs = new HashMap<>();
        public int start;
        public int end;
    }

    // 主解析函数
    public static SFCDescriptor parseComponent(String source) {
        SFCDescriptor descriptor = new SFCDescriptor();
        SFCBlock currentBlock = null;
        int index = 0;

        // 重构正则：明确支持script/style标签，优化自闭合和注释处理
        Pattern tagPattern = Pattern.compile(
            "</?(" +
            "template|script|style|[a-zA-Z][^\\s>!]*" +  // 明确支持sfc标签
            ")([^>]*?)(\\s*/)?>|<!--([\\s\\S]*?)-->",  // 优化自闭合和注释
            Pattern.DOTALL
        );
        Matcher matcher = tagPattern.matcher(source);

        while (matcher.find()) {
            String fullTag = matcher.group(0);
            // 跳过注释标签 <!-- -->
            if (fullTag.startsWith("<!--")) {
                index = matcher.end();
                continue;
            }

            boolean isEndTag = fullTag.startsWith("</");
            String tagName = (matcher.group(1) != null) ? matcher.group(1).toLowerCase() : "";
            String attrStr = (matcher.group(2) != null) ? matcher.group(2).trim() : "";
            boolean isSelfClosing = matcher.group(3) != null;  // 通过第三组判断自闭合
            int tagStart = matcher.start();
            int tagEnd = matcher.end();

            // 处理起始标签
            if (!isEndTag) {
                // 仅处理SFC支持的核心标签
                if (currentBlock == null && 
                    (tagName.equals("template") || tagName.equals("script") || 
                     tagName.equals("style") || !tagName.isEmpty())) {
                    
                    currentBlock = new SFCBlock();
                    currentBlock.type = tagName;
                    currentBlock.start = tagEnd;
                    
                    // 解析属性（支持混合引号）
                    if (!attrStr.isEmpty()) {
                        parseAttributes(attrStr, currentBlock.attrs);
                    }
                    
                    // 自闭合标签立即结束
                    if (isSelfClosing) {
                        currentBlock.end = tagEnd;
                        currentBlock.content = "";
                        addBlockToDescriptor(currentBlock, descriptor);
                        currentBlock = null;
                    }
                }
            } 
            // 处理结束标签
            else if (currentBlock != null && tagName.equals(currentBlock.type)) {
                currentBlock.end = tagStart;
                currentBlock.content = deindent(
                    source.substring(currentBlock.start, currentBlock.end)
                );
                addBlockToDescriptor(currentBlock, descriptor);
                currentBlock = null;
            }
            index = tagEnd;
        }
        return descriptor;
    }

    // 属性解析（增强引号处理）
    private static void parseAttributes(String attrStr, Map<String, String> attrs) {
        // 重构正则：支持混合引号和无值属性
        Pattern attrPattern = Pattern.compile(
            "(\\w+)\\s*=\\s*([\"'])(.*?)\\2|(\\w+)(?=\\s|$)", 
            Pattern.DOTALL
        );
        Matcher m = attrPattern.matcher(attrStr);
        while (m.find()) {
            if (m.group(1) != null) {  // key="value" 形式
                attrs.put(m.group(1), m.group(3));
            } else if (m.group(4) != null) {  // 无值属性
                attrs.put(m.group(4), "true");
            }
        }
    }

    // 去除缩进（简化实现）
    private static String deindent(String content) {
        return content.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    // 块分类存储（template/script/style/自定义）
    private static void addBlockToDescriptor(SFCBlock block, SFCDescriptor descriptor) {
        switch (block.type) {
            case "template":
                descriptor.template = block;
                break;
            case "script":
                descriptor.script = block;
                break;
            case "style":
                descriptor.styles.add(block);
                break;
            default:
                descriptor.customBlocks.add(block);
        }
    }

    // 示例用法
    public static void main(String[] args) {
        String vueFile = "<!-- ZjTree.vue -->\n" +
                "<template>\n" +
                "       <el-tree\n" +
                "        style=\"width: 100%;height: 100%;\"\n" +
                "        :data=\"dataSource\"\n" +
                "        :show-checkbox=\"showCheckBox\"\n" +
                "        node-key=\"id\"\n" +
                "        default-expand-all\n" +
                "        :expand-on-click-node=\"false\"\n" +
                "        >\n" +
                "        <template #default=\"{ node, data }\" v-if=\"showDell\">\n" +
                "            <div class=\"custom-tree-node\">\n" +
                "            <span>{{ node.label }}</span>\n" +
                "            <div>\n" +
                "                <!-- <el-button type=\"primary\" link @click=\"append(data)\">\n" +
                "                Append\n" +
                "                </el-button> -->\n" +
                "                <el-button\n" +
                "                style=\"margin-left: 4px\"\n" +
                "                type=\"danger\"\n" +
                "                link\n" +
                "                @click=\"remove(node, data)\"\n" +
                "                v-if=\"showDell\"\n" +
                "                >\n" +
                "                Delete\n" +
                "                </el-button>\n" +
                "            </div>\n" +
                "            </div>\n" +
                "        </template>\n" +
                "        </el-tree>\n" +
                "  </template>\n" +
                "  \n" +
                "  <script  lang=\"ts\">\n" +
                "  import { defineComponent,ref  } from 'vue'\n" +
                "  import { Node,Cell } from '@antv/x6'\n" +
                "  import { useHomeStore } from \"@/stores/home\"\n" +
                "  import type { RenderContentContext } from 'element-plus'\n" +
                "  // RenderContentFunction\n" +
                "  interface Tree {\n" +
                "    id: number\n" +
                "    label: string\n" +
                "    children?: Tree[]\n" +
                "    }\n" +
                "  type treeNode = RenderContentContext['node']\n" +
                "  type Data = RenderContentContext['data']\n" +
                "\n" +
                "\n" +
                "  export default defineComponent({\n" +
                "    name:\"ZjTree\",\n" +
                "    inject:['getNode'],\n" +
                "    data() {\n" +
                "      return {\n" +
                "        form:{\n" +
                "          select:\"\"\n" +
                "        },\n" +
                "        editableLabel: '请选择',\n" +
                "        id:100,\n" +
                "        showCheckBox:true,\n" +
                "        showDell:true,\n" +
                "        dataSource: ref<Tree[]>([\n" +
                "            {\n" +
                "                id: 1,\n" +
                "                label: 'Level one 1',\n" +
                "                children: [\n" +
                "                    {\n" +
                "                        id: 4,\n" +
                "                        label: 'Level two 1-1',\n" +
                "                        children: [\n" +
                "                            {\n" +
                "                                id: 9,\n" +
                "                                label: 'Level three 1-1-1',\n" +
                "                            },\n" +
                "                            {\n" +
                "                                id: 10,\n" +
                "                                label: 'Level three 1-1-2',\n" +
                "                            },\n" +
                "                        ],\n" +
                "                    },\n" +
                "                ],\n" +
                "            },\n" +
                "            {\n" +
                "                id: 2,\n" +
                "                label: 'Level one 2',\n" +
                "                children: [\n" +
                "                    {\n" +
                "                        id: 5,\n" +
                "                        label: 'Level two 2-1',\n" +
                "                    },\n" +
                "                    {\n" +
                "                        id: 6,\n" +
                "                        label: 'Level two 2-2',\n" +
                "                    },\n" +
                "                ],\n" +
                "            },\n" +
                "           \n" +
                "            ])\n" +
                "      }\n" +
                "    },\n" +
                "    computed: {\n" +
                "      dynamicLabel() {\n" +
                "        return this.editableLabel; // 动态返回可编辑label文本\n" +
                "      }\n" +
                "    },\n" +
                "    mounted(){\n" +
                "      const node = (this as any).getNode()as Node;\n" +
                "        console.log(\"node mounted ZjTree\")\n" +
                "        console.log(node)\n" +
                "      //  if(node&&node.attrs!.text.text){\n" +
                "      //     this.editableLabel = String(node.attrs!.text.text)\n" +
                "      //   }\n" +
                "        const homeStore = useHomeStore();\n" +
                "         if(node&&node.attrs!.text.nameValue==\"treeData\"){\n" +
                "            let str = this.generateRandomString(8)\n" +
                "            setTimeout(() => {\n" +
                "              const cell = homeStore.getGraph!.getCellById(node.id);\n" +
                "              cell.attr(`text/nameValue`, `treeData${str}`)\n" +
                "              // cell.attr(`text/arrayValue`, `options${str}`)\n" +
                "            }, 100)\n" +
                "         }\n" +
                "        node.on('change:*', (args: {\n" +
                "          cell: Cell\n" +
                "          node: Node\n" +
                "          key: string   // 通过 key 来确定改变项\n" +
                "          current: any  // 当前值，类型根据 key 指代的类型确定\n" +
                "          previous: any // 改变之前的值，类型根据 key 指代的类型确定\n" +
                "          options: any  // 透传的 options\n" +
                "        }) => { \n" +
                "            // console.log(\"args\")\n" +
                "            // console.log(args)\n" +
                "            // console.log(args.current)\n" +
                "            if(args.key==\"attrs\"&&args.current){\n" +
                "              let current = args.current\n" +
                "              //  console.log(current)\n" +
                "              //  console.log(current.text)\n" +
                "              this.showCheckBox = Boolean(current.text.showCheckbox);  \n" +
                "              this.showDell = Boolean(current.text.showDell);  \n" +
                "              // console.log(  this.showCheckBox )\n" +
                "              // console.log( this.showDell )\n" +
                "            }\n" +
                "            \n" +
                "        })\n" +
                "  \n" +
                "    },\n" +
                "    methods:{\n" +
                "      generateRandomString(length:number) {\n" +
                "        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'; \n" +
                "        return Array.from({ length })\n" +
                "          .map(() => characters.charAt(Math.floor(Math.random() * characters.length)))\n" +
                "          .join('');\n" +
                "      },\n" +
                "      append(data: Data){\n" +
                "        const newChild = { id: this.id++, label: 'testtest', children: [] }\n" +
                "        if (!data.children) {\n" +
                "            data.children = []\n" +
                "        }\n" +
                "        data.children.push(newChild)\n" +
                "        this.dataSource = [...this.dataSource]\n" +
                "      },\n" +
                "      remove(node: treeNode, data: Data) {\n" +
                "        const parent = node.parent\n" +
                "        const children: Tree[] = parent?.data.children || parent?.data\n" +
                "        const index = children.findIndex((d) => d.id === data.id)\n" +
                "        children.splice(index, 1)\n" +
                "        this.dataSource = [...this.dataSource]\n" +
                "      }\n" +
                "    }\n" +
                "  })\n" +
                " \n" +
                "  </script>\n" +
                "  <style lang=\"scss\" scoped>\n" +
                "     .custom-tree-node {\n" +
                "        flex: 1;\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "        justify-content: space-between;\n" +
                "        font-size: 14px;\n" +
                "        padding-right: 8px;\n" +
                "    }\n" +
                "  </style>";

        SFCDescriptor result = parseComponent(vueFile);
        System.out.println("Template: " + result.template.content); // <div>Hello {{name}}</div>
        System.out.println("Script: " + result.script.content);    // export default { ... }
        System.out.println("Style (scoped): " + result.styles.get(0).attrs.get("scoped")); // true
    }
}