package com.liny.maker.template.model;

import com.liny.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileFilter;
import java.util.List;

@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    //文件分组配置
    private FileGroupConfig fileGroupConfig;


    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        private String path;

        private List<FileFilterConfig> filterConfigList;
    }

    @Data
    @NoArgsConstructor
    public static class FileGroupConfig {

        private String condition;

        private String groupKey;

        private String groupName;

    }
}
