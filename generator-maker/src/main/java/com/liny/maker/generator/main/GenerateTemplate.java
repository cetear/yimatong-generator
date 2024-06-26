package com.liny.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.liny.maker.generator.JarGenerator;
import com.liny.maker.generator.ScriptGenerator;
import com.liny.maker.generator.file.DynamicFileGenerator;
import com.liny.maker.meta.Meta;
import com.liny.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public abstract class GenerateTemplate {
    public void doGenerate() throws TemplateException, IOException, InterruptedException{
        Meta metaObject = MetaManager.getMetaObject();
        System.out.println(metaObject);

        //输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated";
        if(!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }

        //从原始模板文件路径复制到生成的代码包中
        String sourceCopyDestPath = copySource(metaObject, outputPath);

        //获取模板路径
        generateCode(metaObject, outputPath);

        //构建jar包
        JarGenerator.doGenerate(outputPath);

        //封装脚本
        String shellOutputPath = outputPath + File.separator + "generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", metaObject.getName(), metaObject.getVersion());
        String jarPath = "target/" + jarName;
        ScriptGenerator.doGenerator(shellOutputPath, jarPath);


        //生成精简版（产物）
        buildDist(outputPath, sourceCopyDestPath, shellOutputPath, jarPath);
    }

    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputPath, String jarPath) {
        String distputPath = outputPath + "-dist";
        //拷贝jar包
        String targetAbsolutePath = distputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        //拷贝脚本文件
        FileUtil.copy(shellOutputPath, distputPath, true);
        FileUtil.copy(shellOutputPath + ".bat" , distputPath, true);

        //拷贝模板文件
        FileUtil.copy(sourceCopyDestPath, distputPath, true);
    }

    protected void generateCode(Meta metaObject, String outputPath) throws IOException, TemplateException {
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResource = classPathResource.getAbsolutePath();

        //java包的基础路径
        String outputBasePackage = metaObject.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        //生成model
        inputFilePath = inputResource + File.separator + "template/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        //生成command
        inputFilePath = inputResource + File.separator + "template/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        inputFilePath = inputResource + File.separator + "template/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        inputFilePath = inputResource + File.separator + "template/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        inputFilePath = inputResource + File.separator + "template/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        inputFilePath = inputResource + File.separator + "template/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);


        //生成生成文件类
        inputFilePath = inputResource + File.separator + "template/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        inputFilePath = inputResource + File.separator + "template/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        inputFilePath = inputResource + File.separator + "template/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        //生成pom。xml
        inputFilePath = inputResource + File.separator + "template/pom.xml.ftl";
        outputFilePath = outputPath + "/pom.xml";
        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);

        //生成README.md项目文件
//        inputFilePath = inputResource + File.separator + "template/README.md.ftl";
//        outputFilePath = outputPath + "/README.md";
//        DynamicFileGenerator.doGenerator(inputFilePath, outputFilePath, metaObject);
    }

    protected String copySource(Meta metaObject, String outputPath) {
        String sourceRootPath = metaObject.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }
}
