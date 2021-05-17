package space.nov29.cataria.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import space.nov29.cataria.dto.PostDto;
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
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

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

    public PostDto getPost(String slug) throws PostNotFoundException {
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

    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostDto::new).collect(Collectors.toList());
    }

    public Page<Post> getPostsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if(!isLogin()) {
            return postRepository.findByPublishedTrueOrderByPublishedTimeDesc(pageable);
        }
        return postRepository.findAll(pageable);

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

    private Set<Tag> getTagsFromTagNameList(List<String> tagNames) {
        if(tagNames == null || tagNames.size() == 0) return null;
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElse(new Tag());
            tag.setName(tagName);
            tags.add(tag);
        }
        return tags;
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
            Category category = categoryRepository.findByName(categoryName).orElse(new Category());
            category.setName(categoryName);
            post.setCategory(category);
            return;
        }
        post.setCategory(null);
    }

    private void setTags(PostDto postDto, Post post) {
        List<String> tagNames = postDto.getTags();
        Set<Tag> tags = getTagsFromTagNameList(tagNames);
        post.setTags(tags);
    }

    private boolean isLogin() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        return principal instanceof UsernamePasswordAuthenticationToken;
    }
}
