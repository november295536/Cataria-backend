package space.nov29.cataria.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import space.nov29.cataria.dto.CategoryDto;
import space.nov29.cataria.dto.PostDto;
import space.nov29.cataria.dto.TagDto;
import space.nov29.cataria.exception.PostNotFoundException;
import space.nov29.cataria.model.Category;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;
import space.nov29.cataria.repository.CategoryRepository;
import space.nov29.cataria.repository.PostRepository;
import space.nov29.cataria.repository.TagRepository;

import java.security.Principal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostsService {

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final TagRepository tagRepository;

    public PostsService(PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public void createPost(PostDto postDto) {
        Post post = createPostFromPostDta(postDto);
        postRepository.save(post);
    }

    public void updatePost(PostDto postDto) {
        Post post = postRepository
                .findById(postDto.getId())
                .orElseThrow(() -> new PostNotFoundException("id: " + postDto.getId()));
        updatePostByPostDto(postDto, post);
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository
                .findById(id)
                .orElseThrow(() -> new PostNotFoundException("id: " + id));
        postRepository.delete(post);
    }

    public PostDto getSinglePost(String slug) throws PostNotFoundException {
        try {
            Post post = postRepository
                    .findBySlug(slug)
                    .orElse(null);
            if(post == null) post = postRepository
                    .findById(Long.parseLong(slug))
                    .orElseThrow(() -> new PostNotFoundException("slug: " + slug));
            if (post.getPublished()) return new PostDto(post);
            if(!isLogin()) throw new PostNotFoundException("slug: " + slug);
            return new PostDto(post);
        } catch (NumberFormatException e) {
            throw new PostNotFoundException("slug: " + slug);
        }
    }

    public Page<Post> getCategoryPosts(String categoryName, int page, int size) throws PostNotFoundException {
        Category category = categoryRepository
                .findByName(categoryName)
                .orElseThrow(() -> new PostNotFoundException("category: " + categoryName));
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByCategoryAndPublishedTrue(category, pageable);

    }

    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("published").ascending().and(Sort.by("publishedTime").descending()));
        return postRepository.findAll(pageable);
    }

    public Page<Post> getPublishedPost(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByPublishedTrueOrderByPublishedTimeDesc(pageable);
    }

    public List<TagDto> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream().map(TagDto::new).collect(Collectors.toList());
    }

    public List<CategoryDto> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryDto::new).collect(Collectors.toList());
    }

    private Post createPostFromPostDta(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setSlug(postDto.getSlug());
        post.setPublished(postDto.getPublished());
        setTime(postDto, post);
        setCategory(postDto, post);
        setTags(postDto, post);

        return post;
    }

    private void updatePostByPostDto(PostDto postDto, Post post) {
        Instant currentTime = Instant.now();
        post.setLastEditTime(currentTime);
        post.setTitle(postDto.getTitle());
        post.setSlug(postDto.getSlug());
        post.setContent(postDto.getContent());
        setCategory(postDto, post);
        setTags(postDto, post);

        if (!post.getPublished() && postDto.getPublished()) {
            post.setPublishedTime(currentTime);
        }
        post.setPublished(postDto.getPublished());
    }

    private void setTime(PostDto postDto, Post post) {
        boolean publishState = postDto.getPublished();
        Instant createTime = postDto.getCreateTime();
        if (createTime == null) {
            Instant currentTime = Instant.now();
            post.setCreateTime(currentTime);
            post.setLastEditTime(currentTime);
            if (publishState) post.setPublishedTime(currentTime);
        } else {
            post.setCreateTime(postDto.getCreateTime());
            post.setLastEditTime(postDto.getLastEditTime());
            if (publishState) post.setPublishedTime(postDto.getPublishedTime());
        }
    }

    private void setCategory(PostDto postDto, Post post) {
        String categoryName = postDto.getCategory();
        if (categoryName != null && !categoryName.isEmpty()) {
            Category category = categoryRepository.findByName(categoryName).orElse(new Category(categoryName));
            category.addPostToPostList(post);
            return;
        }
        if(post.getCategory() == null) return;
        Category category = post.getCategory();
        category.removePostFromPostList(post);
    }

    private void setTags(PostDto postDto, Post post) {
        List<String> tagNames = postDto.getTags();
        Set<Tag> newTags = getTagsFromTagNameList(tagNames);
        Set<Tag> originalTags = post.getTags();
        if (originalTags == null && newTags == null) return;

        if (newTags == null) {
            newTags = new HashSet<>();
        }

        if (originalTags == null) {
            originalTags = new HashSet<>();
        }

        Set<Tag> addTags = new HashSet<>(newTags);
        addTags.removeAll(originalTags);

        Set<Tag> removeTags = new HashSet<>(originalTags);
        removeTags.removeAll(newTags);

        for (Tag tag : addTags) {
            tag.addPostToPostList(post);
        }

        for (Tag tag : removeTags) {
            tag.removePostFromPostList(post);
        }
    }

    private Set<Tag> getTagsFromTagNameList(List<String> tagNames) {
        if(tagNames == null || tagNames.size() == 0) return null;
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElse(new Tag(tagName));
            tags.add(tag);
        }
        return tags;
    }

    private boolean isLogin() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        return principal instanceof UsernamePasswordAuthenticationToken;
    }
}
