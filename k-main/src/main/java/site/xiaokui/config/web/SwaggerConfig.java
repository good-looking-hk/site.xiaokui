package site.xiaokui.config.web;//package site.xiaokui.config.web;
//
//import io.swagger.annotations.ApiOperation;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///**
// * @author HK
// * @date 2018-06-24 01:39
// */
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2).apiInfo(this.apiInfo()).select()
//                // 通过注解扫描确定要显示的接口，另一种是通过包扫描如.apis(RequestHandlerSelectors.basePackage("site.xiaokui.module"))
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .paths(PathSelectors.any()).build();
//    }
//
//    private ApiInfo apiInfo() {
//        Contact contact = new Contact("好看的HK", "www.xiaokui.site/about", "467914950@qq.com");
//        return new ApiInfoBuilder().title("小葵博客平台 v2.0.0").description("小葵博客品台 API文档")
//                .termsOfServiceUrl("http://www.xiaokui.site/about").contact(contact).version("2.0.0").build();
//    }
//}
