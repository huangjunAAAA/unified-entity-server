create table ${table.name}
(
    guid STRING,
    <#list table.cols as x>
    ${sutil.toUnderlineCase(x.nameEn)}  ${x.jdbcType} <#if x.nameZh??>comment "${x.nameZh}"</#if> <#if x_has_next>,</#if>
    </#list>
<#if table.indices?has_content >,</#if>
    <#list singleIdx as idx>
        INDEX ${table.name+"_"+sutil.toUnderlineCase(idx)} (${sutil.toUnderlineCase(idx)}) USING INVERTED  <#if idx_has_next>,</#if>
    </#list>
) ENGINE=olap
PARTITION BY RANGE(${table.partitionCol}) ()
DISTRIBUTED BY HASH(${table.hashCol})
PROPERTIES
(
    <#list table.properties?keys as k>
        "${k}" = "${table.properties[k]}" <#if k_has_next>,</#if>
    </#list>
);
