package space.nov29.cataria.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import space.nov29.cataria.model.Category;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Nested
    @DisplayName("save test")
    class saveTest {
        @Test
        @DisplayName("minimal requirement save")
        void minimalRequirementSaveTest() {
            Category category = new Category();
            category.setName("category");

            categoryRepository.save(category);
        }

        @Nested
        @DisplayName("data restrain test")
        class dataRestrainTest {
            @Test
            @DisplayName("category name null")
            void categoryNameNullTest() {
                Category category = new Category();

                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category));
            }

            @Test
            @DisplayName("duplicated name")
            void duplicatedCategoryNameTest() {
                final String name = "name";
                Category category1 = new Category(name);
                Category category2 = new Category(name);

                categoryRepository.save(category1);
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category2));
            }
        }
    }
}
