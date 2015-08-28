package org.metplus.curriculum.database;

/**
 * Created by Joao Pereira on 26/08/2015.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettingsTest;
import org.metplus.curriculum.database.domain.SettingsTest;
import org.metplus.curriculum.database.repository.SettingsRepositoryTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.ProfileValueSource;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;


public class TestSuite {
}
