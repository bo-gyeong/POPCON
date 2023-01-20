package com.example.popconback.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class swaggerConfig {
//    private ApiInfo swaggerInfo() {
//        return new ApiInfoBuilder().title("IoT API")
//                .description("IoT API Docs").build();
//    }
//
//    @Bean
//    public Docket swaggerApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .consumes(getConsumeContentTypes())
//                .produces(getProduceContentTypes())
//                .apiInfo(swaggerInfo()).select()
//                .apis(RequestHandlerSelectors.basePackage("com.nahwasa.iot.controller"))
//                .paths(PathSelectors.any())
//                .build()
//                .useDefaultResponseMessages(false);
//    }
//
//    private Set<String> getConsumeContentTypes() {
//        Set<String> consumes = new HashSet<>();
//        consumes.add("application/json;charset=UTF-8");
//        consumes.add("application/x-www-form-urlencoded");
//        return consumes;
//    }
//
//    private Set<String> getProduceContentTypes() {
//        Set<String> produces = new HashSet<>();
//        produces.add("application/json;charset=UTF-8");
//        return produces;
//    }
}
