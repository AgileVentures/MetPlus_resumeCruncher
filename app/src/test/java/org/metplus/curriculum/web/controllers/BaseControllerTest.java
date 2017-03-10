package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.Application;
import org.metplus.curriculum.api.WebMvcConfig;
import org.metplus.curriculum.security.SecurityConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;


@ActiveProfiles("development")
@Profile("unit-test")
//@ContextConfiguration(loader=SpringBootContextLoader.class, classes = {Application.class, SecurityConfig.class, WebMvcConfig.class})
//@WebAppConfiguration
//@SpringBootApplication
//@SpringBootConfiguration(classes = {Application.class, WebMvcConfig.class}, locations = "resources/", initializers = ConfigFileApplicationContextInitializer.class)
//@SpringBootTest(classes = {Application.class, SecurityConfig.class})
@WebAppConfiguration
@EnableAutoConfiguration
//@EnableConfigurationProperties
public class BaseControllerTest {

}
