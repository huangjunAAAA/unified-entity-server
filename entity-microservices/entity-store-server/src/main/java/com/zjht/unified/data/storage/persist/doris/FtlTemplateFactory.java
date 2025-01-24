package com.zjht.unified.data.storage.persist.doris;

import cn.hutool.core.io.resource.ResourceUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.wukong.core.weblog.utils.Charsets;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;

@Configuration
@Slf4j
public class FtlTemplateFactory {

    private static freemarker.template.Configuration configuration;

    static {
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        configuration = new freemarker.template.Configuration(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setTemplateLoader(templateLoader);
        configuration.setDefaultEncoding(Charsets.UTF_8_NAME);
        configuration.setClassForTemplateLoading(DorisDDLService.class, StringPool.SLASH);
    }

    @Bean("create-table-template")
    public Template createTbl() throws IOException {
        BufferedReader r = ResourceUtil.getReader("doris_create_table.ftl", Charset.forName("utf8"));
        Template template = new Template("doris_create_table.ftl", r, configuration);

        r.close();
        return template;
    }

    @Bean("create-pipe-template")
    public Template createPipe() throws IOException{
        BufferedReader r = ResourceUtil.getReader("doris_kafka_pipe.ftl", Charset.forName("utf8"));
        Template template = new Template("doris_kafka_pipe.ftl", r, configuration);
        r.close();
        return template;
    }
}
