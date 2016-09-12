package org.metplus.curriculum.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.metplus.curriculum.web.controllers.BaseController.baseUrlApi;

/**
 * Created by joao on 9/12/16.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestHandlerMapping(baseUrlApi);
    }
}