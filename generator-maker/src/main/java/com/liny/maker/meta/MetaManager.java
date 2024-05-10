package com.liny.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 读取元信息，并转化成meta类，使用单例模式简化效率，不用每次使用都读取一遍
 */
public class MetaManager {

    private static volatile Meta meta;

    public static Meta getMetaObject(){
        if(meta == null){
            synchronized (MetaManager.class){
                if(meta == null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta(){
//        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        System.out.println(meta);
        MetaValidator.doValidAndFill(meta);
        return meta;
    }


}
