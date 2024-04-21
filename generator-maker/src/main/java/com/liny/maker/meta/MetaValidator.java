package com.liny.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class MetaValidator {

    public static void doValidAndFill(Meta meta){
        //基础信息校验和默认值
        String name = meta.getName();
        if(StrUtil.isBlank(name)){
            name = "my-generator";
            meta.setName(name);
        }
        String description = meta.getDescription();
        if(StrUtil.isEmpty(description)){
            description = "模板代码生成器";
            meta.setDescription(description);
        }
        String basePackage = meta.getBasePackage();
        if(StrUtil.isBlank(basePackage)){
            basePackage = "com.liny";
            meta.setBasePackage(basePackage);
        }
        String version = meta.getVersion();
        if(StrUtil.isEmpty(version)){
            version = "1.0";
            meta.setVersion(version);
        }
        String author = meta.getAuthor();
        if(StrUtil.isEmpty(name)){
            name = "Liny";
            meta.setAuthor(name);
        }
        String createTime = meta.getCreateTime();
        if (StrUtil.isEmpty(createTime)){
            createTime = DateUtil.now();
            meta.setCreateTime(createTime);
        }

        //fileConfig 校验和默认值

        Meta.FileConfig fileConfig = meta.getFileConfig();
        if(fileConfig != null){
            String sourceRootPath = fileConfig.getSourceRootPath();
            if(StrUtil.isBlank(sourceRootPath)){
                throw new MetaException("未填写SourceRootPath");
            }

            String inputRootPath = fileConfig.getInputRootPath();
            if(StrUtil.isEmpty(inputRootPath)){
                String defaultInputRootPath = ".source/" +
                        FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
                fileConfig.setInputRootPath(defaultInputRootPath);
            }
            //当前路径下generated
            String outputRootPath = fileConfig.getOutputRootPath();
            if(StrUtil.isEmpty(outputRootPath)){
                String defaultOutputRootPath = "generated";
                fileConfig.setOutputRootPath(defaultOutputRootPath);
            }

            String fileConfigType = fileConfig.getType();
            if(StrUtil.isEmpty(fileConfigType)){
                String defaultType = "dir";
                fileConfig.setType(defaultType);
            }

            List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
            if(CollUtil.isNotEmpty(files)){
                for (Meta.FileConfig.FileInfo file : files) {
                    //inputPath
                    String inputPath = file.getInputPath();
                    if(StrUtil.isBlank(inputPath)){
                        throw new MetaException("未填写inputPath");
                    }
                    String outputPath = file.getOutputPath();
                    if(StrUtil.isEmpty(outputPath)){
                        file.setOutputPath(inputPath);
                    }
                    String type = file.getType();
                    if(StrUtil.isBlank(type)){
                        if(StrUtil.isBlank(FileUtil.getSuffix(inputPath))){
                            file.setType("dir");
                        }else{
                            file.setType("file");
                        }
                    }
                    //判断是否ftl，是就是动态，不是就是静态
                    String generateType = file.getGenerateType();
                    if(StrUtil.isBlank(generateType)){
                        if(inputPath.endsWith(".ftl")){
                            file.setGenerateType("dynamic");
                        }else {
                            file.setGenerateType("static");
                        }
                    }
                }
            }

        }

        //modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if(modelConfig != null){
            List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
            if(CollUtil.isNotEmpty(models)){
                for (Meta.ModelConfig.ModelInfo model : models) {
                    String fieldName = model.getFieldName();
                    if(StrUtil.isBlank(fieldName)){
                        throw new MetaException("未填写fileName");
                    }

                    String type = model.getType();
                    if(StrUtil.isEmpty(type)){
                        model.setType("String");
                    }
                }
            }
        }
    }
}
