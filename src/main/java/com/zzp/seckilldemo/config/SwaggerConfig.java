package com.zzp.seckilldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author zzp
 * @Date 2023/3/25 12:17
 * @Description: Swagger配置类
 * @Version 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket(Environment environment) {
        // 设置项目处于哪些环境时, 需要开启 swagger
        // Profiles.of("dev", "test") 处于 dev test 环境 开启 swagger
        Profiles of = Profiles.of("dev", "test");
        // 判断当前项目所处环境, 如果为 dev test 环境, 返回 true
        // 通过 enable() 接收此参数判断并决定是否显示
        boolean b = environment.acceptsProfiles(of);

        return new Docket(DocumentationType.SWAGGER_2).
                apiInfo(apiInfo()).
                enable(b).
                select().
                // 通过 .select() 方法去配置扫描接口, RequestHandlerSelectors 配置如何扫描接口
                apis(RequestHandlerSelectors.basePackage("com.zzp.seckilldemo.controller")).
                build();
    }

    // 配置文档信息
    public ApiInfo apiInfo() {
        // contact 联系人信息
        Contact contact = new Contact("codeRoc", "", "");
        return new ApiInfoBuilder()
                .title("秒杀系统")
                .description("秒杀系统接口管理")
                .version("v1.0")
                .contact(contact)
                .build();
    }
}
