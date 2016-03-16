package org.metplus.curriculum.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.Application;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.metplus.curriculum.web.controllers.auth.AuthenticationController;

/**
 * Created by Joao Pereira on 03/11/2015.
 */

@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class, AdminController.class, CurriculumController.class, AuthenticationController.class})
@WebAppConfiguration
@SpringBootApplication
@SpringApplicationConfiguration(classes = Application.class)
public class BaseControllerTest {

    @Autowired

    @Test
    public void dummy() throws Exception {

    }
}
