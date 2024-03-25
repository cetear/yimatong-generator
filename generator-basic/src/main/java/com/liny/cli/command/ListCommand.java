package com.liny.cli.command;

import cn.hutool.core.io.FileUtil;
import com.liny.generator.StaticGenerator;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        //整个项目的根路径
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String srcPath =  new File(parentFile , "demo-projects" + File.separator + "acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(srcPath);
        for(File file : files){
            System.out.println(file);
        }
    }
}
