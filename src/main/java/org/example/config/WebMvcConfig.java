package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.json.JacksonObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
//
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry){
//       super.addResourceHandlers(registry);
//       log.info("--------静态映射start------");
//       registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
//   }
////
////    @Override
////    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
////        log.info("---converter---");
////        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
////        messageConverter.setObjectMapper(new JacksonObjectMapper());
////        converters.add(0, messageConverter);
////    }
//}

    /**
     * 通过knife4j生成接口文档
     * @return
     */
    @Bean
    public Docket docket() {
        log.info("准备生成接口文档");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("在线阅读系统项目接口文档")
                .version("2.0")
                .description("在线阅读系统项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry){
        super.addResourceHandlers(registry);
       // log.info("--------静态映射start------");

        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/swagger-ui/");

        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

//    @Override
//    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info("---converter---");
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        converters.add(0, messageConverter);
//    }
}

