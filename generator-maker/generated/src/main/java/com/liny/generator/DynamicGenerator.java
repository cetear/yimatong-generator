package com.liny.generator;

import com.liny.model.DataModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态文件生成器
 */
public class DynamicGenerator {

    public static void doGenerator(String srcPath, String destPath, Object model) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        //指定模板文件路径
        File parentFile = new File(srcPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(parentFile);

        configuration.setDefaultEncoding("utf-8");

        //创建模板对象，加载指定模板
        String name = new File(srcPath).getName();
        Template template = configuration.getTemplate(name);

        Writer writer = new FileWriter(destPath);

        template.process(model, writer);

        writer.close();    }
}
