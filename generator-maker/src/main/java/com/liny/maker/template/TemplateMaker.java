package com.liny.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
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
import com.liny.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
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
     * @param templateMakerModelConfig
     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta , String originProjectPath , TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, Long id){
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
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        //转化为配置文件接受的modelinfo对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());
        //本次新增的模型列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        //如果是模型组
        //获取分组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        //文件组不为空
        if(modelGroupConfig != null){
            String condition = modelGroupConfig.getCondition();
            String groupName = modelGroupConfig.getGroupName();
            String groupKey = modelGroupConfig.getGroupKey();

            //设置一个分组的信息
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setCondition(condition);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setGroupKey(groupKey);
            //把前面遍历到的模型放到一个分组内
            groupModelInfo.setModels(inputModelInfoList);

            //重置遍历到的模型列表，把设置好的分组模型当成一个普通文件加进去
            newModelInfoList = new ArrayList<>();
            newModelInfoList.add(groupModelInfo);
        }else {
            //不分组，添加索引的模型信息列表
            newModelInfoList.addAll(inputModelInfoList);
        }


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
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate( templateMakerModelConfig, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        //如果是文件组
        //获取分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        //文件组不为空
        if(fileGroupConfig != null){
            String condition = fileGroupConfig.getCondition();
            String groupName = fileGroupConfig.getGroupName();
            String groupKey = fileGroupConfig.getGroupKey();

            //设置一个分组的信息
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setGroupKey(groupKey);
            //把前面遍历到的文件放到一个分组内
            groupFileInfo.setFiles(newFileInfoList);

            //重置遍历到的文件列表，把设置好的分组文件当成一个普通文件加进去
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        
        //处理模型信息
       

        //三.生成配置文件meta.json
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        //已有meta文件，不是首次制作，在原有基础上修改
        
        if(FileUtil.exist(metaOutputPath)){
            newMeta  = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, newMeta, CopyOptions.create().ignoreNullValue());
            //1.追加配置
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

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
            modelInfoList.addAll(newModelInfoList);
        }
        //2.输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta) , metaOutputPath);
        return id;
    }

    /**
     * 制作模板文件
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile) {
        // 文件 --> 绝对路径 --> 相对路径
        // 绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 相对路径（用于生成配置）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/","");
        String fileOutputPath = fileInputPath + ".ftl";

        //对于同一个文件，遍历模型，进行多轮替换
        //获取需要修改的文件
        String fileContent = null;
        //文件存在，不是首次制作，在原有模板上挖坑
        if (FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String  replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            //判断模型配置是否分组
            //不是分组
            if(modelGroupConfig == null){
                replacement = String.format("${%s}", fieldName);
            }else {
                //有分组
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, fieldName);
            }
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

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
        String inputFilePath2 = "src/main/resources/application.yml";
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

        //分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfige = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfige.setCondition("outputText");
        fileGroupConfige.setGroupName("测试分组2");
        fileGroupConfige.setGroupKey("test02");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfige);

        // 模型分组
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
// - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

// - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        TemplateMaker.makeTemplate(meta ,originProjectPath ,templateMakerFileConfig ,templateMakerModelConfig, 1787340399901253632L);

    }

    /**
     * 文件去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        //1。将文件分为有分组和无分组
        //根据groupKey过滤
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                //过滤掉没有分组的
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                //按照groupkey分组
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        //2.对于有分组，同组对象放一起，在去重，不同分组可同时保留
        //同组内配置合并
        //定义合并后的对面map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergeFileInfo = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            //文件列表
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            //将文件列表内文件打平,并合并
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>( tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            //第二个参数表示不做任何修改，第三个参数表示取新值覆盖旧值
                            Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, a -> a, (o, n) -> n)
                    ).values());

            //使用新的group配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergeFileInfo.put(groupKey, newFileInfo);
        }

        //3.创建新的文件列表，合并后的结果加入
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergeFileInfo.values());
        //4.将无分组的文件加入

        resultList.addAll( new ArrayList<>(fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(
                        //第二个参数表示不做任何修改，第三个参数表示取新值覆盖旧值
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, a -> a, (o, n) -> n)
                ).values()));

        return resultList;
    }

    /**
     * 模型去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        //1。将模型分为有分组和无分组
        //根据groupKey过滤
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                //过滤掉没有分组的
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                //按照groupkey分组
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        //2.对于有分组，同组对象放一起，在去重，不同分组可同时保留
        //同组内配置合并
        //定义合并后的对面map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergeModelInfo = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            //模型列表
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            //将模型列表内模型打平,并合并
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>( tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(
                            //第二个参数表示不做任何修改，第三个参数表示取新值覆盖旧值
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, a -> a, (o, n) -> n)
                    ).values());

            //使用新的group配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergeModelInfo.put(groupKey, newModelInfo);
        }

        //3.创建新的模型列表，合并后的结果加入
        ArrayList<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergeModelInfo.values());
        //4.将无分组的模型加入

        resultList.addAll( new ArrayList<>(modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(
                        //第二个参数表示不做任何修改，第三个参数表示取新值覆盖旧值
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, a -> a, (o, n) -> n)
                ).values()));

        return resultList;
    }
}
