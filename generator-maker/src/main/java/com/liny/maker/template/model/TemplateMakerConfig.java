package com.liny.maker.template.model;

import com.liny.maker.meta.Meta;
import lombok.Data;

@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    /**
     * 原始文件位置
     */
    private String originProjectPath;

    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
