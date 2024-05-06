package com.liny.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.liny.maker.meta.Meta;
import com.liny.maker.meta.enums.FileGenerateEnum;
import com.liny.maker.meta.enums.FileTypeEnum;
import com.liny.maker.template.enums.FileFilterRangeEnum;
import com.liny.maker.template.enums.FileFilterRuleEnum;
import com.liny.maker.template.model.FileFilterConfig;
import com.liny.maker.template.model.TemplateMakerFileConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板制作工具,根据参数生成模板文件
 */
public class TemplateMaker {
    /**
     * 制作ftl模板
     * @param newMeta
     * @param originProjectPath
     * @param templateMakerFileConfig
     * @param modelInfo
     * @param SearchStr
     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta , String originProjectPath , TemplateMakerFileConfig templateMakerFileConfig, Meta.ModelConfig.ModelInfo modelInfo, String SearchStr, Long id){
        //没有id则生成
        if(id == null){
            id = IdUtil.getSnowflakeNextId();
        }

        //业务逻辑
        //进行工作空间隔离,指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        //进行工作空间隔离,复制目录
        String temDirPath = projectPath + File.separator + ".temp";
        String templatePath = temDirPath + File.separator + id;
        //判断是否是首次制作，判断目录是否存在
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(temDirPath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        //一.输入信息
        //2.输入文件信息
        //String projectPath = System.getProperty("user.dir");
        //挖坑的项目根路径, originProjectPath 最后一层目录 acm-template (这里)
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        //二。生成模板文件
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            String inputFilePath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
            //传入绝对路径
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate( modelInfo, SearchStr, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        //三.生成配置文件meta.json
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //已有meta文件，不是首次制作，在原有基础上修改

        if(FileUtil.exist(metaOutputPath)){
            Meta oldMeta  = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;
            //1.追加配置
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            //配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));

        }else {
            //1.构造配置参数对象
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);
        }
        //2.输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta) , metaOutputPath);
        return id;
    }

    /**
     * 制作模板文件
     * @param modelInfo
     * @param SearchStr
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(Meta.ModelConfig.ModelInfo modelInfo, String SearchStr, String sourceRootPath, File inputFile) {
        // 文件 --> 绝对路径 --> 相对路径
        // 绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 相对路径（用于生成配置）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/","");
        String fileOutputPath = fileInputPath + ".ftl";

        //获取需要修改的文件
        String fileContent = null;
        //文件存在，不是首次制作，在原有模板上挖坑
        if (FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, SearchStr, replacement);


        //文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        //和原文件一致表示没有挖坑
        if(newFileContent.equals(fileContent)){
            //输出路径=输入路径
            fileInfo.setOutputPath(fileInputPath);
            fileInfo.setType(FileGenerateEnum.STATIC.getValue());
        }else {
            fileInfo.setGenerateType(FileGenerateEnum.DYNAMIC.getValue());
            //输出模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }


    public static void main(String[] args) {
        //1.项目基本信息
       //TODO 上次挖坑的消失不见
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 实例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "demo-projects/springboot-init-master/springboot-init-master";

        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1, inputFilePath2);

        //3.输入模型参数信息
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("mySum = ");

        //输入模型参数信息二
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("clasName");
        modelInfo.setType("String");

        //替换变量
        String searchStr = "BaseResponse";
//        String searchStr = "MainTempalte";

        //文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));

        TemplateMaker.makeTemplate(meta ,originProjectPath ,templateMakerFileConfig ,modelInfo , searchStr, 1787340399901253632L);

    }

    /**
     * 文件去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(fileInfoList.stream()
                .collect(
                        //第二个参数表示不做任何修改，第三个参数表示取新值覆盖旧值
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, a -> a, (o, n) -> n)
                ).values()); 
        return newFileInfoList;
    }

    /**
     * 模型去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(modelInfoList.stream()
                .collect(
                        //第二个参数表示不做任何修改，第三个参数表示取新值覆盖旧值
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, a -> a, (o, n) -> n)
                ).values());
        return newModelInfoList;
    }
}
