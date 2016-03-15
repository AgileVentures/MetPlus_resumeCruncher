package org.metplus.curriculum.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.assertEquals;

/**
 * Created by joao on 3/14/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DatabaseConfigTest.UriSet.class})
public class DatabaseConfigTest {

    static public class UriSet{
        @Test
        public void success() {
            DatabaseConfig config = new DatabaseConfig();
            config.setUri("mongodb://username:password@myhost.com:1234/my_db");
            assertEquals("username", config.getUsername());
            assertEquals("password", config.getPassword());
            assertEquals(1234, config.getPort());
            assertEquals("my_db", config.getName());
        }
    }
}
