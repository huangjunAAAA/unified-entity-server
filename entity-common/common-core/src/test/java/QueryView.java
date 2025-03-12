import com.wukong.core.weblog.utils.JsonUtil;
import com.zjht.unified.common.core.domain.component.CollectionField;
import com.zjht.unified.common.core.domain.component.ComponentSpec;
import com.zjht.unified.common.core.domain.component.CustomizeOptions;
import com.zjht.unified.common.core.domain.FieldLocator;
import com.zjht.unified.common.core.domain.component.SimpleField;

import java.util.ArrayList;
import java.util.List;

public class QueryView {
    public static void main(String[] args) {
        ComponentSpec spec=new ComponentSpec();
        spec.setInterfaces(new ArrayList<>());
        String is="import {reactive, ref} from 'vue'\n" +
                "import { prjList } from '@/api/prjApi';\n" +
                "import {QueryTableView,QueryBar,InputField,DisplayField,OpField,QueryField,ZFunc,DataTable,GeneralComponent} from '@zjht/zbasics'\n" +
                "// import {deserialize,registerClass, registerFunc} from \"@/components/objserialize\"\n" +
                "import {deserialize,registerClass, registerFunc} from '@zjht/zbasics'\n" +
                "\n" +
                "\n" +
                "const utilityClass=[QueryBar,QueryField,InputField,DataTable,OpField,DisplayField,GeneralComponent,ZFunc]\n" +
                "utilityClass.forEach(el =>{\n" +
                "            registerClass(el);\n" +
                "        })\n" +
                "registerFunc(prjList)" +
                "\n" +
                "let sample=\"${sample}\";\n" +
                "const qbSpec = deserialize(new QueryBar(),sample);\n" +
                "const handleRow=(rowdata: any) => {\n" +
                "    console.log(rowdata);\n" +
                "}\n" +
                "qbSpec.tableSpec.oFields[0].submitFunc = new ZFunc(handleRow); ";
        System.out.println(is);
        String sample="{\"__clazz\":\"QueryBar\",\"shape\":{\"__clazz\":\"GeneralComponent\",\"disabled\":false,\"placeholder\":\"\",\"ctype\":\"\",\"icon\":\"\",\"size\":\"\",\"startPlaceholder\":\"\",\"rangeSeparator\":\"\",\"vformat\":\"\",\"endPlaceholder\":\"\"},\"qFields\":[{\"__clazz\":\"QueryField\",\"shape\":{\"__clazz\":\"GeneralComponent\",\"disabled\":false,\"placeholder\":\"输入ID\",\"ctype\":\"\",\"icon\":\"\",\"size\":\"\",\"startPlaceholder\":\"\",\"rangeSeparator\":\"\",\"vformat\":\"\",\"endPlaceholder\":\"\"},\"dataField\":{\"__clazz\":\"InputField\",\"id\":\"0\",\"name\":\"id\",\"type\":\"input\",\"valRef\":{\"dep\":{\"version\":0,\"sc\":0},\"__v_isRef\":true,\"__v_isShallow\":false,\"_rawValue\":\"1\",\"_value\":\"1\"},\"vformat\":\"YYYY-MM-DD\"},\"alignment\":true}],\"alignment\":\"top\",\"splitWidth\":\"5px\",\"pageParam\":\"page:1\",\"sizeParam\":\"size:10\",\"tableSpec\":{\"__clazz\":\"DataTable\",\"disabled\":false,\"placeholder\":\"\",\"ctype\":\"\",\"icon\":\"\",\"size\":\"\",\"startPlaceholder\":\"\",\"rangeSeparator\":\"\",\"vformat\":\"\",\"endPlaceholder\":\"\",\"dFields\":[{\"__clazz\":\"DisplayField\",\"id\":\"d1\",\"name\":\"id\",\"title\":\"ID\",\"order\":0,\"alignment\":true,\"minWidth\":\"190\"}],\"oFields\":[{\"__clazz\":\"OpField\",\"disabled\":false,\"placeholder\":\"\",\"ctype\":\"\",\"icon\":\"view\",\"size\":\"\",\"startPlaceholder\":\"\",\"rangeSeparator\":\"\",\"vformat\":\"\",\"endPlaceholder\":\"\",\"title\":\"查看\",\"order\":0,\"submitFunc\":{\"__clazz\":\"ZFunc\",\"fname\":\"handleRow\"}}],\"opWidth\":\"\",\"alignment\":\"middle\"},\"getListFunc\":{\"__clazz\":\"ZFunc\",\"fname\":\"\"}}";
        spec.setSample(sample);
        CustomizeOptions inf1=new CustomizeOptions();
        inf1.setBindings(new ArrayList<>());
        CollectionField cf=new CollectionField();
        cf.setName("查询条件");
        cf.setDesc("查询条件字段列表");
        cf.setOrder(1);
        inf1.getBindings().add(cf);

        List<FieldLocator> qlSteps=new ArrayList<>();
        FieldLocator s1 = new FieldLocator();
        s1.setField("qFields");
        qlSteps.add(s1);
        cf.setIndexSteps(qlSteps);

        CustomizeOptions qlst=new CustomizeOptions();
        cf.setInternal(qlst);
        qlst.setBindings(new ArrayList<>());

        SimpleField binding1=new SimpleField();
        binding1.setName("参数名");
        binding1.setOrder(1);
        binding1.setDesc("查询参数名");
        List<FieldLocator> b1Steps=new ArrayList<>();
        FieldLocator b1s1 = new FieldLocator();
        b1s1.setField("dataField");
        b1Steps.add(b1s1);
        FieldLocator b1s2 = new FieldLocator();
        b1s2.setField("name");
        b1Steps.add(b1s2);
        binding1.setIndexSteps(b1Steps);
        qlst.getBindings().add(binding1);

        SimpleField binding2=new SimpleField();
        binding2.setName("输入类型");
        binding2.setOrder(2);
        binding2.setDesc("参数输入类型 input 输入框，select 列表选择， date 日期选择器");
        List<FieldLocator> b2Steps=new ArrayList<>();
        FieldLocator b2s1 = new FieldLocator();
        b2s1.setField("dataField");
        b2Steps.add(b2s1);
        FieldLocator b2s2 = new FieldLocator();
        b2s2.setField("type");
        b2Steps.add(b2s2);
        binding2.setIndexSteps(b2Steps);
        qlst.getBindings().add(binding2);

        SimpleField binding3=new SimpleField();
        binding3.setName("缺省提示信息");
        binding3.setOrder(3);
        binding3.setDesc("无输入时的提示信息");
        List<FieldLocator> b3Steps=new ArrayList<>();
        FieldLocator b3s1 = new FieldLocator();
        b3s1.setField("shape");
        b3Steps.add(b3s1);
        FieldLocator b3s2 = new FieldLocator();
        b3s2.setField("placeholder");
        b3Steps.add(b3s2);
        binding3.setIndexSteps(b3Steps);
        qlst.getBindings().add(binding3);


        CollectionField cf2=new CollectionField();
        cf2.setName("显示字段");
        cf2.setDesc("显示条件字段列表");
        cf2.setOrder(2);
        List<FieldLocator> tblSteps=new ArrayList<>();
        FieldLocator s2_1 = new FieldLocator();
        s2_1.setField("tableSpec");
        tblSteps.add(s2_1);
        FieldLocator s2_2 = new FieldLocator();
        s2_2.setField("dFields");
        tblSteps.add(s2_2);
        cf2.setIndexSteps(tblSteps);
        inf1.getBindings().add(cf2);

        CustomizeOptions clst=new CustomizeOptions();
        clst.setBindings(new ArrayList<>());
        cf2.setInternal(clst);


        SimpleField tc1=new SimpleField();
        tc1.setName("名称");
        tc1.setOrder(1);
        tc1.setDesc("列显示名称");
        List<FieldLocator> tc1Steps=new ArrayList<>();
        FieldLocator tc1S1 = new FieldLocator();
        tc1S1.setField("title");
        tc1Steps.add(tc1S1);
        tc1.setIndexSteps(tc1Steps);
        clst.getBindings().add(tc1);

        SimpleField tc2=new SimpleField();
        tc2.setName("参数名");
        tc2.setOrder(2);
        tc2.setDesc("结果数据集中的参数名");
        List<FieldLocator> tc2Steps=new ArrayList<>();
        FieldLocator tc2S1 = new FieldLocator();
        tc2S1.setField("name");
        tc2Steps.add(tc2S1);
        tc2.setIndexSteps(tc2Steps);
        clst.getBindings().add(tc2);

        SimpleField tc3=new SimpleField();
        tc3.setName("显示顺序");
        tc3.setOrder(3);
        tc3.setDesc("列的显示顺序");
        List<FieldLocator> tc3Steps=new ArrayList<>();
        FieldLocator tc3S1 = new FieldLocator();
        tc3S1.setField("order");
        tc3Steps.add(tc3S1);
        tc3.setIndexSteps(tc3Steps);
        clst.getBindings().add(tc3);

        spec.getInterfaces().add(inf1);
        String json = JsonUtil.toJson(spec);
        System.out.println(json);
        ComponentSpec ci = JsonUtil.parse(json, ComponentSpec.class);
        System.out.println();
    }
}
