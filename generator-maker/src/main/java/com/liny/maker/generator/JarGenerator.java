package com.liny.maker.generator;

import java.io.*;

public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        //调用process类执行maven的打包
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";

        ProcessBuilder processBuilder = new ProcessBuilder(winMavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        //获取执行命令的输出
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = bufferedReader.readLine()) != null){
            System.out.println(line);
        }

        int i = process.waitFor();
        System.out.println("命令退出码：" + i);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("D:\\YupiProgram\\yimatong-generator\\generator-basic");
    }

}
