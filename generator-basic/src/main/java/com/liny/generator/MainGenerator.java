package com.liny.generator;

import com.liny.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        //1.静态文件生成
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        System.out.println(projectPath);
        String srcPath =  new File(parentFile , "demo-projects" + File.separator + "acm-template").getAbsolutePath();
        String outPath = projectPath;
        StaticGenerator.copyFilesByHutool(srcPath, outPath);

        //2.动态文件生成

        String DynamicSrcPath = projectPath + File.separator + "src/main/resources/template/MainTemplate.java.ftl";
        String DynamicDestPath = projectPath + File.separator + "acm-template/src/com/generator/acm/MainTemplate.java";

        //数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("Liny");
        mainTemplateConfig.setOutputText("输出结果");
        mainTemplateConfig.setLoop(false);

        DynamicGenerator.doGenerator(DynamicSrcPath, DynamicDestPath, mainTemplateConfig);
    }
}
