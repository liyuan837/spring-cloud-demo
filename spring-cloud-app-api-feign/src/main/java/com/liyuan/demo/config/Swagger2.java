package com.liyuan.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    //通过createRestApi函数创建Docket的Bean
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //默认会将该包下面所有的controller的每一个接口都生成API，除非被@ApiIgnore制定忽略的请求
                .apis(RequestHandlerSelectors.basePackage("com.liyuan.demo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    //apiInfo()用来创建该Api的基本信息（这些基本信息会展现在文档页面中）
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Demo模板项目的Swagger2API接口生成")
                .description("更多相关内容请关注：https://github.com/liyuan837/")
                .termsOfServiceUrl("https://github.com/liyuan837/")
                .contact("演得太逼真")
                .version("1.0")
                .build();
    }

}