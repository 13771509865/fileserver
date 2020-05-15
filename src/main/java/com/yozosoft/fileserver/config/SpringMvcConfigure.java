package com.yozosoft.fileserver.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yozosoft.fileserver.interceptor.SignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhoufeng
 * @description spring mvc 配置类
 * @create 2020-05-06 14:39
 **/
@Configuration
@EnableWebMvc
public class SpringMvcConfigure implements WebMvcConfigurer {

    @Autowired
    private SignInterceptor signInterceptor;

    /**
     * @description 配置静态资源, 避免静态资源被拦截
     * @author zhoufeng
     * @date 2020/5/6
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //第一个方法设置访问路径前缀，第二个方法设置资源路径
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 解决 SWAGGER 404报错
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * @description 配置拦截器
     * @author zhoufeng
     * @date 2020/5/6
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signInterceptor).addPathPatterns("/api/file/**").excludePathPatterns("/api/file/localDownload/**");
    }

    /**
     * @description 允许跨域
     * @author zhoufeng
     * @date 2020/5/6
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*").allowCredentials(true).maxAge(3600);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //忽略后缀匹配
        configurer.favorPathExtension(false);
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() { //统一编码
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    /**
     * 返回值为空不返回
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL).registerModule(simpleModule);
    }

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyConverter());
        //解决处理中文问题后接口500问题
        converters.add(messageConverter());
    }
}
