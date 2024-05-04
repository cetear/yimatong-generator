package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
    <#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    <#else >
${indent}DynamicGenerator.doGenerator(inputPath, outputPath, model);
    </#if>
</#macro>

/**
* 核心生成器
*/
public class MainGenerator {

    public static void doGenerate(DataModel model) throws TemplateException, IOException {
        
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";
        
        String inputPath;
        String outputPath;

<#list modelConfig.models as modelInfo>
<#--    有分组-->
        <#if modelInfo.groupKey??>
            <#list modelInfo.models as subModelInfo>
                ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
            </#list>
<#--            没分组-->
            <#else >
                ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
</#list>

<#list fileConfig.files as fileInfo>

<#--    判断是否分组-->
    <#if fileInfo.groupKey??>
        // groupKey = ${fileInfo.groupKey}
<#--        判断组内是否有条件-->
    <#if fileInfo.condition??>
        if(${fileInfo.condition}){
        <#list fileInfo.files as fileInfo>
           <@generateFile indent="        " fileInfo=fileInfo></@generateFile>
        </#list>
        }
<#--        组内没条件-->
    <#else >
        <@generateFile indent="    " fileInfo=fileInfo></@generateFile>
    </#if>
<#--    分组的else-->
    <#else >
        <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
            <@generateFile indent="        " fileInfo=fileInfo></@generateFile>
        }
        <#else>
            <@generateFile indent="    " fileInfo=fileInfo></@generateFile>
        </#if>

    </#if>
</#list>
    }
}
