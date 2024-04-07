package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        //查看目录下文件列表
        String projectPath = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(projectPath);
        for(File file : files){
            System.out.println(file);
        }
    }
}
