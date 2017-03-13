package org.metplus.curriculum.web.controllers;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;


@ActiveProfiles("development")
@Profile("unit-test")
@WebAppConfiguration
@EnableAutoConfiguration
public class BaseControllerTest {

}
