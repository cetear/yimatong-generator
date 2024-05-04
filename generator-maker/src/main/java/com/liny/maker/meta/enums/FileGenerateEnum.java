package com.liny.maker.meta.enums;

public enum FileGenerateEnum {
    DYNAMIC("动态", "dynamic"),
        STATIC("文件", "static");

    private final String text;

    private final String value;

    FileGenerateEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
