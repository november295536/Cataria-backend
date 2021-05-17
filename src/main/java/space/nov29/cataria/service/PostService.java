package space.nov29.cataria.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.security.Principal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostRespository postRespository;

    @Autowired
    private CategoryRespository categoryRespository;

    public void createPost(PostDto postDto) {
        Post post = createPostFromPostDta(postDto);
        postRespository.save(post);
    }

    public void updatePost(PostDto postDto) {

        Post post = postRespository
                .findById(postDto.getId())
                .orElseThrow(()->new PostNotFoundException("id: "+ postDto.getId()));
        updatePostByPostDto(postDto, post);
        postRespository.save(post);
    }

    public PostDto getPost(String slug) throws PostNotFoundException {
        Post post = postRespository
                .findBySlug(slug)
                .orElse(
                        postRespository
                                .findById(Long.parseLong(slug))
                                .orElseThrow(()->new PostNotFoundException("slug: "+ slug)));
        if(post.getPublished()) return mapFromPostToDto(post);

        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        if(!(principal instanceof UsernamePasswordAuthenticationToken)) throw new PostNotFoundException("slug: "+ slug);
        return mapFromPostToDto(post);
    }

    public List<PostDto> getAllPosts() {
        List<Post> posts = postRespository.findAll();
        return posts.stream().map(this::mapFromPostToDto).collect(Collectors.toList());
    }

    private PostDto mapFromPostToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        String slug = post.getSlug();
        dto.setSlug(Objects.requireNonNullElseGet(slug, () -> Long.toString(post.getId())));
        dto.setContent(post.getContent());
        dto.setLastEditTime(post.getLastEditTime());
        dto.setCreateTime(post.getCreateTime());
        dto.setPublishedTime(post.getPublishedTime());
        dto.setPublished(post.getPublished());

        return dto;
    }

    private Post createPostFromPostDta(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setSlug(postDto.getSlug());
        boolean publishState = postDto.getPublished();
        post.setPublished(publishState);
        String categoryName = postDto.getCategory();
        if(categoryName !=null && !categoryName.isEmpty()) {
            Category category = categoryRespository.findByName(categoryName).orElse(new Category());
            category.setName(categoryName);
            post.setCategory(category);
        }

        Instant createTime = postDto.getCreateTime();
        if(createTime == null) {
            Instant currentTime = Instant.now();
            post.setCreateTime(currentTime);
            post.setLastEditTime(currentTime);
            if(publishState) post.setPublishedTime(currentTime);
            return post;
        }
        post.setCreateTime(postDto.getCreateTime());
        post.setLastEditTime(postDto.getLastEditTime());
        if(publishState) post.setPublishedTime(postDto.getPublishedTime());
        return post;
    }

    private void updatePostByPostDto(PostDto postDto, Post post) {
        Instant currentTime = Instant.now();
        post.setLastEditTime(currentTime);
        post.setTitle(postDto.getTitle());
        post.setSlug(postDto.getSlug());
        post.setContent(postDto.getContent());

        if(!post.getPublished() && postDto.getPublished()) {
            post.setPublishedTime(currentTime);
        }
        post.setPublished(postDto.getPublished());

        String categoryName = postDto.getCategory();
        if(categoryName !=null && !categoryName.isEmpty()){
            Category category = categoryRespository.findByName(categoryName).orElse(new Category());
            category.setName(categoryName);
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }
    }
}
