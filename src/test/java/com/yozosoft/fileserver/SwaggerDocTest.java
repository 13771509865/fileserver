package com.yozosoft.fileserver;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.nio.file.Paths;

/**
 * @author zhoufeng
 * @description
 * @create 2020-05-19 13:05
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class SwaggerDocTest {

    @Test
    public void generate() throws Exception {
        //    输出Ascii格式
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                //MarkupLanguage.CONFLUENCE_MARKUP
//                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
//                .withMarkupLanguage(MarkupLanguage.CONFLUENCE_MARKUP)
                .withOutputLanguage(Language.ZH)
                .withPathsGroupedBy(GroupBy.TAGS)
                .withGeneratedExamples()
                .withoutInlineSchema()
                .build();

        URL url = new URL("http://localhost:9001/v2/api-docs");
        Swagger2MarkupConverter.from(url)
                .withConfig(config)
                .build()
//                .toFolder(Paths.get("D:\\fcs\\"));
                .toFile(Paths.get("D:/fileserver"));
    }
}
