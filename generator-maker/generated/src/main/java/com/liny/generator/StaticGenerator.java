package com.liny.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 静态文件生成器
 */

public class StaticGenerator {
    /**
     * 拷贝文件(使用hutool)
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     */
    public static void copyFilesByHutool(String srcPath, String destPath){
        FileUtil.copy(srcPath, destPath, false);
    }
}
