package space.nov29.cataria.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import space.nov29.cataria.model.User;
import static org.junit.jupiter.api.Assertions.*;

@Tag("repository")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRespository userRespository;

    @Nested
    @DisplayName("save user test")
    class saveUserTest {
        @Test
        @DisplayName("minimal requirement user create")
        void minimalRequirementUserCreateTest() {
            User user = new User();
            user.setUsername("username");
            user.setPassword("password");

            userRespository.save(user);
        }

        @Nested
        @DisplayName("data restrain test")
        class dataRestrainTest {
            @Test
            @DisplayName("username null")
            void usernameNullTest() {
                User user = new User();
                user.setPassword("password");

                assertThrows(DataIntegrityViolationException.class, () -> userRespository.save(user));
            }

            @Test
            @DisplayName("password null")
            void passwordNullTest() {
                User user = new User();
                user.setUsername("username");

                assertThrows(DataIntegrityViolationException.class, () -> userRespository.save(user));
            }

            @Test
            @DisplayName("duplicated username")
            void duplicatedUsernameTest() {
                final String testUsername = "username";
                User user1 = new User();
                user1.setUsername(testUsername);
                user1.setPassword("password");

                User user2 = new User();
                user2.setUsername(testUsername);
                user2.setPassword("password");

                userRespository.save(user1);

                assertThrows(DataIntegrityViolationException.class, () -> userRespository.save(user2));
            }
        }
    }
}
