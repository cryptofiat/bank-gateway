package eu.cryptoeuro.bankgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/v1/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Kryptoeuro API",
                "REST API for kryptoeuro services",
                "v1",
                "https://api.kryptoeuro.eu/v1",
                "api@kryptoeuro.com",
                "MIT",
                "https://api.kryptoeuro.eu");
        return apiInfo;
    }

}
