package space.nov29.cataria.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import space.nov29.cataria.model.Category;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.Tag("repository")
@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Nested
    @DisplayName("save post test")
    class savePostTest {
        @Test
        @DisplayName("Minimal requirement post create")
        void minimalRequirementPostCreateTest() {
            Post newPost = generateMinimalRequirementPost();

            postRepository.save(newPost);
        }

        @Nested
        @DisplayName("Data restrain test")
        class dataRestrainTest {
            @Test
            @DisplayName("create time null")
            void createTimeNullTest() {
                Post newPost = generateMinimalRequirementPost();
                newPost.setCreateTime(null);

                assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(newPost));
            }

            @Test
            @DisplayName("last edit time null")
            void lastEditTimeNullTest() {
                Post newPost = generateMinimalRequirementPost();
                newPost.setLastEditTime(null);

                assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(newPost));
            }

            @Test
            @DisplayName("title null")
            void titleNullTest() {
                Post newPost = generateMinimalRequirementPost();
                newPost.setTitle(null);

                assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(newPost));
            }

            @Test
            @DisplayName("published state null")
            void publishedNullTest() {
                Post newPost = generateMinimalRequirementPost();
                newPost.setPublished(null);

                assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(newPost));
            }

            @Test
            @DisplayName("duplicated slug")
            void duplicatedSlugTest() {
                final String testSlug = "slug";
                Post post1 = generateMinimalRequirementPost();
                post1.setSlug(testSlug);

                Post post2 = generateMinimalRequirementPost();
                post2.setSlug(testSlug);
                postRepository.save(post1);

                assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(post2));
            }
        }

        @Nested
        @DisplayName("cascade operation test")
        class cascadeOperationTest {
            @Test
            @DisplayName("cascade save category")
            void cascadeSaveCategoryTest() {
                final String categoryName = "category";
                Post post = generateMinimalRequirementPost();
                Category category = new Category();
                category.setName(categoryName);
                category.addPostToPostList(post);
                postRepository.save(post);

                Category savedCategory = categoryRepository.findByName(categoryName).orElse(null);
                assertNotNull(savedCategory);
                assertNotNull(savedCategory.getId());
                assertEquals(savedCategory.getName(), categoryName);
                assertEquals(savedCategory.getPosts().get(0), post);
            }

            @Test
            @DisplayName("cascade save tag")
            void cascadeSaveTagTest() {
                final String tagName = "tag";
                Post post = generateMinimalRequirementPost();
                Tag tag = new Tag(tagName);
                tag.addPostToPostList(post);
                postRepository.save(post);

                Tag savedTag = tagRepository.findByName(tagName).orElse(null);
                assertNotNull(savedTag);
                assertNotNull(savedTag.getId());
                assertEquals(savedTag.getName(), tagName);
                assertEquals(savedTag.getPosts().get(0), post);
            }
        }
    }

    private Post generateMinimalRequirementPost() {
        Instant currentTime = Instant.now();
        Post newPost = new Post();
        newPost.setCreateTime(currentTime);
        newPost.setLastEditTime(currentTime);
        newPost.setTitle(String.format("title %s", UUID.randomUUID()));
        newPost.setPublished(false);
        return newPost;
    }
}
