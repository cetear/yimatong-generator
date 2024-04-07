package com.liny.maker.generator.file;

import com.liny.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class AllFileGenerator {

    String mypath = "src/main/resources/template/MainTemplate.java.ftl";

    public static void main(String[] args) throws TemplateException, IOException {
        //数据模型
        DataModel dataModel = new DataModel();
        dataModel.setAuthor("Liny");
        dataModel.setOutputText("输出结果");
        dataModel.setLoop(false);
        doGenerate(dataModel);
    }

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
