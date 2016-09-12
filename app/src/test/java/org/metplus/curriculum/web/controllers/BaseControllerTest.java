package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.Application;
import org.metplus.curriculum.api.WebMvcConfig;
import org.metplus.curriculum.security.SecurityConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Joao Pereira on 03/11/2015.
 */

@ActiveProfiles("development")
@Profile("unit-test")
@ContextConfiguration(loader=SpringApplicationContextLoader.class, classes = {Application.class, SecurityConfig.class, WebMvcConfig.class})
@WebAppConfiguration
@SpringBootApplication
@SpringApplicationConfiguration(classes = {Application.class, WebMvcConfig.class}, locations = "resources/", initializers = ConfigFileApplicationContextInitializer.class)
@EnableAutoConfiguration
@EnableConfigurationProperties
public class BaseControllerTest {

}
