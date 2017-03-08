package org.metplus.curriculum.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.metplus.curriculum.web.controllers.BaseController.baseUrlApi;

@Configuration
public class WebMvcConfig extends DelegatingWebMvcConfiguration {
//    @Autowired
//    ApplicationLoginFilter loginFilter;


    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestHandlerMapping(baseUrlApi);
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginFilter).addPathPatterns("/**/authenticate");
//        registry.addInterceptor(tokenAuthenticationFilter).addPathPatterns("**");
//    }
}