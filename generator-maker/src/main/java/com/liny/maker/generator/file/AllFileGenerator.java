package com.liny.maker.generator.file;


import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class AllFileGenerator {

    String mypath = "src/main/resources/template/MainTemplate.java.ftl";


    public static void doGenerate(Object model) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        //整个项目的根路径
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String srcPath =  new File(parentFile , "demo-projects" + File.separator + "acm-template").getAbsolutePath();
        String outPath = projectPath;
        //1.静态文件生成
        StaticFileGenerator.copyFilesByHutool(srcPath, outPath);

        //2.动态文件生成

        String DynamicSrcPath = projectPath + File.separator + "src/main/resources/template/MainTemplate.java.ftl";
        String DynamicDestPath = projectPath + File.separator + "acm-template/src/com/generator/acm/MainTemplate.java";

        DynamicFileGenerator.doGenerator(DynamicSrcPath, DynamicDestPath, model);
    }
}
