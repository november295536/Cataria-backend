package space.nov29.cataria.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import space.nov29.cataria.model.Tag;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Nested
    @DisplayName("save test")
    class saveTest {
        @Test
        @DisplayName("minimal requirement save")
        void minimalRequirementSaveTest() {
            Tag tag = new Tag("tagName");

            tagRepository.save(tag);
        }

        @Nested
        @DisplayName("data restrain test")
        class dataRestrainTest {
            @Test
            @DisplayName("tag name null")
            void tagNameNullTest() {
                Tag tag = new Tag();

                assertThrows(DataIntegrityViolationException.class, () -> tagRepository.save(tag));
            }

            @Test
            @DisplayName("duplicated name")
            void duplicatedTagNameTest() {
                final String name = "name";
                Tag tag1 = new Tag(name);
                Tag tag2 = new Tag(name);

                tagRepository.save(tag1);
                assertThrows(DataIntegrityViolationException.class, () -> tagRepository.save(tag2));
            }
        }
    }
}
