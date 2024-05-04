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

public class MainGenerator extends GenerateTemplate {

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}
