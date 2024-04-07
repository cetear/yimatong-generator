package com.liny.maker.model;

import lombok.Data;

/**
 * 静态模板配置
 */
@Data
public class DataModel {
    /**
     * 填充值,作者
     */
    private String author = "Liny";
    /**
     * 输出信息
     */
    private String outputText = "输出结果";
    /**
     * 是否循环
     */
    private boolean loop = false;
}
