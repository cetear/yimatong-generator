package com.liny.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.liny.maker.meta.Meta;
import com.liny.maker.template.enums.FileFilterRangeEnum;
import com.liny.maker.template.enums.FileFilterRuleEnum;
import com.liny.maker.template.model.FileFilterConfig;
import com.liny.maker.template.model.TemplateMakerConfig;
import com.liny.maker.template.model.TemplateMakerFileConfig;
import com.liny.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TemplateMakerTest {

    @Test
    public void makeTemplate() {
    }

    @Test
    public  void testMakeTemplate001() {
        //1.项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 实例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "demo-projects/springboot-init-master/springboot-init-master";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";

        //文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型分组
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
// - 模型组配置
//        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
//        modelGroupConfig.setGroupKey("mysql");
//        modelGroupConfig.setGroupName("数据库配置");
//        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
// - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        TemplateMaker.makeTemplate(meta ,originProjectPath ,templateMakerFileConfig ,templateMakerModelConfig,null, 1L);

    }

    @Test
    public  void testMakeTemplate002() {
        //1.项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 实例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "demo-projects/springboot-init-master/springboot-init-master";
        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";

        //文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型分组
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
// - 模型组配置
//        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
//        modelGroupConfig.setGroupKey("mysql");
//        modelGroupConfig.setGroupName("数据库配置");
//        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
// - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        TemplateMaker.makeTemplate(meta ,originProjectPath ,templateMakerFileConfig ,templateMakerModelConfig,null, 1L);

    }

    @Test
    public void testMakeTemplateJson(){
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    @Test
    public void makeSpringBootTemplate(){
        String rootPath = "example/springboot-init";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        long id = TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker1.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker2.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker3.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker4.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker5.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker6.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);

//        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker7.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "/templateMaker8.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        //System.out.println(id);
    }


}