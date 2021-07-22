package space.nov29.cataria.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import space.nov29.cataria.dto.PostDto;
import space.nov29.cataria.exception.PostNotFoundException;
import space.nov29.cataria.model.Category;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;
import space.nov29.cataria.model.User;
import space.nov29.cataria.repository.CategoryRepository;
import space.nov29.cataria.repository.PostRepository;
import space.nov29.cataria.repository.TagRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@org.junit.jupiter.api.Tag("Service")
@DisplayName("PostsService test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class PostsServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    private PostsService postsService;

    @Captor
    ArgumentCaptor<Post> postArgumentCaptor;

    @BeforeAll
    static void postsServiceTestInit() {
        System.out.println("PostsService test start!");
    }

    @DisplayName("Create post test")
    @Nested
    class createPostTest{
        @Test
        @DisplayName("Create post")
        void createNormalPostTest() {
            //given
            PostDto postDto = generateTestingPostDto();
            Tag tag = new Tag(postDto.getTags().get(1));
            tag.setId(1L);
            given(tagRepository.findByName(postDto.getTags().get(0))).willReturn(Optional.empty());
            given(tagRepository.findByName((postDto.getTags().get(1)))).willReturn(Optional.of(tag));
            given(tagRepository.findByName((postDto.getTags().get(2)))).willReturn(Optional.empty());
            given(categoryRepository.findByName(postDto.getCategory())).willReturn(Optional.empty());

            //when
            postsService.createPost(postDto);

            //then
            Mockito.verify(postRepository).save(postArgumentCaptor.capture());
            Post savedPost = postArgumentCaptor.getValue();

            assertNull(savedPost.getId(), "ID not match");
            assertEquals(savedPost.getTitle(), postDto.getTitle(), "title not match");
            assertEquals(savedPost.getSlug(), postDto.getSlug(), "slug not match");
            assertEquals(savedPost.getContent(), postDto.getContent(), "content not match");
            assertEquals(savedPost.getPublished(), postDto.getPublished(), "published state not match");
            assertEquals(savedPost.getCreateTime(), postDto.getCreateTime(), "created time not match");
            assertEquals(savedPost.getLastEditTime(), postDto.getLastEditTime(), "last edited time not match");
            assertEquals(savedPost.getPublishedTime(), postDto.getPublishedTime(), "published time not match");
            assertEquals(savedPost.getCategory(), new Category(postDto.getCategory()), "category not match");
            assertTrue(savedPost.getTags().contains(tag), "tag2 not match");
            assertTrue(savedPost.getTags().contains(new Tag(postDto.getTags().get(0))), "tag1 not match");
            assertTrue(savedPost.getTags().contains(new Tag(postDto.getTags().get(2))), "tag3 not match");
        }
    }
    @Nested
    @DisplayName("update post test")
    class updatePostTest {
        @Test
        @DisplayName("update not existing post")
        void updateNotExistingPostTest() {
            //given
            PostDto postDto = generateTestingPostDto();
            postDto.setId(1L);
            given(postRepository.findById(1L)).willReturn(Optional.empty());

            //then
            assertThrows(PostNotFoundException.class, () -> postsService.updatePost(postDto));
        }

        @Test
        @DisplayName("update published state to true")
        void updatePublishedStateToTrueTest() {
            //given
            final Long postId = 1L;
            PostDto postDto = generateTestingPostDto();
            postDto.setId(postId);
            postDto.setPublished(true);

            Post post = new Post();
            post.setId(postId);
            post.setPublished(false);
            given(postRepository.findById(postId)).willReturn(Optional.of(post));

            //when
            postsService.updatePost(postDto);

            //then
            Mockito.verify(postRepository).save(postArgumentCaptor.capture());
            Post savedPost = postArgumentCaptor.getValue();

            assertNotNull(savedPost.getPublishedTime());
            assertTrue(savedPost.getPublishedTime().isAfter(postDto.getCreateTime()));
            assertEquals(savedPost.getLastEditTime(), savedPost.getPublishedTime());
        }
    }

    @Nested
    @DisplayName("get single post test")
    class getSinglePostTest {
        @Test
        @DisplayName("use number string as slug")
        void useNumberStringAsSlugTest() {
            //given
            final String numberSlug = "1";
            Post post = new Post();
            post.setPublished(true);
            post.setTags(new HashSet<>());
            given(postRepository.findBySlug(numberSlug)).willReturn(Optional.empty());
            given(postRepository.findById(Long.parseLong(numberSlug))).willReturn(Optional.of(post));

            //then
            assertEquals(postsService.getSinglePost(numberSlug), new PostDto(post));
        }

        @Test
        @DisplayName("get unpublished post without login")
        void getUnpublishedPostWithoutLoginTest() {
            //given
            SecurityContextHolder.getContext().setAuthentication(null);
            Post post = new Post();
            post.setPublished(false);
            post.setTags(new HashSet<>());
            given(postRepository.findBySlug(anyString())).willReturn(Optional.of(post));

            //then
            assertThrows(PostNotFoundException.class, () -> postsService.getSinglePost("test"));
        }

        @Test
        @DisplayName("get unpublished post with authentication")
        void getUnpublishedPostWithAuthentication() {
            //given
            User user = new User();
            user.setUsername("name");
            user.setPassword("password");
            Authentication auth = new UsernamePasswordAuthenticationToken(user,null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            Post post = new Post();
            post.setPublished(false);
            post.setTags(new HashSet<>());
            given(postRepository.findBySlug(anyString())).willReturn(Optional.of(post));

            //then
            assertEquals(postsService.getSinglePost("slug"), new PostDto(post));
        }
    }

    private PostDto generateTestingPostDto() {
        final String title = "title";
        final String slug = "slug";
        final String categoryName = "category";
        final List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        final Instant current = Instant.now();

        PostDto postDto = new PostDto();
        postDto.setTitle(title);
        postDto.setSlug(slug);
        postDto.setPublished(false);
        postDto.setCreateTime(current);
        postDto.setLastEditTime(current);
        postDto.setCategory(categoryName);
        postDto.setTags(tags);

        return postDto;
    }
}