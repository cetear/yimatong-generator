package com.liny.maker.template.model;

import com.liny.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;

    //文件分组配置
    private ModelGroupConfig modelGroupConfig;


    @Data
    @NoArgsConstructor
    public static class ModelInfoConfig {

            private String fieldName;

            private String type;

            private String description;

            private Object defaultValue;

            private String abbr;

        /**
         * 用于替换哪些文本你
         */
        private String replaceText;
        }

    @Data
    @NoArgsConstructor
    public static class ModelGroupConfig {

        private String condition;

        private String groupKey;

        private String groupName;

    }
}
