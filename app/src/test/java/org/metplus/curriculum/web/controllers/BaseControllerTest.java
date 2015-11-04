package org.metplus.curriculum.web.controllers;

import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Joao Pereira on 03/11/2015.
 */

@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class, AdminController.class})
@WebAppConfiguration
public class BaseControllerTest {
}
