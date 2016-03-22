package org.metplus.curriculum.web.controllers;

import org.junit.runner.RunWith;
import org.metplus.curriculum.Application;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * Created by Joao Pereira on 03/11/2015.
 */

@ActiveProfiles("development")
@Profile("unit-test")
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class, AdminController.class, CurriculumController.class, AuthenticationController.class, ResumeCruncher.class},)
//@ContextConfiguration(loader=AnnotationConfigWebContextLoader.class, classes = {Application.class, SecurityConfig.class})
@ContextConfiguration(loader=SpringApplicationContextLoader.class, classes = {Application.class, SecurityConfig.class})
@WebAppConfiguration
@SpringBootApplication
@SpringApplicationConfiguration(classes = Application.class, locations = "resources/", initializers = ConfigFileApplicationContextInitializer.class)
@EnableAutoConfiguration
@EnableConfigurationProperties
public class BaseControllerTest {

}
