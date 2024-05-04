package com.liny.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.liny.generator.MainGenerator;
import com.liny.model.DataModel;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable {


        @Option(names = {"-n", "--needGit"}, arity = "0..1", description = "是否生成.gitignore文件", interactive = true, echo = true)
        private boolean needGit = true;


        @Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否生成循环", interactive = true, echo = true)
        private boolean loop = false;


            /**
            * 核心模板
            */
            static DataModel.MainTemplate mainTemplate = new DataModel.MainTemplate();

            @Command(name = "mainTemplate")
            @Data
            public static class MainTemplateCommand implements Runnable{
            @Option(names = {"-a", "--author"}, arity = "0..1", description = "作者注释", interactive = true, echo = true)
            private String author = "liny";
            @Option(names = {"-o", "--outputText"}, arity = "0..1", description = "输出信息", interactive = true, echo = true)
            private String outputText = "sum = ";
            @Override
            public void run() {
                mainTemplate.author = author;
                mainTemplate.outputText = outputText;
                }
            }

    @Override
    public Integer call() throws Exception {
        if(loop) {
                System.out.println("输入核心模板配置：");
                CommandLine commandLine = new CommandLine(MainTemplateCommand.class);
                commandLine.execute("--author", "--outputText");
        }
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        dataModel.mainTemplate = mainTemplate;
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
