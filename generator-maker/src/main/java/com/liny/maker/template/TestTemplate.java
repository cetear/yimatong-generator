//package com.dexcode.maker.template;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.bean.copier.CopyOptions;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import com.dexcode.maker.meta.Meta;
//import com.dexcode.maker.meta.enums.FileGenerateTypeEnum;
//import com.dexcode.maker.meta.enums.FileTypeEnum;
//
//import java.io.File;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 第七期：模板制作工具
// */
//public class TestTemplate {
//
//    /**
//     * 制作模板（分步能力制作）
//     *
//     * @param newMeta
//     * @param id
//     * @param originProjectPath
//     * @param inputFilePathList
//     * @param modelInfo
//     * @param searchStr
//     * @return
//     */
//    private static long makeTemplate(Meta newMeta, Long id, String originProjectPath, List<String> inputFilePathList, Meta.ModelConfig.ModelInfo modelInfo, String searchStr) {
//        // 没有 id 则生成
//        if (id == null) {
//            id = IdUtil.getSnowflakeNextId();
//        }
//        String projectPath = System.getProperty("user.dir");
//        String tempDirPath = projectPath + File.separator + ".temp";
//        String templatePath = tempDirPath + File.separator + id;
//
//        // 判断：是否为首次制作
//        // 目录不存在，则是首次制作，复制目录
//        if (!FileUtil.exist(templatePath)) {
//            FileUtil.mkdir(templatePath);
//            FileUtil.copy(originProjectPath, templatePath, true);
//        }
//
//        // 一、输入信息
//        // 输入文件信息
//        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
//        // 注意 win 系统需要对路径进行转义
//        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
//
//        // 二、生成文件模板
//        // 输入文件为目录
//        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
//        for (String inputFilePath : inputFilePathList) {
//            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
//            if (FileUtil.isDirectory(inputFileAbsolutePath)) {
//                List<File> fileList = FileUtil.loopFiles(inputFileAbsolutePath);
//                for (File file : fileList) {
//                    Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, modelInfo, searchStr, sourceRootPath);
//                    newFileInfoList.add(fileInfo);
//                }
//            } else {
//                // 输入的是文件
//                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(new File(inputFileAbsolutePath), modelInfo, searchStr, sourceRootPath);
//                newFileInfoList.add(fileInfo);
//            }
//        }
//
//        // 三、生成配置文件
//        String metaOutputPath = sourceRootPath + File.separator + "meta.json";
//
//        // 如果已有meta.json文件，则为非首次制作
//        if (FileUtil.exist(metaOutputPath)) {
//            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
//            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
//            newMeta = oldMeta;
//
//            // 1.追加配置参数
//            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
//            fileInfoList.addAll(newFileInfoList);
//            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
//            modelInfoList.add(modelInfo);
//
//            // 配置去重
//            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
//            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
//        } else {
//            // 1. 构造配置参数
//            Meta.FileConfig fileConfig = new Meta.FileConfig();
//            newMeta.setFileConfig(fileConfig);
//            fileConfig.setSourceRootPath(sourceRootPath);
//            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
//            fileConfig.setFiles(fileInfoList);
//            fileInfoList.addAll(newFileInfoList);
//
//            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
//            newMeta.setModelConfig(modelConfig);
//            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
//            modelConfig.setModels(modelInfoList);
//            modelInfoList.add(modelInfo);
//        }
//        // 2. 输出元信息文件
//        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
//        return id;
//    }
//
//    /**
//     * 单个文件的制作模板
//     *
//     * @param inputFile
//     * @param modelInfo
//     * @param searchStr
//     * @param sourceRootPath
//     * @return
//     */
//    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath) {
//        // 文件 --> 绝对路径 --> 相对路径
//        // 绝对路径
//        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
//        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";
//
//        // 相对路径（用于生成配置）
//        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
//        String fileOutputPath = fileInputPath + ".ftl";
//
//        // 二、使用字符串替换，生成模板文件
//        String fileContent = null;
//        // 如果已有.ftl文件，则为非首次制作
//        if (FileUtil.exist(fileOutputAbsolutePath)) {
//            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
//        } else {
//            // 读取原文件
//            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
//        }
//        String replacement = String.format("${%s}", modelInfo.getFieldName());
//        // 替换新内容
//        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);
//
//        // 将文件配置 fileInfo 的构造提前，无论是新增还是修改元信息都能使用该对象
//        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
//        fileInfo.setInputPath(fileInputPath);
//        fileInfo.setOutputPath(fileOutputPath);
//        fileInfo.setType(FileTypeEnum.FILE.getValue());
//        // fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
//
//        // 和原文件一致，没有挖坑，则为静态生成
//        if (newFileContent.equals(fileContent)) {
//            // 输出路径 = 输入路径
//            fileInfo.setOutputPath(fileInputPath);
//            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
//        } else {
//            // 生成模板文件
//            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
//            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
//        }
//        return fileInfo;
//    }
//
//    public static void main(String[] args) {
//
//        Meta meta = new Meta();
//        meta.setName("acm-template-pro-generator");
//        meta.setDescription("ACM 示例模板生成器");
//
//        String projectPath = System.getProperty("user.dir");
//        String originProjectPath = new File(projectPath).getParent() + File.separator + "dexcode-generator-demo-projects/springboot-init";
//        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
//        String inputFilePath2 = "src/main/java/com/yupi/springbootinit/controller";
//        List<String> inputFilePathList = Arrays.asList(inputFilePath1, inputFilePath2);
//
//        // 模型参数信息（首次）
////        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
////        modelInfo.setFieldName("outputText");
////        modelInfo.setType("String");
////        modelInfo.setDescription("mySum = ");
//
//        // 模型参数信息（第二次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");
//
//        // 替换变量（首次）
////        String searchStr = "Sum: ";
//
//        // 替换变量（第二次）
//        String searchStr = "BaseResponse";
//
//        long id = makeTemplate(meta, 1743561710386155520L, originProjectPath, inputFilePathList, modelInfo, searchStr);
//        System.out.println(id);
//    }
//
//    /**
//     * 文件去重
//     *
//     * @param fileInfoList
//     * @return
//     */
//    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
//        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
//                fileInfoList.stream()
//                        .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r))
//                        .values()
//        );
//        return newFileInfoList;
//    }
//
//    /**
//     * 模型去重
//     *
//     * @param modelInfoList
//     * @return
//     */
//    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
//        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
//                modelInfoList.stream()
//                        .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r))
//                        .values()
//        );
//        return newModelInfoList;
//    }
//}