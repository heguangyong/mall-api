package com.zbkj.admin.config;

import com.google.common.base.Predicate;
import com.zbkj.common.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Swagger配置组件
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "api.doc")
public class SwaggerConfig {

    //是否开启swagger，正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置
    Boolean swaggerEnabled = true;

    @Value("${server.port}")
    private String port;

    @Value("${crmeb.domain}")
    private String domain;

    @Bean("merchant")
    public Docket createRestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("merchant")
                .host(domain)
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.admin.controller.merchant"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(merchantPathsAnt())
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    @Bean("platform")
    public Docket create2RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("platform")
                .host(domain)
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.admin.controller.platform"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(platformPathsAnt()) //只监听
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    @Bean("publicly")
    public Docket create3RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("publicly")
                .host(domain)
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.admin.controller.publicly"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(publicPathsAnt())
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    private Predicate<String> merchantPathsAnt() {
        return PathSelectors.ant("/api/admin/merchant/**");
    }

    private Predicate<String> platformPathsAnt() {
        return PathSelectors.ant("/api/admin/platform/**");
    }

    private Predicate<String> publicPathsAnt() {
        return PathSelectors.ant("/api/publicly/**");
    }

    private List<ApiKey> security() {
        return newArrayList(
                new ApiKey(Constants.HEADER_AUTHORIZATION_KEY, Constants.HEADER_AUTHORIZATION_KEY, "header")
        );
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Crmeb","https://www.crmeb.com/index/java_merchant", "278437628@qq.com");
        return new ApiInfoBuilder()
                .title("Crmeb Java 多商户管理侧")
                .description("Java多商户接口文档")
                .contact(contact)
                .termsOfServiceUrl("https://www.crmeb.com/index/java_merchant")
                .version("1.4.0").build();
    }


    private List<SecurityContext> securityContexts() {
        List<SecurityContext> res = new ArrayList<>();
        res.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build());
        return res;
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> res = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", Constants.HEADER_AUTHORIZATION_KEY);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        res.add(new SecurityReference(Constants.HEADER_AUTHORIZATION_KEY, authorizationScopes));
        return res;
    }
}
