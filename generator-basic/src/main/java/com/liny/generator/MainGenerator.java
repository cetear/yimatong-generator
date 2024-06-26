package com.liny.generator;

import com.liny.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {

    String mypath = "src/main/resources/template/MainTemplate.java.ftl";

    public static void main(String[] args) throws TemplateException, IOException {
        //数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("Liny");
        mainTemplateConfig.setOutputText("输出结果");
        mainTemplateConfig.setLoop(false);
        doGenerate(mainTemplateConfig);
    }

    public static void doGenerate(Object model) throws TemplateException, IOException {

        String inputRootPath = "D:\\YupiProgram\\yimatong-generator\\demo-projects\\acm-template-pro";
        String outputRootPath = "D:\\YupiProgram\\yimatong-generator";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath, "src/com/liny/acm/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, "src/com/liny/acm/MainTemplate.java").getAbsolutePath();
        DynamicGenerator.doGenerator(inputPath, outputPath, model);

        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();

        StaticGenerator.copyFilesByHutool(inputPath, outputPath);

        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();

        StaticGenerator.copyFilesByHutool(inputPath, outputPath);

    }
}
