package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * 主命令类，不负责实现功能，只负责绑定子命令
 */
//开启帮助文档
@Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecutor implements  Runnable{

    private final CommandLine commandLine;
    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        System.out.println("请输入具体命令，查看命令 --help");
    }

    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }
}
