import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;


public class FreeMarkerTest {

    @Test
    public void test() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        //指定模板文件路径
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/template"));

        configuration.setDefaultEncoding("utf-8");

        //创建模板对象，加载指定模板
        Template template = configuration.getTemplate("myweb.html.ftl");

        //数据模型
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2024);
        List<Map<String, Object>> menuItems = new ArrayList<>();
        Map<String, Object> menuItem1 = new HashMap<>();
        menuItem1.put("url", "asdadadad");
        menuItem1.put("label", "标签1");
        Map<String, Object> menuItem2 = new HashMap<>();
        menuItem2.put("url", "asdadas");
        menuItem2.put("label", "标签2");
        menuItems.add(menuItem1);
        menuItems.add(menuItem2);
        dataModel.put("menuItems", menuItems);

        Writer writer = new FileWriter("myweb.html");

        template.process(dataModel, writer);

        writer.close();
    }

}
