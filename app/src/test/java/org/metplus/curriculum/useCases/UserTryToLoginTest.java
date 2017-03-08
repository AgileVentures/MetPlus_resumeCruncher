package org.metplus.curriculum.useCases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.metplus.curriculum.test.BeforeAfterInterface;
import org.metplus.curriculum.test.BeforeAfterRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(Suite.class)
@Suite.SuiteClasses({UserTryToLoginTest.LoginSuccessfull.class,
        UserTryToLoginTest.LoginError.class})
public class UserTryToLoginTest {
    public static class DefaultLoginTest implements BeforeAfterInterface {
        @Rule
        public BeforeAfterRule beforeAfter = new BeforeAfterRule(this);
        protected UserTryToLogin useCase;

        @Override
        public void after() {
        }

        @Override
        public void before() {
            useCase = new UserTryToLogin("username", "password");
        }
    }

    public static class LoginSuccessfull extends DefaultLoginTest {
        @Test
        public void correctUserNameAndPassword_shouldReturnTrue() throws Exception {
            assertTrue(useCase.canUserLogin("username", "password"));
        }

        @Test
        public void changeUserAndPasswordOnConstructor_shouldReturnTrue() throws Exception {
            useCase = new UserTryToLogin("test", "testing");
            assertTrue(useCase.canUserLogin("test", "testing"));
        }
    }

    public static class LoginError extends DefaultLoginTest {
        @Test
        public void incorrectUserNameAndPassword_shouldReturnFalse() throws Exception {
            assertFalse(useCase.canUserLogin("invalid_user", "invalid_password"));
        }
        @Test
        public void incorrectUserName_shouldReturnFalse() throws Exception {
            assertFalse(useCase.canUserLogin("invalid_user", "password"));
        }

        @Test
        public void incorrectPassword_shouldReturnFalse() throws Exception {
            assertFalse(useCase.canUserLogin("username", "invalid_password"));
        }

        @Test
        public void nullUsername_shouldReturnFalse() throws Exception {
            assertFalse(useCase.canUserLogin(null, ""));
        }

        @Test
        public void nullPassword_shouldReturnFalse() throws Exception {
            assertFalse(useCase.canUserLogin("username", null));
        }
    }
}