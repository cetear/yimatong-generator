package com.liny.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 静态文件生成器
 */

public class StaticGenerator {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        System.out.println(projectPath);
        String srcPath =  new File(parentFile , "demo-projects" + File.separator + "acm-template").getAbsolutePath();
        String outPath = projectPath;
        copyFilesByHutool(srcPath, outPath);
    }

    /**
     * 拷贝文件(使用hutool)
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     */
    public static void copyFilesByHutool(String srcPath, String destPath){
        FileUtil.copy(srcPath, destPath, false);
    }
}
