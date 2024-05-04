package com.liny.maker.model;

import lombok.Data;

/**
 * 静态模板配置
 */
@Data
public class DataModel {

    /**
     * 是否生成.gitignore文件
     */
    private boolean needGit = false;

    /**
     * 是否循环
     */
    private boolean loop = false;


    /**
     * 核心模板
     */
    private MainTemplate mainTemplate = new MainTemplate();

    @Data
    public static class MainTemplate {
        /**
         * 填充值,作者
         */
        private String author = "Liny";
        /**
         * 输出信息
         */
        private String outputText = "输出结果";
    }

}
