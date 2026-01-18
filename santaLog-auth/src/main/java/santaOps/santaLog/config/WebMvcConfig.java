package santaOps.santaLog.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private String resourcePath = "file:///C:/Users/dnjft/SpringProject/santaLog-dev/src/main/resources/static/img/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /img/** 로 들어오는 요청을 위 경로로 매핑
        registry.addResourceHandler("/img/**")
                .addResourceLocations(resourcePath);
    }
}