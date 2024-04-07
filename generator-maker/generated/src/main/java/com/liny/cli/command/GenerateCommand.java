package com.liny.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.liny.model.DataModel;
import com.liny.generator.MainGenerator;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable {


        @CommandLine.Option(names = {"-l", "--loop"}, description = "是否生成循环",  arity = "0..1", interactive = true)
        private boolean loop = false;

        @CommandLine.Option(names = {"-a", "--author"}, description = "作者注释",  arity = "0..1", interactive = true)
        private String author = "liny";

        @CommandLine.Option(names = {"-o", "--outputText"}, description = "输出信息",  arity = "0..1", interactive = true)
        private String outputText = "sum = ";


    @Override
    public Integer call() throws TemplateException, IOException {
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
