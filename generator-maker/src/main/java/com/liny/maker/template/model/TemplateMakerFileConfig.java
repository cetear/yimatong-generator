package com.liny.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileFilter;
import java.util.List;

@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        private String path;

        private List<FileFilterConfig> filterConfigList;
    }
}
