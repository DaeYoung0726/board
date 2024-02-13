package com.Board.project_board.config;


import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(@NotNull ViewResolverRegistry registry) {          // .mustache 로 만들면 안해도됨.
        MustacheViewResolver resolver = new MustacheViewResolver();   // 머스태치 재설정
        resolver.setCharset("UTF-8");                   // 인코딩 설정
        resolver.setContentType("text/html; charset=UTF-8");  // 던지는 데이터는 html파일 그리고, html파일은 UTF-8
        resolver.setPrefix("classpath:/templates/"); // 위치
        resolver.setSuffix(".html");            // .html파일을 머스태치가 인식

        registry.viewResolver(resolver);   // resolver로 뷰리졸버로 등록.
    }

}