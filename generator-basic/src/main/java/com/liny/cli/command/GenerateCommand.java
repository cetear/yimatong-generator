package com.liny.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.liny.generator.MainGenerator;
import com.liny.model.MainTemplateConfig;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable {

    /**
     * 填充值,作者
     */
    @CommandLine.Option(names = {"-a", "--author"}, description = "作者名称", arity = "0..1", interactive = true)
    private String author = "Liny";
    /**
     * 输出信息
     */
    @CommandLine.Option(names = {"-o", "--outputText"}, description = "输出文本", arity = "0..1", interactive = true)
    private String outputText = "输出结果";
    /**
     * 是否循环
     */
    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环", arity = "0..1", interactive = true)
    private boolean loop = false;

    @Override
    public Integer call() throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this, mainTemplateConfig);
        MainGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
